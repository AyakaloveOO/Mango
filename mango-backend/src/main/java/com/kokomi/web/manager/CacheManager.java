package com.kokomi.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写缓存
     * @param key
     * @param value
     */
    public void put(String key,Object value){
        localCache.put(key,value);
        redisTemplate.opsForValue().set(key,value,10,TimeUnit.MINUTES);
    }

    /**
     * 读缓存
     * @param key
     * @return
     */
    public Object get(String key){
        Object value = localCache.getIfPresent(key);
        if(value!=null){
            return value;
        }
        value=redisTemplate.opsForValue().get(key);
        if(value!=null){
            localCache.put(key,value);
        }
        return value;
    }

    /**
     * 删除缓存
     * @param key
     */
    public void delete(String key){
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }
}
