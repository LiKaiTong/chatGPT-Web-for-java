package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LKT
 * @create 2023-05-23 16:17
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessage {
    String model;
    List<Message> messages;
    boolean stream;
}
