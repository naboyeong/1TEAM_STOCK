package com.example.backend.dto;

import com.example.backend.entity.Popular;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopularDTO {
    private String stockId;
    private Integer ranking;

    public PopularDTO(Popular popular) {
        this.stockId = popular.getStockId();
        this.ranking = popular.getRanking();
    }
}
