package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankingDTO {

    private Integer ranking; //순위

    private String stockName; //stock name

    private String stockId; //stock id

    private Integer changeRate; // 등락율

    private Integer volume; // 일거래량
}
