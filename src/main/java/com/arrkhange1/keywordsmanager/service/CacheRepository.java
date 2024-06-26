package com.arrkhange1.keywordsmanager.service;

public interface CacheRepository<K, V> {
    V get(K key);
    void set(K key, V value);
}
