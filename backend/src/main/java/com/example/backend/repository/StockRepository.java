package com.example.backend.repository;

import com.example.backend.entity.Stock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    Optional<Stock> findByStockId(String stockId);
}
