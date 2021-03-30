package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串 （验证码 / 上传文件的文件名）

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");

    }

    // MD5加密 (对注册时的密码进行加密)
    // hello + e34a8(salt) ->
    // key 待进行md5加密的字符串
    public static String md5(String key){
        // 用commons.lang3包判定字符串是否为空
        if(StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes()) ;   // Spring自带的加密工具

    }
}
