package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "POPULAR_TB")
public class Popular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rankingId;

    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @Column(name = "ranking")
    private Integer ranking;

    // Getters and Setters
    public Popular(Integer ranking, String stockId){
        this.ranking = ranking;
        this.stockId = stockId;
    }
}