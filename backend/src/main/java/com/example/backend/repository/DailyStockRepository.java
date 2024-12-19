package com.example.backend.repository;

import com.example.backend.entity.DailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStockRepository extends JpaRepository<DailyEntity, Long> {
}
