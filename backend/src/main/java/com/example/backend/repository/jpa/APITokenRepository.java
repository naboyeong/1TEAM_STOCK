package com.example.backend.repository.jpa;

import com.example.backend.entity.APIToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface APITokenRepository extends JpaRepository<APIToken, Integer> {

    // 특정 토큰 값으로 조회
    Optional<APIToken> findByTokenValue(String tokenValue);

    // 만료된 토큰 삭제
    @Query("DELETE FROM APIToken t WHERE t.expirationTime < CURRENT_TIMESTAMP")
    void deleteExpiredAPITokens();
}