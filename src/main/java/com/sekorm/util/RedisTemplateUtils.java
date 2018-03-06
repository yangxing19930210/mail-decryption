package com.sekorm.util;

import java.util.Arrays;  
import java.util.Map;
import java.util.concurrent.TimeUnit;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;  
import org.springframework.stereotype.Component;

/** 
 * RedisTemplate操作工具类 
 *  
 * @author noah_yang 
 * @version 3.0 
 * @since 2018-02-01
 */  
@Component
public final class RedisTemplateUtils {  
	
    private static RedisTemplate<String, Object> redisTemplate; 
    
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisTemplateUtils.redisTemplate = redisTemplate;
	}

	/** 
     * String写入缓存 
     *  
     * @param key 
     * @param value 
     * @param expire 
     */  
    public static void set(final String key, final Object value, final long expire) {  
        redisTemplate.opsForValue().set(key, value,expire, TimeUnit.DAYS);  
    } 
    
    /**
     * Hash写入缓存
     * 
     * @param key
     * @param hashKey
     * @param value
     */
    public static void hSet(final String key,final Object hashKey, final Object value) {  
        redisTemplate.opsForHash().put(key,hashKey,value);
    } 
    
    /**
     * Hash获取缓存
     * 
     * @param key
     * @return
     */
    public Map<Object, Object> getMap(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    /** 
     * String读取缓存 
     *  
     * @param key 
     * @param clazz 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T get(final String key, Class<T> clazz) {  
        return (T) redisTemplate.boundValueOps(key).get();  
    }  
      
    /** 
     * String读取缓存 
     * @param key 
     * @return 
     */  
    public static Object getObj(final String key){  
        return redisTemplate.boundValueOps(key).get();  
    }  
  
    /** 
     * 删除，根据key精确匹配 
     *  
     * @param key 
     */  
    public static void del(final String... key) {  
        redisTemplate.delete(Arrays.asList(key));  
    }  
  
    /** 
     * 批量删除，根据key模糊匹配 
     *  
     * @param pattern 
     */  
    public static void delpn(final String... pattern) {  
        for (String kp : pattern) {  
            redisTemplate.delete(redisTemplate.keys(kp + "*"));  
        }  
    }  
  
    /** 
     * key是否存在 
     *  
     * @param key 
     */  
    public static boolean exists(final String key) {  
        return redisTemplate.hasKey(key);  
    }  
    
}  