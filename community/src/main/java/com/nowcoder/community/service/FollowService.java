package com.nowcoder.community.service;


import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {


    @Autowired
    private RedisTemplate redisTemplate ;

    @Autowired
    private UserService userService ;

    /***
     * 业务 - userId的用户关注了entityType entityId的实体
     * @param userId 关注者
     * @param entityType 实体类型
     * @param entityId 实体id
     */
    public void follow(int userId,int entityType,int entityId){

        // 关注的时候需要存两份数据,需要用事务保证一致性

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeekey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerkey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();

                operations.opsForZSet().add(followeekey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerkey,userId,System.currentTimeMillis());


                return operations.exec();
            }
        });
    }


    /***
     * userId的用户取消关注了entityType entityId的实体
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unfollow(int userId,int entityType,int entityId){

        // 关注的时候需要存两份数据,需要用事务保证一致性

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeekey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerkey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                operations.multi();

                operations.opsForZSet().remove(followeekey,entityId);
                operations.opsForZSet().remove(followerkey,userId);


                return operations.exec();
            }
        });
    }


    // 查询某个用户关注的某一类实体的数量
    public long findFolloweeCount(int userId,int entityType){

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey); // 查询关注的该类型的实体的集合里有多少个数据

    }

    // 查询实体的粉丝的数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }


    // 查询当前用户是否已关注该实体

    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }



    // 查询某用户关注的人

    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1); // 查询时最新的时间在前面
        if(targetIds==null){
            return null ;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for(Integer targetId:targetIds){ // 对每个Id查到用户,封装到Map中
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey,targetId); // 取得关注每个用户的时间
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }

        return list ;
    }



    // 查询某用户的粉丝

    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit){

        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);

        if(targetIds==null){
            return null ;
        }

        List<Map<String,Object>> list = new ArrayList<>();
        for(Integer targetId:targetIds){ // 对每个Id查到用户,封装到Map中
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId); // 取得关注每个用户的时间
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }

        return list ;




    }




}
