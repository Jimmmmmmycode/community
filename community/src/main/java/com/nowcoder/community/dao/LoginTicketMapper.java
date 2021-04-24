package com.nowcoder.community.dao;


import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {

    // 插入 凭证
    @Insert({
            "INSERT INTO login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})",
            ""})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "SELECT id,user_id,ticket,status,expired ",
            "FROM login_ticket WHERE ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);


    // 退出功能 - 修改凭证的状态 (在互联网行业很少真正地删除数据,更多的时候是改变状态标志位）

    @Update({
            "<script>",
            "UPDATE login_ticket SET status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\">",
            "and 1=1",   // 演示一下动态sql的写法
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);




}
