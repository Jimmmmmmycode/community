package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService ;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId); // 链接详情页要用到

        if(comment.getEntityType()==ENTITY_TYPE_POST){ // 给帖子的评论
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId()); // 帖子的作者
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){ // 给评论的评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId()); // 评论的作者
        }

        eventProducer.fireEvent(event); // 扔到消息队列就不管了

        return "redirect:/discuss/detail/"+discussPostId;
    }







}
