package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockName;
    private String price;

    public Stock() {}

    public Stock(String stockName, String price) {
        this.stockName = stockName;
        this.price = price;
    }

    public Long getId() {
        return id;
    }
    public String getStockName() {
        return stockName;
    }
    public String getPrice() {
        return price;
    }

}
