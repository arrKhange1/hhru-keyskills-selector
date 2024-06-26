package com.arrkhange1.keywordsmanager.service;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheRepositoryBase<K, V> implements CacheRepository<K, V> {

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
}
