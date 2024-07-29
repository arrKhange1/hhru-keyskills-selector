package com.arrkhange1.keywordsmanager.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisJSONCacheRepository extends RedisCacheRepositoryBase<String, Object> {

    RedisJSONCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

}
