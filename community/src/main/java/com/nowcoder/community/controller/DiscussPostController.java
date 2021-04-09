package com.nowcoder.community.controller;


// 处理和帖子相关的业务的Controller

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.*;


@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    HostHolder hostHolder ; // 获取当前用户

    @Autowired
    UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService ;

    @RequestMapping(path="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){

        User user = hostHolder.getUser();
        if(user==null){
            // 给页面返回提示 - 异步的,json格式的数据
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        // 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }




    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    // 返回模版路径
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user );

        // 帖子点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);

        // 点赞状态(没登录不显示已赞,登录了才显示已赞)
        int likeStatus = hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);


        // 设置分页信息
        page.setLimit(5); // 每页显示5条
        page.setPath("/discuss/detail/"+discussPostId); // 当前页面的路径
        page.setRows(post.getCommentCount()); // 一共有多少条评论数据


        // 评论:给帖子的评论
        // 回复:给评论的评论
        List<Comment> commentList =  commentService.findCommentByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit() );
        // 评论VO列表
        List<Map<String,Object>> commentVoList  = new ArrayList<>(); // 封装显示对象
        if(commentList!=null){
            for(Comment comment:commentList){
                // 一条评论的VO
                Map<String,Object> commentVo = new HashMap<>();

                // 评论
                commentVo.put("comment",comment);
                // 评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));  // 通过userid得到User相关信息

                // 帖子点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);

                // 点赞状态(没登录不显示已赞,登录了才显示已赞)
                likeStatus = hostHolder.getUser()==null?0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);


                // 一条评论的回复列表
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE ); // 不做分页,有多少条查多少条
                // 回复的VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply",reply);
                        // 回复的作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        // 处理回复的目标
                        User target = reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        // 帖子点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);

                        // 点赞状态(没登录不显示已赞,登录了才显示已赞)
                        likeStatus = hostHolder.getUser()==null?0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);



                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList); // 一条评论下的所有回复
                // 查询一条评论下回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments",commentVoList);


        return "site/discuss-detail";
    }



}
