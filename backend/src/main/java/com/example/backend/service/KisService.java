package com.example.backend.service;

import com.example.backend.dto.DailyStockResponseDto;
import com.example.backend.entity.DailyEntity;
import com.example.backend.repository.DailyStockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
public class KisService {
    private final DailyStockRepository dailyStockRepository;
    @Value("${kis.api.appKey}")
    private String appKey;

    @Value("${kis.api.appSecret}")
    private String appSecret;

    @Value("${kis.api.accessToken}")
    private String accessToken;

    @Value("${kis.api.baseUrl}")
    private String baseUrl;

    private final String URL = "/uapi/domestic-stock/v1/quotations/inquire-daily-price";

    public KisService(DailyStockRepository dailyStockRepository) {
        this.dailyStockRepository = dailyStockRepository;
    }

    public DailyStockResponseDto postStock(String fid_input_iscd) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // Base URL과 쿼리 파라미터 추가
        HttpUrl url = HttpUrl.parse(baseUrl + URL).newBuilder()
                .addQueryParameter("FID_COND_MRKT_DIV_CODE", "J") // 쿼리 파라미터 추가
                .addQueryParameter("FID_INPUT_ISCD", fid_input_iscd)
                .addQueryParameter("FID_PERIOD_DIV_CODE", "D")
                .addQueryParameter("FID_ORG_ADJ_PRC", "0")
                .build();

        // HTTP Request 생성: header
        Request request = new Request.Builder()
                .url(url) // 쿼리 파라미터가 포함된 URL 사용
                .get()
                .addHeader("content-type", "application/json; charset=utf-8")
                .addHeader("authorization", "Bearer " + accessToken)
                .addHeader("appkey", appKey)
                .addHeader("appsecret", appSecret)
                .addHeader("tr_id", "FHKST01010400")
                .addHeader("custtype", "P")
                .build();

        // HTTP Response
        try (Response response = client.newCall(request).execute()) {
            // 응답 상태 코드 확인
            int statusCode = response.code();

            // 응답 본문 읽기
            if (statusCode == 200) {
                String responseBody = response.body().string();

                //JSON 데이터룰 DTO로 변환
                DailyStockResponseDto dailyStockResponseDto = convertResponseToDto(responseBody, fid_input_iscd);

                //DTO 데이터를 Entity로 변환 후 DB에 저장
                saveStockData(dailyStockResponseDto, fid_input_iscd);

                return dailyStockResponseDto;

            } else {
                log.info("Response Body is empty.");
            }
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            log.info("Request failed: " + e.getMessage());
        }

        return null;
    }

    private DailyStockResponseDto convertResponseToDto(String responseBody, String fid_input_iscd) {
        //DailyStockResponseDto
        DailyStockResponseDto dailyStockResponseDto = new DailyStockResponseDto();

        try {
            //Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            //JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(responseBody);
            //"output" 배열 가져오기
            JsonNode outputArray = rootNode.path("output");

            JsonNode firstObject = outputArray.get(0);

            String stck_bsop_date = firstObject.path("stck_bsop_date").asText(); //주식영업일자
            String stck_oprc = firstObject.path("stck_oprc").asText(); //주식시가
            String stck_hgpr = firstObject.path("stck_hgpr").asText(); //주식최고가
            String stck_lwpr = firstObject.path("stck_lwpr").asText(); //주식최저가
            String stck_clpr = firstObject.path("stck_clpr").asText(); //주식종가
            String acml_vol = firstObject.path("acml_vol").asText(); //누적거래량
            String prdy_ctrt = firstObject.path("prdy_ctrt").asText(); //전일대비율

            dailyStockResponseDto.setFidInputIscd(fid_input_iscd);
            dailyStockResponseDto.setStckBsopDate(stck_bsop_date);
            dailyStockResponseDto.setStckOprc(stck_oprc);
            dailyStockResponseDto.setStckHgpr(stck_hgpr);
            dailyStockResponseDto.setStckLwpr(stck_lwpr);
            dailyStockResponseDto.setStckClpr(stck_clpr);
            dailyStockResponseDto.setAcmlVol(acml_vol);
            dailyStockResponseDto.setPrdyCtrt(prdy_ctrt);

            return dailyStockResponseDto;
        } catch (Exception e) {
            log.info("Error Parsing response: " + e.getMessage());
        }
        return dailyStockResponseDto;
    }

    private void saveStockData(DailyStockResponseDto stockDto, String fid_input_iscd) {
        //DTO -> Entity 변환
        DailyEntity dailyEntity = new DailyEntity();
        dailyEntity.setFidInputIscd(fid_input_iscd);
        dailyEntity.setStckBsopDate(stockDto.getStckBsopDate());
        dailyEntity.setStckOprc(stockDto.getStckOprc());
        dailyEntity.setStckHgpr(stockDto.getStckHgpr());
        dailyEntity.setStckLwpr(stockDto.getStckLwpr());
        dailyEntity.setStckClpr(stockDto.getStckClpr());
        dailyEntity.setAcmlVol(stockDto.getAcmlVol());
        dailyEntity.setPrdyCtrt(stockDto.getPrdyCtrt());

        //DB에 저장
        dailyStockRepository.save(dailyEntity);
        log.info("Stock data saved successfully! ");
    }

    public DailyStockResponseDto getStock(String fid_input_iscd) throws Exception {

        DailyEntity dailyEntity = dailyStockRepository.findByFidInputIscd(fid_input_iscd);

        DailyStockResponseDto dailyStockResponseDto = new DailyStockResponseDto(dailyEntity.getFidInputIscd(), dailyEntity.getStckBsopDate(), dailyEntity.getStckOprc(), dailyEntity.getStckHgpr(), dailyEntity.getStckLwpr(), dailyEntity.getStckClpr(), dailyEntity.getAcmlVol(), dailyEntity.getPrdyCtrt());
        return dailyStockResponseDto;
    }
}
