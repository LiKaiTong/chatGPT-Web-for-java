package com.likaitong.chatgptweb.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.likaitong.chatgptweb.entity.ChatGPTEntity.Message;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueInfo;
import com.likaitong.chatgptweb.entity.DaoEntity.DialogueMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LKT
 * @create 2023-05-30 14:25
 */

@Repository
public interface GPTDao extends BaseMapper<DialogueMessage> {
    List<DialogueInfo> getAllDialogueInfo();
    List<Message> getMessageByDiaId(String diaId);

    int insertMessage(DialogueMessage diaMess);
    int insertDialogue(DialogueInfo dialogueInfo);

    int updateDialogueName(DialogueInfo dialogueInfo);

    int deleteMessageByDiaId(String diaId);
    int deleteDialogueByDiaId(String diaId);
}
