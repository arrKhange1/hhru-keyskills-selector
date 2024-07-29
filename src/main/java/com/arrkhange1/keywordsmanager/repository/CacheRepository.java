package com.arrkhange1.keywordsmanager.repository;

public interface CacheRepository<K, V> {
    V get(K key);
    void set(K key, V value);
}
