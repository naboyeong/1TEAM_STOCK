package com.example.backend.repository.redis;

import com.example.backend.entity.RealTimeRedis;
import org.springframework.data.repository.CrudRepository;

public interface RealTimeRedisRepository extends CrudRepository<RealTimeRedis, String> {
    // Redis Repository 코드
}