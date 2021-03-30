package com.nowcoder.community.controller;


import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService; // 查到主页帖子相关信息

    @Autowired
    private UserService userService; // 根据userid查到user相关信息

    @RequestMapping(path="/index",method= RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        // DispatchServlet会把request的数据给model和page？
        // 方法调用前，SpringMVC会自动实例化Model和Page,并将Page注入给Model
        // 自动把page装到model里
        // 所以,在thymeledf中可以直接访问Page对象中的数据

        page.setRows(discussPostService.findDiscussPostsRows(0));  // 数据库中帖子数量总行数
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getoffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list!=null){
            // 用discusspost中的userid查询user完整信息并把它与原来的信息通过hashmap整合起来
            for(DiscussPost post:list){

                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);

            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

}
