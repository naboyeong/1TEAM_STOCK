package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "POPULAR_TB")
public class Popular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rankingId;

    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @Column(name = "ranking")
    private Integer ranking;

    // Getters and Setters
}