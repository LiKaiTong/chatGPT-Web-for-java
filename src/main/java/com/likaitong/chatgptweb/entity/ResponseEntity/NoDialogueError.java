package com.likaitong.chatgptweb.entity.ResponseEntity;

/**
 * @author LKT
 * @create 2023-06-16 9:35
 */
public class NoDialogueError extends ResponseInfo{
    public NoDialogueError(){
        super(410,"The current dialogue does not exist !");
    }
}
