package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DailyPriceService {

    private final RestTemplate restTemplate;

    @Value("${kis.api.appKey}")
    private String appKey;

    @Value("${kis.api.appSecret}")
    private String appSecret;

    @Value("${kis.api.baseUrl}")
    private String baseUrl;

    private final String DAILY_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-price";


    public DailyPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getDailyPrice(String stockCode) {
        String url = baseUrl + DAILY_PATH;
        log.info("Request URL: {}", url);

        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6ImUyNmM5MzJlLWVhODgtNGNkZC1hM2ZlLWVkNWZiMmIzMjJjNCIsInByZHRfY2QiOiIiLCJpc3MiOiJ1bm9ndyIsImV4cCI6MTczNDY3MjU1MywiaWF0IjoxNzM0NTg2MTUzLCJqdGkiOiJQU0F4OHF1djVQWnR6b2xSNll0YlJTM1FUUjVIc3RRZnVONGUifQ.iFaH0PytCnsUHAfaCZQsz4SjHee4y462wWee3KZnqfl2_H0guAv-hfeLVxZBa-ea6f6C5UPK18PpWiojMHHjDA");//토큰 하드코딩
        headers.set("tr_id", "FHKST01010400");

        // Query Parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("FID_COND_MRKT_DIV_CODE", "J"); // 주식 코드
        queryParams.put("FID_INPUT_ISCD", stockCode); // 종목코드
        queryParams.put("FID_PERIOD_DIV_CODE", "D"); // D : 일별
        queryParams.put("FID_ORG_ADJ_PRC", "0");

        // 요청
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url + "?FID_COND_MRKT_DIV_CODE={FID_COND_MRKT_DIV_CODE}&FID_INPUT_ISCD={FID_INPUT_ISCD}&FID_PERIOD_DIV_CODE={FID_PERIOD_DIV_CODE}&FID_ORG_ADJ_PRC={FID_ORG_ADJ_PRC}",
                    HttpMethod.GET,
                    entity,
                    String.class,
                    queryParams
            );

            log.info("Response Code: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("일별 시세 조회 중 오류 발생", e);
            throw e;
        }
    }
}