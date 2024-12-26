package com.example.backend.controller;

import com.example.backend.dto.DailyPriceDTO;
import com.example.backend.service.DailyPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.KisTokenService;

import java.util.List;

@RestController
@RequestMapping("/api/daily-price")
@RequiredArgsConstructor
public class DailyPriceController {

    private final DailyPriceService dailyPriceService; 
    private final KisTokenService kisTokenService;


    @GetMapping("/{stockCode}")
    public ResponseEntity<List<DailyPriceDTO>> getDailyPrices(@PathVariable String stockCode) {
        try {
            String accessToken = kisTokenService.getCachedAccessToken();
            List<DailyPriceDTO> dailyPrices = dailyPriceService.getDailyPrices(stockCode, accessToken);

            return ResponseEntity.ok(dailyPrices);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}