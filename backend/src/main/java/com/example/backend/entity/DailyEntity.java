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

    private String stck_bsop_date; //주식영업일자
    private String stck_oprc; //주식시가
    private String stck_hgpr; //주식최고가
    private String stck_lwpr; //주식최저가
    private String stck_clpr; //주식종가
    private String acml_vol; //누적거래량
    private String prdy_ctrt; //전일대비율
}
