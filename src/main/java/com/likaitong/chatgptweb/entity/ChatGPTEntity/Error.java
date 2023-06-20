package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

/**
 * @author LKT
 * @create 2023-06-02 14:58
 */

@Data
public class Error {
    String message;
    String type;
    String param;
    String code;
}
