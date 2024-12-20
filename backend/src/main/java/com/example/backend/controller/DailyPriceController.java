package com.example.backend.controller;

import com.example.backend.service.DailyPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily-price")
@RequiredArgsConstructor
public class DailyPriceController {

    private final DailyPriceService dailyPriceService;

    @GetMapping("/{stockCode}")
    public ResponseEntity<String> getDailyPrices(@PathVariable String stockCode) {
        try {
            // Service 호출
            String stockData = dailyPriceService.getDailyPrice(stockCode);

            return ResponseEntity.ok(stockData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("API 호출 실패: " + e.getMessage());
        }
    }
}