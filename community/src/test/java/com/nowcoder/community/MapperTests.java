package com.nowcoder.community;


import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)  // 加上注解后Test中运行的代码以CommunityApplication为配置类
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper ;

    @Test
    public void testSelectUser(){
        User user =  userMapper.selectById(101);
        System.out.println(user);

        user =  userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

        user =  userMapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user);
    }

    @Test
    public void testUpdate(){
        int rows= userMapper.updateStatus(150,1);
        System.out.println(rows);
        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows = userMapper.updatePassword(150,"1234567");
        System.out.println(rows);
    }


    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10); // 第一行10条数据
        for(DiscussPost post:list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);

    }


    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10)); // 设置过期时间
        loginTicketMapper.insertLoginTicket(loginTicket);
        loginTicket.setUserId(101) ;
    }


    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }




}
