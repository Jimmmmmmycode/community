package com.nowcoder.community.controller;


import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired  // 需要调业务层处理请求
    private UserService userService;

    @Autowired  // 生成验证码
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";  // 返回模版路径
    }

    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login" ;
    }  // 给浏览器返回登录页面的html

    // 填写表单发送的是Post请求(浏览器向服务器提交数据)
    @RequestMapping(path="/register",method=RequestMethod.POST)
    // 用User接收注册时的邮箱,账号,密码
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user) ; // 调用Service层的注册函数
        // 注册成功 - 跳到首页
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，已向您的邮箱发送了一封激活邮件,请尽快激活！");
            model.addAttribute("target","/index"); // 跳到首页
            return "/site/operate-result";
        }
       // 用户填写的信息不符合要求,注册失败
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register" ;
        }

    }

    //  http://localhost:8080/community/activation/101/code
    @RequestMapping(path="activation/{userId}/{code}",method =RequestMethod.GET)

    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
       int result =  userService.activation(userId,code);
       if(result == ACTIVATION_SUCCESS){
           model.addAttribute("msg","激活成功,您的账号已经可以正常使用了");
           model.addAttribute("target","/login");
       }else if(result==ACTIVATION_REPEAT){
           model.addAttribute("msg","无效操作,该账号已经激活过了");
           model.addAttribute("target","/index");
       }else{
           model.addAttribute("msg","激活失败,您提供的激活码不正确");
           model.addAttribute("target","/index");

       }
        return "/site/operate-result";   // 跳转到操作结果页面
    }
    // 浏览器在收到html中的图片路径时，向服务器发送请求获取图片
    @RequestMapping(path="/kaptcha",method=RequestMethod.GET)
    // 返回值 void - 向浏览器输出的是图片 ,不是字符串也不是网页,自己用Response对象手动向浏览器输出
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码时服务端需要记住,当浏览器再次访问服务器时好验证验证码对不对(在多个请求间使用)
        // 不能存在浏览器端,否则很容易被盗取
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        session.setAttribute("kaptcha",text); // 在session中也是以key-value对的形式保存
        // 将图片输出给浏览器
        response.setContentType("image/png");  // 声明响应返回内容的格式
        try {
            OutputStream os = response.getOutputStream();  // 获取输出流 - 图片 - 字节流
            ImageIO.write(image,"png",os); // 向浏览器输出图片,输出格式png,输出流 os
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }
    /**
     * @param username
     * @param password
     * @param code
     * @param rememberme   // 表单提交信息,用户名,密码,验证码,记住我
     * @param model        // 向视图传递数据
     * @param session      // 获取存入服务器session的验证码
     * @param response     // 用来给浏览器发送cookie
     * @return
     */
    @RequestMapping(path="/login",method =RequestMethod.POST) // 处理表单过来的数据
    public String login( String username,String password,String code,boolean rememberme,
                         Model model , HttpSession session,HttpServletResponse response){
        // 检查验证码
        String kapthca = (String) session.getAttribute("kaptcha"); // 强制类型转换
        if(StringUtils.isBlank(kapthca)||StringUtils.isBlank(code)||!kapthca.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确!");
            return "site/login";
        }
        // 检查账号，密码
        int expiredSeconds = rememberme ?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){  // 如果登录业务返回的map包含ticket,表示成功,否则即是失败了
             Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
             cookie.setPath(contextPath);
             cookie.setMaxAge(expiredSeconds);
             response.addCookie(cookie);
             return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }
    @RequestMapping(path="/logout",method=RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login" ; // 重定向是默认是get请求
    }
}
