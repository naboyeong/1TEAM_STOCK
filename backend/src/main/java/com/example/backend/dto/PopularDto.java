package com.example.backend.dto;

import com.example.backend.entity.Popular;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopularDto {
    private String stockId;

    private String ranking;
    public PopularDto(Popular popular) {
        this.stockId = popular.getStockId();
        this.ranking = popular.getRanking();
    }
}
