package com.arrkhange1.keywordsmanager.service;

import java.util.concurrent.TimeUnit;

public interface CacheExpireRepository<K, V> {
    void setWithExpirationTime(K key, V value, long secsTilExpire, TimeUnit unit);
}
