package com.example.backend.controller;

import com.example.backend.dto.DailyStockResponseDto;
import com.example.backend.service.KisService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class KisController {
    @Autowired
    private KisService kisService;

    @GetMapping("/daily")
    public DailyStockResponseDto getDailyStock() throws Exception {
        DailyStockResponseDto responseDto = new DailyStockResponseDto();
        responseDto = kisService.getStock();

        // null 체크 및 예외 처리
        if (responseDto == null) {
            throw new Exception("Response is null");
        }

        return responseDto;
    }

}
