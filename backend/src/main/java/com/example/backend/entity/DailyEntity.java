package com.example.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DailyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fidInputIscd; //종목
    private String stckBsopDate; //주식영업일자
    private String stckOprc; //주식시가
    private String stckHgpr; //주식최고가
    private String stckLwpr; //주식최저가
    private String stckClpr; //주식종가
    private String acmlVol; //누적거래량
    private String prdyCtrt; //전일대비율
}
