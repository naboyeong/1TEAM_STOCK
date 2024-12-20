package com.example.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class DailyStockResponseDto {
    private String fidInputIscd; //종목
    private String stckBsopDate; //주식영업일자
    private String stckOprc; //주식시가
    private String stckHgpr; //주식최고가
    private String stckLwpr; //주식최저가
    private String stckClpr; //주식종가
    private String acmlVol; //누적거래량
    private String prdyCtrt; //전일대비율

    public DailyStockResponseDto(String fidInputIscd, String stckBsopDate, String stckOprc, String stckHgpr, String stckLwpr, String stckClpr, String acmlVol, String prdyCtrt) {
        this.fidInputIscd = fidInputIscd;
        this.stckBsopDate = stckBsopDate;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.stckClpr = stckClpr;
        this.acmlVol = acmlVol;
        this.prdyCtrt = prdyCtrt;
    }
}
