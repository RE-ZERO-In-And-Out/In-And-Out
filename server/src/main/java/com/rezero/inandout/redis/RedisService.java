package com.rezero.inandout.redis;

public interface RedisService {
    <T> T get(String key, Class<T> classType);
    void put(String key, Object object);
}
