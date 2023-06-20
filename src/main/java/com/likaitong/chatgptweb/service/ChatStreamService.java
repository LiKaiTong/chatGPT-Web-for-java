package com.likaitong.chatgptweb.service;

import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueInfo;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueMessage;

import java.io.PrintWriter;

/**
 * @author LKT
 * @create 2023-05-25 19:08
 */
public interface ChatStreamService {

    public String postUserContent(DialogueMessage dialogueMessage);
    public void sendChatResponseStream(PrintWriter writer,String dialogueId);
    public String getDialogue(String dialogueId);
    public String getAllDialogueInfo();
    public String deleteDialogue(String dialogueId);
    public String createNewDialogue();
    public String getCurDialogueId();
    public String renameDialogue(DialogueInfo dialogueInfo);
}
