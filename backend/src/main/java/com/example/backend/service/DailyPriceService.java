package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import com.example.backend.dto.DailyPriceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

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

    //@Value("${kis.api.accessToken}")
    //private String token;

    private final String DAILY_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-price";

    public DailyPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DailyPriceDTO> getDailyPrices(String stockCode, String token) throws Exception {
        String url = baseUrl + DAILY_PATH;
        log.info("Request URL: {}", url);

        // HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appkey", appKey);
        headers.set("appsecret", appSecret);
        headers.set("authorization", "Bearer " + token);
        headers.set("tr_id", "FHKST01010400");

        // Query Parameters 설정
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(
                    url + "?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=" + stockCode +
                            "&FID_PERIOD_DIV_CODE=D&FID_ORG_ADJ_PRC=0",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생", e);
            throw new RuntimeException("API 호출 실패", e);
        }

        log.info("Response Code: {}", response.getStatusCode());
        log.info("Response Body: {}", response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
        }

        // JSON 파싱 및 DTO 변환
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        JsonNode outputArray = rootNode.path("output");

        List<DailyPriceDTO> priceList = new ArrayList<>();
        for (JsonNode item : outputArray) {
            DailyPriceDTO dto = new DailyPriceDTO(
                    item.path("stck_bsop_date").asText(), // 날짜
                    item.path("stck_hgpr").asText(),      // 고가
                    item.path("stck_lwpr").asText(),      // 저가
                    item.path("stck_clpr").asText(),      // 종가
                    item.path("stck_oprc").asText(),      // 시가
                    item.path("prdy_ctrt").asText(),      // 등락율
                    item.path("acml_vol").asText()        // 일거래량
            );
            priceList.add(dto);
        }

        return priceList;
    }
}