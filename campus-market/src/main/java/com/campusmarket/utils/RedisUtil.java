package com.campusmarket.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ========== 基本操作 ==========

    /**
     * 存储（无过期时间）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存储（带过期时间）
     *
     * @param timeout 过期时长
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ========== Token 相关 ==========

    /**
     * 存储 Token（默认 24 小时过期）
     */
    public void setToken(String token, Object value) {
        set(token, value, 24, TimeUnit.HOURS);
    }

    /**
     * 存储 Token（自定义过期时间）
     */
    public void setToken(String token, Object value, long timeout, TimeUnit unit) {
        set(token, value, timeout, unit);
    }

    /**
     * 获取 Token 对应的值
     */
    public Object getToken(String token) {
        return get(token);
    }

    /**
     * 删除 Token
     */
    public boolean deleteToken(String token) {
        return delete(token);
    }

    // ========== 过期时间 ==========

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获取过期时间（秒）
     */
    public long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key);
        return expire != null ? expire : -1;
    }
}
