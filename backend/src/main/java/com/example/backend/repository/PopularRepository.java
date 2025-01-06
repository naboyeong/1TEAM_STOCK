package com.example.backend.repository;

import com.example.backend.entity.Popular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PopularRepository extends JpaRepository<Popular, Integer> {
    Optional<Popular> findByRanking(String ranking);

    // ranking 값으로 stockId 변경
    @Modifying
    @Query("UPDATE Popular p SET p.stockId = :stockId WHERE p.ranking = :ranking")
    int updateStockIdByRanking(String stockId, String ranking);
}