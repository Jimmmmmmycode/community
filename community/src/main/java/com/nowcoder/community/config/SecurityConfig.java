package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    // 不配置 protected void configure(AuthenticationManagerBuilder auth) throws Exception
    // 绕过这个环节

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 在这个配置方法里进行授权
        // 看Controller- 记录每个方法 - 哪些是不用登陆可以访问,哪些是登陆后的普通用户可访问
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting", // 不登录不可以进行设置,在UserController中
                                    "/user/upload", // 不登录不可以上传头像,在UserController中
                                    "/discuss/add", // 不登录不可以发帖,在DiscussPostController中
                                    "/comment/add/**",// 不登陆不可以添加评论,在CommentController中
                                    "/letter/**", // MessageController
                                    "/notice/**", // MessageController
                                    "/like", // 不登录不可以点赞,在likeController中
                                    "/follow",
                                    "/unfollow"
                )
                .hasAnyAuthority(
                            AUTHORITY_USER,
                            AUTHORITY_ADMIN,
                            AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();


        // 权限不够时候如何处理
        // 区分普通请求和异步请求
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 无登录时的处理
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                            String xRequestedWith = request.getHeader("x-requested-with");
                            if("XMLHttpRequest".equals(xRequestedWith)){ // 异步请求
                                response.setContentType("application/plain;charset=utf-8");
                                PrintWriter writer = response.getWriter();
                                writer.write(CommunityUtil.getJSONString(400,"你还没有登录"));
                            }else{
                                response.sendRedirect(request.getContextPath()+"/login");

                            }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足时的处理
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){ // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(400,"你没有访问此功能的权限"));
                        }else{
                            response.sendRedirect(request.getContextPath()+"/denied");

                        }
                    }
                }
                );

        // Security底层默认拦截logout请求进行退出处理
        // 覆盖默认逻辑,才能执行我们自己退出的代码
        // 善意的欺骗,阻止它拦截
        http.logout().logoutUrl("/securitylogout");
    }


}
