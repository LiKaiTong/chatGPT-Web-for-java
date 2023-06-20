package com.likaitong.chatgptweb.entity.DaoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LKT
 * @create 2023-06-01 9:40
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogueInfo {
    int id;
    String dialogueId;
    String dialogueName;

    public DialogueInfo(String dialogueId, String dialogueName) {
        this.dialogueId = dialogueId;
        this.dialogueName = dialogueName;
    }
}
