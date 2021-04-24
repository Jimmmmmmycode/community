package com.nowcoder.community.dao;


import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CommentMapper {


    /***
     * 根据实体查询评论
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param offset 分页条件
     * @param limit 每页显示行数限制
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

    List<Comment> selectCommentsByUser(int userId, int offset, int limit);

    int selectCountByUser(int userId);
}
