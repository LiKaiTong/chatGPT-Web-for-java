package com.likaitong.chatgptweb.entity.ResponseEntity;

import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import lombok.Data;

import java.util.List;

/**
 * @author LKT
 * @create 2023-06-17 13:01
 */

@Data
public class ResponsewithDialogMessage {
    int code;
    List<Message> messages;

    public ResponsewithDialogMessage(int code, List<Message> messages) {
        this.code = code;
        this.messages = messages;
    }
}
