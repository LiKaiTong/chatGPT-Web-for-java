package com.likaitong.chatgptweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.likaitong.chatgptweb.config.GPTConfig;
import com.likaitong.chatgptweb.dao.GPTDao;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.RequestMessage;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.ResponseError;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.ResponseStreamMessage;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueInfo;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueMessage;
import com.likaitong.chatgptweb.entity.LockEntity.DialogueLock;
import com.likaitong.chatgptweb.entity.ResponseEntity.ChatOngoingError;
import com.likaitong.chatgptweb.entity.ResponseEntity.NoDialogueError;
import com.likaitong.chatgptweb.entity.ResponseEntity.ResponsewithDialogMessage;
import com.likaitong.chatgptweb.entity.ResponseEntity.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LKT
 * @create 2023-05-25 19:10
 */

@Service
@DependsOn({"GPTDao","GPTConfig"})
public class ChatStreamServiceImpl implements ChatStreamService {

    String httpUrl="";
    String apiKey="";
    String model="";

    Encoding enc =null;
    int max_token=4*1024;
    float completionRate=0.25f;
    int max_completion=1024;


    ConcurrentHashMap<String,List<Message>> dialogueMap=new ConcurrentHashMap<>();
    ConcurrentHashMap<String, DialogueLock> dialogueLockMap=new ConcurrentHashMap<>();

    volatile String curDialogueId="no-dialogueId";


    @Autowired
    GPTDao gptDao;

    @Autowired
    GPTConfig gptConfig;

    /**
     * Initialize the service, if there are no current dialogue, a new default dialogue is created
     */
    @PostConstruct
    public void init(){
        // set gpt config
        httpUrl=gptConfig.getUrl();
        apiKey=gptConfig.getApiKey();
        model=gptConfig.getModel();

        // set token number
        if(model.indexOf("16k")>0){
            max_token=16*1024;
            max_completion=(int)(completionRate*max_token);
        }else if(model.indexOf("32k")>0){
            max_token=32*1024;
            max_completion=(int)(completionRate*max_token);
        }

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        enc = registry.getEncoding(EncodingType.CL100K_BASE);

        // Initialize the dialogs
        List<DialogueInfo> dialogueInfos = gptDao.getAllDialogueInfo();
        if(dialogueInfos.size()==0){
            createNewDialogue();
        }else {
            for (DialogueInfo dialogueInfo : dialogueInfos) {
                String curDiaId=dialogueInfo.getDialogueId();
                List<Message> messageList = gptDao.getMessageByDiaId(curDiaId);
                dialogueMap.put(curDiaId,messageList);
                dialogueLockMap.put(curDiaId,new DialogueLock(curDialogueId));
            }
        }
        curDialogueId=dialogueInfos.get(0).getDialogueId();
    }

    /**
     * post user message
     * @param dialogueMessage
     * @return
     */
    @Override
    public String postUserContent(DialogueMessage dialogueMessage) {
        // check id
        String diaId=dialogueMessage.getDialogueId();
        if(!dialogueMap.containsKey(diaId)){
            return JSON.toJSONString(new NoDialogueError());
        }

        // try get the dialogue lock
        boolean isChatting=false;
        DialogueLock lock=dialogueLockMap.get(diaId);
        while (true){
            if(!lock.isChatting()){
                boolean tryLock = lock.lockDialogue();
                if(tryLock){
                    break;
                }
            }
            isChatting=true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message message=new Message(dialogueMessage.getRole(),dialogueMessage.getContent());

        if(dialogueMap.containsKey(diaId)){
            dialogueMap.get(diaId).add(message);
        }else {
            return JSON.toJSONString(new NoDialogueError());
        }


        if(isChatting){
            return JSON.toJSONString(new ChatOngoingError());
        }else {
            return JSON.toJSONString(new SuccessResponse());
        }


    }

    /**
     * send the errors with SSE
     * @param writer
     * @param message
     */
    private void sseErrorSend(PrintWriter writer,String message){
        writer.write("data: {}"+"\n\n");
        writer.flush();
        writer.write("data: [ERROR] "+message+"\n\n");
        writer.flush();
        writer.write("data: [DONE]"+"\n\n");
        writer.flush();
    }

    /**
     * Limit the number of tokens that send messages
     * @param messages
     * @return
     */
    private List<Message> tokenLimitMessage(List<Message> messages){
        List<Message> sendMessages=new ArrayList<>();
        int curTokenNum=0;
        for(int i=messages.size()-1;i>=0;i--){
            int curMessageTokenNum=enc.countTokens(messages.get(i).getContent());
            if(curTokenNum+curMessageTokenNum<max_token-max_completion){
                sendMessages.add(0,messages.get(i));
                curTokenNum+=curMessageTokenNum;
            }else {
                break;
            }
        }
        return sendMessages;
    }

    /**
     * # refer to https://platform.openai.com/docs/api-reference/chat/create
     * @param writer
     * @param dialogueId
     */
    @Override
    public  void sendChatResponseStream(PrintWriter writer,String dialogueId) {
        List<Message> messages=dialogueMap.get(dialogueId);
        DialogueLock lock=dialogueLockMap.get(dialogueId);

        // create url and proxy
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid URL !");
            sseErrorSend(writer,"Invalid URL !");
            return;
        }

        Proxy proxy=null;
        if(gptConfig.isProxy()){
            try {
                proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",gptConfig.getProxyPort()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                System.out.println("Illegal proxy argument !");
                sseErrorSend(writer,"Illegal proxy argument !");
                return;
            }
        }

        boolean requestSucceed=false;
        try {
            // connect
            HttpURLConnection connection=null;

            if(gptConfig.isProxy()&&null!=proxy){
                connection = (HttpURLConnection) url.openConnection(proxy);
            }else {
                connection=(HttpURLConnection)url.openConnection();
            }

            // set request head
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+apiKey);
            connection.setDoOutput(true);

            // Create the request body of this request
            RequestMessage requestMessage=new RequestMessage(model,tokenLimitMessage(messages), true);

            // send
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = JSONObject.toJSONString(requestMessage).getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }catch (SocketException e){
                e.printStackTrace();
                sseErrorSend(writer,"SocketException! Please check your proxy or other network setting!");
                return;
            }

