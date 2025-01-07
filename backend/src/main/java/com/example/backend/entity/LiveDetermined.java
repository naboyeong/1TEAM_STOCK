package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LIVE_DETERMINED_TB")
public class LiveDetermined {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @Column(name = "stock_name")
    private Integer stockName;

    @Column(name = "current_price")
    private Integer currentPrice;

    @Column(name = "fluctuation")
    private Integer fluctuation;

    @Column(name = "fluctuation_rate")
    private Integer fluctuationRate;

    @Column(name = "sign")
    private Integer sign;

    @Column(name = "execution_time")
    private Integer executionTime;

    @Column(name = "exectuion_amout")
    private Integer executionAmount;

    // Getters and Setters
}