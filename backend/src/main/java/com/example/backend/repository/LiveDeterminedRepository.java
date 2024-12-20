package com.example.backend.repository;

import com.example.backend.entity.LiveDetermined;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveDeterminedRepository extends JpaRepository<LiveDetermined, Integer> {
}