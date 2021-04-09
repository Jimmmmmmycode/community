package com.nowcoder.community.service;


import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;



@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate ; // 注入redisTemplate往redis中存取数据



    /***
     * 点赞业务方法
     * @param userId  点赞的userId (是谁点的赞)
     * @param entityType
     * @param entityId  用来拼entityLike key  实体 -> set记录点赞的用户id
     * @param entityUserId  用来拼 userLike key  实体-> 记录收到的赞的数量
     */
    public void like(int userId,int entityType,int entityId,int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // 第一次点赞,第二次取消,每次先判断一下userId是否在set中, 在--已点赞,不在--未点赞
                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId); // 查询要放在redis事务之外

                operations.multi(); // 事务开启

                if(isMember){ // 已点赞,取消点赞  从集合中移除该数据
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else{ // 未点赞，点赞
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });


    }

    // 查询某实体点赞数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey); // 统计点赞数量 = 统计集合中元素个数
    }

    // 查询某人对某实体的点赞状态
    // 用布尔值 - 只能表现两种状态
    // 用整数 - 能表现多种状态(方便业务扩展)
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0 ;
    }


    // 查询某个用户获得的赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }
}
