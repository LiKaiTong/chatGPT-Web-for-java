package com.likaitong.chatgptweb.entity.LockEntity;

/**
 * @author LKT
 * @create 2023-06-05 10:27
 * A session lock that locks a dialogue before getting a GPT reply when the user sends that message
 */


public class DialogueLock {
    private final String dialogueId;
    private volatile boolean chatStatus=false;

    public DialogueLock(String dialogueId) {
        this.dialogueId = dialogueId;
    }

    public String getDialogueId(){
        return this.dialogueId;
    }

    public boolean isChatting(){
        return chatStatus;
    }

    /**
     *  lock the dialogue
     * @return the result of lock
     */
    public boolean lockDialogue(){
        if(!chatStatus){
            synchronized (this){
                if(!chatStatus){
                    chatStatus=true;
                    return true;
                }else {
                    return false;
                }
            }
        }else {
            return false;
        }
    }

    public synchronized void unlockDialogue(){
        chatStatus=false;
    }
}
