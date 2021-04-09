package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    // 服务器返回的json字符串
    public static String getJSONString(int code, String msg,Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for(String key:map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>(); // 用一个map模拟点业务数据
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println( getJSONString(0,"ok", map) );
    }




}
