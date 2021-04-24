package com.nowcoder.community.util;

public class RedisKeyUtil {


    private static final String SPLIT = ":" ; // key在拼的时候以冒号分隔各种单词

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";

    private static final String PREFIX_FOLLOWER = "follower";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    private static final String PREFIX_UV = "uv";

    private static final String PREFIX_DAU = "dau";

    private static final String PREFIX_POST = "post";

    /***
     * 某个实体的赞的key
     * 传入实体的关键信息来拼key
     * 准备key的过程也是梳理如何往redis里存数据的过程
     * @param entityType
     * @param entityId
     * @return  eg :  like:entity:entityType:entityId -> set(userId)  为了业务的可扩展性,不能只存一个整数
     */
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    // 某个用户关注了某个实体
    // followee:userId:entityType -> zset(entityId,now) 以当前时间作为分数排序
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    // 某个实体拥有的粉丝
    // follower:EntityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }


    /***
     * 某个用户的赞
     * @param userId
     * @return eg:  like:user:userId -> int
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }



    // 登录验证码
    // 用一个字符串临时标识一下用户
    public static String getKaptchKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }


    //登录的凭证

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    // 单日UV
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }

    // 区间UV ,用HyperLogLog合并
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }

    // 帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST+SPLIT+"score";
    }
}
