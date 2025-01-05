package com.example.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @CrossOrigin(origins = "http://localhost:3000") // 프론트엔드 URL 허용
    @GetMapping("/redis-data")
    public Map<String, List<String>> getRedisData() {
        Set<String> keys = redisTemplate.keys("stock:*");
        Map<String, List<String>> redisData = new HashMap<>();

        for (String key : keys) {
            List<String> values = redisTemplate.opsForList().range(key, 0, 4); // 최신 5개 데이터
            redisData.put(key, values);
        }

        return redisData;
    }
}