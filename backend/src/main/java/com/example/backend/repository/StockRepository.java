package com.example.backend.repository;

import com.example.backend.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    // 특정 주식 ID로 조회
    List<Stock> findByStockId(String stockId);

    // 특정 주식 이름으로 조회
    List<Stock> findByStockName(String stockName);
}