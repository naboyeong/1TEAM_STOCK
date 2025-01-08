package com.example.backend.controller;

import com.example.backend.dto.RankingDTO;
import com.example.backend.dto.ResponseOutputDTO;
import com.example.backend.service.KisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class KisController {

    private KisService kisService;

    @Autowired
    public KisController(KisService kisService) {
        this.kisService = kisService;
    }

    @GetMapping("/volume-rank")
    public Mono<List<ResponseOutputDTO>> getVolumeRank() {
        return kisService.getVolumeRank();
    }

    @GetMapping("/popular")
    public List<RankingDTO> getPopular() {
        return kisService.getPopular10();
    }

    @PostMapping("/get-rankings-daily")
    public List<String> getDailyData() {
        return kisService.getDailyDataFromAPI();
    }
}