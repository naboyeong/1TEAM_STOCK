package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
@Table(name = "DAILY_STOCK_TB")
public class DailyStockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dailyId;

    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @Column(name = "date")
    private Integer date;

    @Column(name = "fluctuation_rate_daily")
    private Integer fluctuationRateDaily;

    @Column(name = "cntg_vol")
    private Integer cntgVol;

    @Column(name = "opening_price")
    private Integer openingPrice;

    @Column(name = "closing_price")
    private Integer closingPrice;

    @Column(name = "high_price")
    private Integer highPrice;

    @Column(name = "low_price")
    private Integer lowPrice;


    // Getters and Setters
}