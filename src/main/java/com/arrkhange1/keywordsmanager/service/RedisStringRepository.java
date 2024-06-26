package com.arrkhange1.keywordsmanager.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisStringRepository extends RedisCacheRepositoryBase<String, String> {

    RedisStringRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }
}
