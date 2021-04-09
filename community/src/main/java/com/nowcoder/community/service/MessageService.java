package com.nowcoder.community.service;


import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper ;

    @Autowired
    private SensitiveFilter sensitiveFilter;



    // 查出一个用户的所有会话信息(返回最近的一条私信)
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }
    
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    // 找到一个对话的所有私信
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    // 查找未读消息数量(总的以及单个对话的)
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    // 新增消息
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }


    // 读取消息
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1) ; // 把状态改为已读
    }


    // 查询最新的通知
    public Message findLatestNotice(int userId,String topic){
        return messageMapper.selectLatestNotice(userId,topic);
    }

    // 查询通知数量
    public int findNoticeCount(int userId,String topic){
        return messageMapper.selectNoticeCount(userId,topic);
    }

    // 查询未读通知
    public int findNoticeUnreadCount(int userId,String topic){
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }
}
