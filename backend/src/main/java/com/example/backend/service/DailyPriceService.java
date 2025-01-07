package com.example.backend.service;

import com.example.backend.entity.DailyStockPrice;
import com.example.backend.repository.DailyStockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import com.example.backend.dto.DailyPriceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class DailyPriceService {

    private final RestTemplate restTemplate;
    private DailyStockPrice dailyStockPrice;
    @Autowired
    private DailyStockPriceRepository dailyStockPriceRepository;

    @Value("${kis.api.appKey}")
    private String appKey;

    @Value("${kis.api.appSecret}")
    private String appSecret;

    @Value("${kis.api.baseUrl}")
    private String baseUrl;

    @Value("${kis.api.accessToken}")
    private String token;

    private final String DAILY_PATH = "/uapi/domestic-stock/v1/quotations/inquire-daily-price";

    public DailyPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DailyPriceDTO> postDailyPrice(String stockCode) throws Exception {
        String url = baseUrl + DAILY_PATH;

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
                    stockCode,                   // Stock ID
                    item.path("stck_bsop_date").asInt(), // 날짜
                    item.path("stck_hgpr").asInt(),      // 고가
                    item.path("stck_lwpr").asInt(),      // 저가
                    item.path("stck_clpr").asInt(),      // 종가
                    item.path("stck_oprc").asInt(),      // 시가
                    item.path("prdy_ctrt").asInt(),      // 등락율
                    item.path("acml_vol").asInt()        // 일거래량
            );

            priceList.add(dto);
        }

        return priceList;
    }

    @Transactional
    public void saveList(List<DailyPriceDTO> dtoList) throws Exception {

        for (DailyPriceDTO dto : dtoList) {
            DailyStockPrice existingPrice = dailyStockPriceRepository.findByStockIdAndDate(dto.getStockId(), dto.getDate());

            if (existingPrice == null) {
                dailyStockPrice = new DailyStockPrice();

                dailyStockPrice.setStockId(dto.getStockId());
                dailyStockPrice.setDate(dto.getDate());
                dailyStockPrice.setFluctuationRateDaily(dto.getChangeRate());
                dailyStockPrice.setCntgVol(dto.getVolume());
                dailyStockPrice.setOpeningPrice(dto.getOpen());
                dailyStockPrice.setClosingPrice(dto.getClose());
                dailyStockPrice.setHighPrice(dto.getHigh());
                dailyStockPrice.setLowPrice(dto.getLow());

                try {
                    dailyStockPriceRepository.save(dailyStockPrice);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public List<DailyPriceDTO> getDailyPrice(String stockCode) throws Exception {
        List<DailyPriceDTO> dailyPriceList = getDailyPriceFromDB(stockCode);
        return dailyPriceList;
    }

    @Transactional
    public List<DailyPriceDTO> getDailyPriceFromDB(String stockCode) throws Exception {
        List<DailyStockPrice> dailyPriceList = dailyStockPriceRepository.findByStockId(stockCode);

        List<DailyPriceDTO> dailyPriceDTOList = new ArrayList<>();

        for (DailyStockPrice dailyStockPrice : dailyPriceList) {
            DailyPriceDTO dailyPriceDTO = new DailyPriceDTO();

            // DailyStockPrice의 데이터를 DailyPriceDTO로 매핑
            dailyPriceDTO.setStockId(dailyStockPrice.getStockId());
            dailyPriceDTO.setDate(dailyStockPrice.getDate());
            dailyPriceDTO.setHigh(dailyStockPrice.getHighPrice());
            dailyPriceDTO.setLow(dailyStockPrice.getLowPrice());;
            dailyPriceDTO.setOpen(dailyStockPrice.getOpeningPrice());
            dailyPriceDTO.setClose(dailyStockPrice.getClosingPrice());
            dailyPriceDTO.setVolume(dailyStockPrice.getCntgVol());
            dailyPriceDTO.setChangeRate(dailyStockPrice.getFluctuationRateDaily());

            // DTO를 리스트에 추가
            dailyPriceDTOList.add(dailyPriceDTO);
        }

        return dailyPriceDTOList;

    }

}