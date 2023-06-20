package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LKT
 * @create 2023-05-23 15:57
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    String role;
    String content;
}
