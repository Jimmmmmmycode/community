package com.nowcoder.community.controller.interceptor;



import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){ // 判断拦截的对象是否为方法
            HandlerMethod handlerMethod =(HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class); // 获取方法中的注解,若注解不等于空,说明该方法需要登录才可以访问
            if(loginRequired!=null && hostHolder.getUser()==null) {  // 在未登录的情况下访问了需要登录的方法
                response.sendRedirect(request.getContextPath()+"/login"); // 重定向到登录页面
                return false ; // 拒绝后续的请求
            }
        }
        return true;
    }
}
