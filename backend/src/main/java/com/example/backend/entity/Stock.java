package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "STOCK_TB")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "stock_name")
    private String stockName;

    public Stock() {}

    public Stock(String stockName) {
        this.stockName = stockName;
    }

    public String getId() {
        return id;
    }
    public String getStockName() {
        return stockName;
    }

}
