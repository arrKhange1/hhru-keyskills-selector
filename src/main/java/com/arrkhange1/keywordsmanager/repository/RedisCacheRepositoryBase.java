package com.arrkhange1.keywordsmanager.repository;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisCacheRepositoryBase<K, V> implements CacheRepository<K, V>, CacheExpireRepository<K, V> {

    private final RedisTemplate<K, V> redisTemplate;

    RedisCacheRepositoryBase(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public V get(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithExpirationTime(K key, V value, long secsTilExpire, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, secsTilExpire, unit);
    }
}
