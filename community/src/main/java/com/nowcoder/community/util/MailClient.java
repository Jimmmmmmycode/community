package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class); // 记录日志

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")   // 通过spring.mail.username key值 把username注入到当前的bean中
    private String from;



    /***
     * @param to  目的者
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();  // 用mailSender创建了一个MimeMessage对象
            // MimeMessageHelper可帮助构建MimeMessage对象
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true); // 支持html文本
            mailSender.send(helper.getMimeMessage()); // 从helper中取得构建好的message

        } catch (MessagingException e) {
            logger.error("发送邮件失败");   // 发生错误时记录日志方便日后排错

        }
    }

}
