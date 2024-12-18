package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "STOCK_TB")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "stock_id")
    private String stockId;

    public Stock() {}

    public Stock(String stockName, String stockId) {
        this.stockName = stockName;
        this.stockId = stockId;
    }

    public Integer getId() {
        return id;
    }
    public String getStockName() {
        return stockName;
    }

}
