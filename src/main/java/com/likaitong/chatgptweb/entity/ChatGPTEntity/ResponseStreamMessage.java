package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

import java.util.List;

/**
 * @author LKT
 * @create 2023-05-24 9:40
 */

@Data
public class ResponseStreamMessage {
    String id;
    String object;
    long created;
    String model;
    List<ChoiceStream> choices;
}
