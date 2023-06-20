package com.likaitong.chatgptweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.RequestMessage;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.ResponseMessage;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;



/**
 * @author LKT
 * @create 2023-05-25 12:28
 */

@Service
public class ChatServiceImpl implements ChatService {

    String httpUrl="https://api.openai.com/v1/chat/completions";
    String apiKey="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";
    String model="gpt-3.5-turbo";

    private List<Message> messageList=new ArrayList<>();

    @Override
    public Message getChatMessage(String userContent) {
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",7890));
        messageList.add(new Message("user",userContent));
        RequestMessage requestMessage=new RequestMessage(model,messageList,false);


        ResponseMessage responseMessage=null;

        boolean getResponse=false;
        int retryCount=3;
        String errorMes=null;

        while(!getResponse&&retryCount>0){
            retryCount--;
            try {
                //创建连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer "+apiKey);
                connection.setDoOutput(true);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] input = JSONObject.toJSONString(requestMessage).getBytes("utf-8");
                    outputStream.write(input, 0, input.length);
                }


                int responseCode = connection.getResponseCode();
//                System.out.println(responseCode);
                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    getResponse=true;
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }


                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if(!getResponse&&retryCount!=0) {
                    continue;
                }
                if(!getResponse){
                    System.out.print("Error!  ");
                    errorMes=response.toString();
                    break;
                }


                responseMessage= JSON.parseObject(response.toString(),ResponseMessage.class);
                messageList.add(responseMessage.getChoices().get(0).getMessage());
//                System.out.println("GPT: "+responseMessage.getChoices().get(0).getMessage().getContent());
                return responseMessage.getChoices().get(0).getMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new Message("assistant",errorMes);
    }
}
