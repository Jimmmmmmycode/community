package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration // 表示该类是个配置类

public class AlphaConfig {

    @Bean // 装配第三方的Bean需要Bean注解
    // 把java自带的simpleDateFormat ( 该simpleDateFormat返回的对象将被装配到容器里 )
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 把SimpleDateFormate实例化一次，装配到bean里
    }








}
