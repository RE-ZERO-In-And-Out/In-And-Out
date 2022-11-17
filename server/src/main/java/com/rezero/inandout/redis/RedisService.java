package com.rezero.inandout.redis;

import java.util.List;

public interface RedisService {
    <T> List<T> getList(String key, Class<T> classType);
    <T> void putList(String key, List<T> categories);
}
