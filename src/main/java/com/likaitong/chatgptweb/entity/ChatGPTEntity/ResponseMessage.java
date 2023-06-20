package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

import java.util.List;

/**
 * @author LKT
 * @create 2023-05-23 15:52
 */

@Data
public class ResponseMessage {
    String id;
    String object;
    long created;
    String model;
    Usage usage;
    List<Choice> choices;

}

//{
//        "id":"chatcmpl-7JGq0OWNn54vO2kwENLpDO71ilAJq",
//        "object":"chat.completion",
//        "created":1684827312,
//        "model":"gpt-3.5-turbo-0301",
//        "usage":{"prompt_tokens":10,"completion_tokens":9,"total_tokens":19},
//        "choices":[
//                    {
//                        "message":
//                                {
//                                    "role":"assistant",
//                                    "content":"Hello! How may I assist you today?"
//                                },
//                        "finish_reason":"stop",
//                        "index":0
//                    }
//                ]
//}