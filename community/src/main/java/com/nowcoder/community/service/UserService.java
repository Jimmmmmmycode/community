package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;  // 发邮件

    @Autowired
    private TemplateEngine templateEngine; // 模版引擎

    @Autowired
    private LoginTicketMapper loginTicketMapper ; // 登录凭证

    // 发邮件生成激活码
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath ;


    public User findUserById(int id){
        return userMapper.selectById(id);
    }





    /***
     * 注册方法(用户点击注册时访问这个方法
     * @param user-用户填好表单后封装成一个User对象传给方法
     * @return 以HashMap返回用户注册时可能存在的错误信息
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        // 空值处理
        if(user==null){
            throw new IllegalArgumentException("参数不能为空!");
        }

        if(StringUtils.isBlank(user.getUsername())) { // 用户填写的用户名为空
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) { // 用户填写的用户名为空
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) { // 用户填写的用户名为空
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }

        // 判断账号是否已存在,邮箱是否已被注册
        // 验证账号

        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在!");
            return map ;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册!");
            return map ;
        }

        // 注册用户,把用户信息写到数据库里

        // 设置加密盐，生成随机字符串,用前五位作为密码盐
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        // 密码加盐后进行Md5加密
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0); // 普通用户
        user.setStatus(0); // 未激活
        // 给未激活用户发送激活邮件需要激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 给用户设置随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        // 设置创建时间
        user.setCreateTime(new Date());

        userMapper.insertUser(user) ;

        // 发激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // 自定义激活 url: http://localhost:8080/community/activation/101/code
        // 把激活路径动态的拼出来
        // 插入User信息后Mybatis对user id 进行了回填（mybatis.configuration.useGeneratedKeys = true）

        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context); // process(模版路径,需要传递给页面的参数)
        mailClient.sendMail(user.getEmail(),"您正在激活一个账号",content);

        return map;
    }



    // 激活账号的业务方法

    /**
     * @param userId - 激活链接中的userid
     * @param code - 激活链接中的激活码
     * @return
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT; // 重复的激活
        }
        else if(user.getActivationCode().equals(code)){  // 链接激活码和用户原本的激活码相同
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }



    // 在UserService里写关于登录的方法
    /**
     *
     * @param username -用户名
     * @param password -明文密码,需要先进行mds5加密再和password比对
     * @param expireSeconds -过期时间
     * @return 若成功登陆,返回空,否则用Map返回登陆不成功的原因
     */
    public Map<String,Object> login(String username,String password,int expireSeconds){
        Map<String,Object> map = new HashMap<>();
        // 输入空值处理 / 合法性判断
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map ;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map ;
        }
        // 验证账号 (判断用户输入的账号是否可以登录)
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        // 判断账号是否激活
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活！");
            return map;
        }
        // 验证密码 (先对明文密码进行md5加密[记得加盐！)
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确!");
            return map;
        }
        // 生成登录凭证 , 并往数据库里存
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0); // 有效的状态
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expireSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }


    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }


}
