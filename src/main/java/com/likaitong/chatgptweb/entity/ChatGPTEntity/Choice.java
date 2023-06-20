package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

/**
 * @author LKT
 * @create 2023-05-23 15:59
 */

@Data
public class Choice {
    Message message;
    String finish_reason;
    int index;
}
