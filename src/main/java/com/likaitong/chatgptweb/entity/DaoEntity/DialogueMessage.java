package com.likaitong.chatgptweb.entity.DaoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LKT
 * @create 2023-05-30 14:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogueMessage {
    int id;
    String dialogueId;
    String role;
    String content;

    public DialogueMessage(String dialogueId, String role, String message) {
        this.dialogueId = dialogueId;
        this.role = role;
        this.content = message;
    }
}
