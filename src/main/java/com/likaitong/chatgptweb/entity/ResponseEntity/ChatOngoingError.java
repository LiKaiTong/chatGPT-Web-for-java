package com.likaitong.chatgptweb.entity.ResponseEntity;

/**
 * @author LKT
 * @create 2023-06-16 9:51
 */
public class ChatOngoingError extends ResponseInfo {
    public ChatOngoingError(){
        super(411,"Current dialogue is chatting !");
    }
}
