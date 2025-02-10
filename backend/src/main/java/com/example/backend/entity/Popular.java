package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "POPULAR_TB")
public class Popular {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rankingId;

    @NotNull
    @Max(6)
    @Column(name = "stock_id", nullable = false)
    private String stockId;

    @NotNull
    @Max(2)
    @Column(name = "ranking")
    private Integer ranking;

    @NotNull
    @Column(name = "stock_name")
    private String stockName;

    @NotNull
    @Column(name = "acmlvol")
    private Integer acmlvol;

    // Getters and Setters
    public Popular(Integer ranking, String stockId, String stockName, Integer acmlvol) {
        this.ranking = ranking;
        this.stockId = stockId;
        this.stockName = stockName;
        this.acmlvol = acmlvol;
    }
}