package com.example.backend.repository;

import com.example.backend.dto.DailyStockResponseDto;
import com.example.backend.entity.DailyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyStockRepository extends JpaRepository<DailyEntity, Long> {
    DailyEntity findByFidInputIscd(String fid_input_iscd);
}
