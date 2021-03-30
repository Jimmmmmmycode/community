package com.nowcoder.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import com.nowcoder.community.util.MailClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;  // 把util中的client组件注入到这个测试类中

    @Autowired
    private TemplateEngine templateEngine ;


    @Test
    // 测试发送一个普通的邮件 ( 非html邮件 )
    public void testTextMail(){
        mailClient.sendMail("1205888142@qq.com","TEST","Welcome to Jimmy's community");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","jimmy");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("1205888142@qq.com","HTML",content);
    }



}
