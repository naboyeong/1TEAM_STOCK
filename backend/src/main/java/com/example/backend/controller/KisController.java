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
    public void getDailyStock() throws Exception {
        kisService.getStock();
//        try {
//            if (fid_input_iscd == null || fid_input_iscd.isEmpty()) {
//                log.info("1"+fid_input_iscd);
//                throw new IllegalArgumentException("fid_input_iscd is required");
//            }
//            log.info("2"+fid_input_iscd);
//            kisService.getStock(fid_input_iscd);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return kisService.getStock(fid_input_iscd);
    }

}
