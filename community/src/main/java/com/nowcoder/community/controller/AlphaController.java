package com.nowcoder.community.controller;


import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@Controller
@RequestMapping("/alpha")  // Spring Mvc注解
// 给类取一个访问的名字

public class AlphaController {

    // Controller在处理请求时调Service
    @Autowired
    private AlphaService alphaService;


    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    // 获得http请求对象和服务器响应对象
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        System.out.println(request.getCookies());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+": "+value);
        }
        System.out.println(request.getParameter("code" ));  // 获取请求参数值

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8"); // 设置内容类型（html，图片，文字 ...)

        // 想用response向服务器响应网页，就是通过它的输出流向浏览器发出响应
        try(PrintWriter writer = response.getWriter();) { //jdk7 新语法  -自动加一个finally, 在finally里close
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     处理http请求
     */
    // GET请求
    // students?current=1&limit=20 前端发起对服务器学生信息请求的参数

    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents( @RequestParam(name="current",required = false,defaultValue = "1" ) int current,
                               @RequestParam(name="limit",required = false,defaultValue = "10" ) int limit ){      // 只要参数取的名字和url中的参数名保持一致,Servlet在检测到后就会把url中参数直接赋值到函数里
        System.out.println(current);
        System.out.println(limit);
        return  " some students ";
    }

    //  /student?id=123  or /student/123

    @RequestMapping(path ="/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id ){
        System.out.println(id);
        return "a student";
    }


    // POST请求


    @RequestMapping(path="/student" , method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据

    @RequestMapping(path="/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();   // 新建一个 ModelAndView 对象 (MVC)
        mav.addObject("name","张三");// 往 ModelAndView 中添加 值 model数据 -》view视图
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }


    @RequestMapping(path="/school",method = RequestMethod.GET)
    public String getSchool(Model model){    // DispatcherServlet自动实例化model对象
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "demo/view";         // return view的路径
    }

    // 响应json数据 (异步请求)
    // Java对象->json字符串->js对象

    /**
    @RequestMapping(path="/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,>
    **/

    // cookie示例
    @RequestMapping(value = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookied(HttpServletResponse response){

        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie生效的范围
        cookie.setPath("/community/alpha");
        // 设置cookie生存时间
        cookie.setMaxAge(60*10);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";

    }

    @RequestMapping(value="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    // Session示例
    @RequestMapping(value="/session/set",method = RequestMethod.GET)
    @ResponseBody   // 响应字符串
    // Spring MVC自动创建Session并注入
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "setSession";
    }


    @RequestMapping(value="/session/get",method = RequestMethod.GET)
    @ResponseBody   // 响应字符串
    // Spring MVC自动创建Session并注入
    public String getSession(HttpSession session){
        System.out.println(session.getId());
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "getSession";
    }







}
