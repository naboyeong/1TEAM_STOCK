package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 실시간 체결가 API 데이터 저장하는 Redis Controller
@RestController
@RequestMapping("/api/realtime")
public class StockDataController {

@Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/{stockId}")
    public List<String> getStockData(@PathVariable String stockId) {
        String redisKey = "stock:" + stockId;
        return redisTemplate.opsForList().range(redisKey, 0, 4); // 최신 5개 데이터 반환
    }
}