            // response code
            int responseCode = connection.getResponseCode();

            BufferedReader reader=null;
            // success
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String responseMessDelta;
                StringBuilder responseMess=new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    writer.write(line+"\n\n");
                    writer.flush();
                    int length=line.length();
                    int start=line.indexOf('{');
                    if(start>0){
                        ResponseStreamMessage responseStreamMessage = JSON.parseObject(line.substring(start, length), ResponseStreamMessage.class);
                        responseMessDelta=responseStreamMessage.getChoices().get(0).getDelta().getContent();
                        if(responseMessDelta!=null){
                            responseMess.append(responseMessDelta);
                        }
                    }
                }

                requestSucceed=true;
                gptDao.insertMessage(new DialogueMessage(dialogueId,"user",messages.get(messages.size()-1).getContent()));
                messages.add(new Message("assistant",responseMess.toString()));
                gptDao.insertMessage(new DialogueMessage(dialogueId,"assistant",responseMess.toString()));
            }
            // error
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder responseErrorJSON = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.flush();
                    responseErrorJSON.append(line);
                }
                ResponseError responseError = JSONObject.parseObject(responseErrorJSON.toString(), ResponseError.class);
                System.out.println("ERROR! "+responseError.getError().getMessage());
                sseErrorSend(writer,responseError.getError().getMessage());
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                writer.write("data: [DONE]"+"\n\n");
                writer.flush();
                writer.close();
            }

            if(!requestSucceed){
                messages.remove(messages.size()-1);
            }

            lock.unlockDialogue();
        }
    }

    /**
     * get dialogue messagelist by id
     * @param dialogueId
     * @return
     */
    @Override
    public String getDialogue(String dialogueId) {
        if(dialogueMap.containsKey(dialogueId)){
            List<Message> messages = dialogueMap.get(dialogueId);
            curDialogueId=dialogueId;
            return JSON.toJSONString(new ResponsewithDialogMessage(200,messages));
        }
        else {
            return JSON.toJSONString(new NoDialogueError());
        }
    }

    /**
     * get all dialogue id and name
     * @return
     */
    @Override
    public String getAllDialogueInfo() {
        String res=JSON.toJSONString(gptDao.getAllDialogueInfo());
        return res;
    }

    /**
     * delete dialogue by id
     * @param dialogueId
     * @return
     */
    @Override
    public String deleteDialogue(String dialogueId) {
        // check
        if(!dialogueLockMap.containsKey(dialogueId)){
            return JSON.toJSONString(new NoDialogueError());
        }

        DialogueLock lock=dialogueLockMap.get(dialogueId);
        if(lock.isChatting()){
            return JSON.toJSONString(new ChatOngoingError());
        }

        // get old dialog index
        List<DialogueInfo> oldDialogueInfos = gptDao.getAllDialogueInfo();
        int index=0;
        for(int i=0;i<oldDialogueInfos.size();i++){
            if(oldDialogueInfos.get(i).getDialogueId().equals(dialogueId)){
                index=i;
                break;
            }
        }

        //delete message and lock
        dialogueMap.remove(dialogueId);
        dialogueLockMap.remove(dialogueId);

        gptDao.deleteMessageByDiaId(dialogueId);
        gptDao.deleteDialogueByDiaId(dialogueId);


        if(dialogueMap.isEmpty()){
            createNewDialogue();
        }

        // set current dialog id
        List<DialogueInfo> allDialogueInfo = gptDao.getAllDialogueInfo();
        if(allDialogueInfo.size()<=index){
            curDialogueId=allDialogueInfo.get(allDialogueInfo.size()-1).getDialogueId();
        }else {
            curDialogueId=allDialogueInfo.get(index).getDialogueId();
        }

        return JSON.toJSONString(new SuccessResponse());
    }

    /**
     * create a new dialogue
     * @return
     */
    @Override
    public String createNewDialogue(){
        String newDiaId= UUID.randomUUID().toString();
        DialogueInfo newDialogueInfo=new DialogueInfo(newDiaId,"默认对话");
        gptDao.insertDialogue(newDialogueInfo);
        dialogueMap.put(newDiaId,new ArrayList<>());
        dialogueLockMap.put(newDiaId,new DialogueLock(newDiaId));
        curDialogueId=newDiaId;
        return JSON.toJSONString(newDialogueInfo);
    }

    /**
     * get current dialogue id
     * @return
     */
    @Override
    public String getCurDialogueId() {
        if(dialogueMap.containsKey(curDialogueId)){
            return curDialogueId;
        }else {
            List<DialogueInfo> allDialogueInfo = gptDao.getAllDialogueInfo();
            curDialogueId=allDialogueInfo.get(0).getDialogueId();
            return curDialogueId;
        }
    }

    /**
     * rename a dialogue
     * @param dialogueInfo
     * @return
     */
    @Override
    public String renameDialogue(DialogueInfo dialogueInfo) {
        int res=gptDao.updateDialogueName(dialogueInfo);
        if(res==0){
            return JSON.toJSONString(new NoDialogueError());
        }else {
            return JSON.toJSONString(new SuccessResponse());
        }
    }
}
