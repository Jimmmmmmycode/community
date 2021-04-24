package com.nowcoder.community.aspect;


import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.HostHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);


    @Autowired
    private HostHolder hostHolder;

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){}


    @Before("pointcut()")
    public void before(JoinPoint joinPoint){


        // 在所有service方法前记录日志
        // 用户[10.22.31.45],在[xxx],访问了[com.nowcoder.community.service.xxx()]
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return; // 当kafka队列调Service的时候attributes为空(不是通过http请求访问)
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getRemoteHost();
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String username = "未登录";
            User user =  hostHolder.getUser();
            if(user!=null){
                username = user.getUsername();
            }

            // 知道访问的是哪个类,哪个方法 -- 加joinpoint参数
            String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            logger.info(String.format("用户[%s],[%s],在[%s],访问了[%s].",ip,username,now,target));
        }
    }




