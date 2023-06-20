package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

/**
 * @author LKT
 * @create 2023-05-23 15:56
 */

@Data
public class Usage {
    int prompt_tokens;
    int completion_tokens;
    int total_tokens;
}
