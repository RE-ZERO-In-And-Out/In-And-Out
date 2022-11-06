package com.rezero.inandout.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService{

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    @Transactional
    public <T> List<T> getList(String key, Class<T> classType) {
        List<T> returnList = new ArrayList<>();

        Long size = redisTemplate.opsForList().size(key);

        if (size == null) {
            size = 0L;
        }

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                try {
                    T t = mapper.readValue((String)redisTemplate.opsForList().index(key, i), classType);
                    returnList.add(t);
                } catch (JsonProcessingException e) {
                    log.error("redis 서버가 동작하지 않거나 데이터를 불러올 수 없습니다.");
                }
            }
        }

        return returnList;
    }

    @Override
    @Transactional
    public <T> void putList(String key, List<T> categories) {
        for (T category : categories) {
            try {
                redisTemplate.opsForList().rightPush(key, mapper.writeValueAsString(category));
            } catch (JsonProcessingException e) {
                log.error("redis 서버가 동작하지 않거나 데이터를 삽입할 수 없습니다.");
            }
        }
    }

}
