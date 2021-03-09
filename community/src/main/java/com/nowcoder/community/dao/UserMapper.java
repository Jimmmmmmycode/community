package com.nowcoder.community.dao;


import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    // 根据id,username,email查询用户信息
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);

    // 插入一位user信息,返回插入行数
    int insertUser(User user);

    // 更新信息
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id,String password);

}


    // 要实现其中所有方法,需要给它提供配置文件,配置文件中给每个方法提供sql语句
    // Mybatis底层会自动帮我们生成一个实现类






