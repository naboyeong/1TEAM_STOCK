package com.example.backend.controller;

import com.example.backend.dto.DailyStockResponseDto;
import com.example.backend.service.KisService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class KisController {
    @Autowired
    private KisService kisService;

    @PostMapping("/daily")
    public DailyStockResponseDto postDailyStock(@RequestParam String fid_input_iscd) throws Exception {
        DailyStockResponseDto responseDto = new DailyStockResponseDto();
        responseDto = kisService.postStock(fid_input_iscd);

        // null 체크 및 예외 처리
        if (responseDto == null) {
            throw new Exception("Response is null");
        }
        return responseDto;
    }

    @GetMapping("/daily")
    public DailyStockResponseDto getDailyStock(@RequestParam String fid_input_iscd) throws Exception {
        DailyStockResponseDto dailyStockResponseDto = kisService.getStock(fid_input_iscd);

        if (dailyStockResponseDto.equals(null)) {
            throw new Exception("FID_INPUT_ISCD data is not exist in DB");
        }

        return dailyStockResponseDto;
    }

}
