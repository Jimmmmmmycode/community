package com.nowcoder.community.config;


import com.nowcoder.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class  WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

 //   @Autowired
 //   private LoginRequiredInterceptor loginRequiredInterceptor;
 //   登录检查拦截器被废置

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/static/*.css","/static/*.js","/static/*.jpeg","/static/*.jpg","/static/*.png");

  //     registry.addInterceptor(loginRequiredInterceptor)
  //             .excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");

        registry.addInterceptor(messageInterceptor).excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");

        registry.addInterceptor(dataInterceptor).excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");

    }
}
