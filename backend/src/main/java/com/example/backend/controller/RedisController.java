package com.example.backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
// Redis
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;

@RestController
public class RedisController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/redis-test")
    public String testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello from Redis!");
        return redisTemplate.opsForValue().get("testKey");
    }
    
}
