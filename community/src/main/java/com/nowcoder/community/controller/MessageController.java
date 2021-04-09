package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService ;

    @Autowired
    private HostHolder hostHolder;
    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 查询聊天列表
        List<Message> conversationList = messageService.
                findConversations(user.getId(),page.getOffset(),page.getLimit());
        
        // 获取其他信息,比如每个聊天总消息,每个聊天有多少条未读消息,与当前用户对话的人的头像以及名字等等
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList!=null){
            for(Message message:conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId = user.getId() == message.getFromId()?message.getToId():message.getFromId();  // 总是获取给我发消息的人的id
                map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        // 查询一个用户全部未读消息数
        // conversationId等于null,体现了动态sql的好处
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters) ;

        // 查询私信目标
         model.addAttribute("target",getLetterTarget(conversationId));

        // 提取私信列表未读消息,改成已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

         return "/site/letter-detail";
    }



    // 判断conversationId哪一段和当前登录用户不一样,就是目标
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId()==id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(!letterList.isEmpty()){
            for(Message message:letterList){
                if(hostHolder.getUser().getId()==message.getToId() && message.getStatus() == 0){ // 只有当消息不是我发的且状态为0的时候,才是一条未读消息
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }


    @RequestMapping(path="/letter/send",method =RequestMethod.POST)
    @ResponseBody  // 请求是异步的,服务器响应json格式的消息
    public String sendLetter(String toName,String content){

        // 根据用户名找到要发私信的目标用户
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在!");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }



/**
    @RequestMapping(path="/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();


    }
**/

}
