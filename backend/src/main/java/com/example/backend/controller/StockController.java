package com.example.backend.controller;

import com.example.backend.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search/{stockName}")
    public List<String> searchStock(@PathVariable String stockName) throws Exception {
        List<String> lst = stockService.findWith(stockName);
        return lst;
    }

}
