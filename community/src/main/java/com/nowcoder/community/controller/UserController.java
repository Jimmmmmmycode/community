package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
@RequestMapping("/user")

public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder; // 用户从hostHolder里取

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    @LoginRequired
    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting" ;
    }


     // 必须登录了才可以访问这个方法
     // 上传头像
     @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){

        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        // 给文件生成随机名

        String fileName = headerImage.getOriginalFilename();
        // 获取文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID()+suffix;
        model.addAttribute("fileName",fileName);
        //确定文件存放的路径
        File dest = new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest); // 存储文件 - 直接把headerImage中的内容写到dest中
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常",e);
        }

        // 更新当前用户头像路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser() ;
        String headerUrl = domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index"; // 重定向到首页
    }

    // 获取头像
    @RequestMapping(path="/header/{fileName}",method=RequestMethod.GET)
    // 向浏览器响应二进制数据(通过流输出)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存放路径
        fileName = uploadPath+"/"+fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/"+suffix);
        try( FileInputStream fis = new FileInputStream(fileName);
             OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取头像失败:"+e.getMessage());
        }
    }



    // 个人主页(可以查看任何人的主页)
    @RequestMapping(path="/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        // 防止恶意构造一大堆不存在的数据给服务器查询
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user",user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        // 查询关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        // 查询粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);

        //查询是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);


        return "/site/profile";

    }



}
