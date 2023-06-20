package com.likaitong.chatgptweb.service;

import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;

/**
 * @author LKT
 * @create 2023-05-25 12:26
 */
public interface ChatService {
    public Message getChatMessage(String userContent);
}
