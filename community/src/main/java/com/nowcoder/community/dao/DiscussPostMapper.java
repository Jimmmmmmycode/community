package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface DiscussPostMapper {

    // userId - 个人主页  =0 - 不拼到sql里（动态sql）
    // 分页功能 offset-每页起始行行号 limit- 每页最多显示的数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 计算一共有多少页


    // @Param - 用于给参数取别名
    // 如果只有一个参数,并且在<if>中使用,则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);




}
