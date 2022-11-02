package com.rezero.inandout.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.exception.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService{

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public <T> T get(String key, Class<T> classType) {
        String redisValue = (String) redisTemplate.opsForValue().get(key);

        if (ObjectUtils.isEmpty(redisValue)) {
            return null;
        } else {
            try {
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.error("Parsing error", e);
                return null;
            }
        }
    }

    @Override
    public void put(String key, Object object) {
        try {
            redisTemplate.opsForValue().set(key, mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }
}
