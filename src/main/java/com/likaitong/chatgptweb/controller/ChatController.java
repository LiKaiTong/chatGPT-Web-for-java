package com.likaitong.chatgptweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.RequestMessage;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueInfo;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueMessage;
import com.likaitong.chatgptweb.entity.ResponseEntity.ResponseInfo;
import com.likaitong.chatgptweb.entity.ResponseEntity.SuccessResponse;
import com.likaitong.chatgptweb.service.ChatService;
import com.likaitong.chatgptweb.service.ChatStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LKT
 * @create 2023-05-24 16:28
 */

@Controller
public class ChatController {
    @Autowired
    ChatService chatService;

    @Autowired
    ChatStreamService chatStreamService;

    @RequestMapping("/")
    public String index(){
        return "chatStream";
    }


    @RequestMapping("/chatStream")
    public String getChatStreamPage(){
        return "chatStream";
    }


    @RequestMapping("/chatStream/postUserContent")
    @ResponseBody
    public String postUserContent(@RequestBody DialogueMessage dialogueMessage){
        return chatStreamService.postUserContent(dialogueMessage);
    }


    @RequestMapping(value = "/chatStream/getChatResponseStream",produces="text/event-stream;charset=utf-8")
    @ResponseBody
    public void getChatResponseStream(HttpServletResponse response,@RequestParam String dialogueId) throws IOException {
        response.setContentType("text/event-stream;charset=utf-8");
        PrintWriter writer = response.getWriter();
        chatStreamService.sendChatResponseStream(writer,dialogueId);
    }

    @RequestMapping("/chatStream/getDialogue")
    @ResponseBody
    public String getDialogue(@RequestParam String dialogueId){
        return chatStreamService.getDialogue(dialogueId);
    }

    @RequestMapping("/chatStream/getAllDialogueInfo")
    @ResponseBody
    public String getAllDialogueInfo(){
        return chatStreamService.getAllDialogueInfo();
    }


    @RequestMapping("/chatStream/deleteDialogue")
    @ResponseBody
    public String deleteDialogue(@RequestParam String dialogueId){
        return chatStreamService.deleteDialogue(dialogueId);
    }


    @RequestMapping("/chatStream/createNewDialogue")
    @ResponseBody
    public String createNewDialogue(){
        return chatStreamService.createNewDialogue();
    }


    @RequestMapping("/chatStream/getCurDialogueId")
    @ResponseBody
    public String getCurDialogueId(){
        return chatStreamService.getCurDialogueId();
    }

    @RequestMapping("/chatStream/renameDialogue")
    @ResponseBody
    public String renameDialogue(@RequestBody DialogueInfo dialogueInfo){
        return chatStreamService.renameDialogue(dialogueInfo);
    }

}
