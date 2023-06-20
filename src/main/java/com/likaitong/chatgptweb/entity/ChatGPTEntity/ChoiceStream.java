package com.likaitong.chatgptweb.entity.ChatGPTEntity;

import lombok.Data;

/**
 * @author LKT
 * @create 2023-05-24 9:41
 */

@Data
public class ChoiceStream {
    Delta delta;
    int index;
    String finish_reason;
}
