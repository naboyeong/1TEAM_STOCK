package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceDTO {
    @JsonProperty("stck_bsop_date")
    private String date;  // 거래 날짜

    @JsonProperty("stck_hgpr")
    private String high;  // 고가

    @JsonProperty("stck_lwpr")
    private String low;   // 저가

    @JsonProperty("stck_clpr")
    private String close; // 종가
}