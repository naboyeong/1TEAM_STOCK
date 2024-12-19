package com.example.backend.controller;

import com.example.backend.entity.Stock;
import com.example.backend.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks/rds")
public class RDSController {

    private final StockRepository stockRepository;

    public RDSController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return ResponseEntity.ok(stocks);
    }
    // 모든 주식 조회
    // curl -X GET http://localhost:8080/stocks/rds

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable int id) {
        return stockRepository.findById(id)
                .map(stock -> ResponseEntity.ok(stock))
                .orElse(ResponseEntity.notFound().build());
    }
    // 특정 ID 주식 조회
    // curl -X GET http://localhost:8080/stocks/rds/1

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        Stock saved = stockRepository.save(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    // 새로운 주식 생성
    // curl -X POST http://localhost:8080/stocks/rds \
    // -H "Content-Type: application/json" \
    // -d '{"stockName":"TestStock","stockId":"TST001"}'

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable int id, @RequestBody Stock updatedStock) {
        return stockRepository.findById(id)
                .map(existing -> {
                    existing.setStockName(updatedStock.getStockName());
                    existing.setStockId(updatedStock.getStockId());
                    Stock saved = stockRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // 주식 업데이트
    // curl -X PUT http://localhost:8080/stocks/rds/1 \
    // -H "Content-Type: application/json" \
    // -d '{"stockName":"UpdatedName","stockId":"UPD123"}'

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable int id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 특정 ID 주식 삭제
    // curl -X DELETE http://localhost:8080/stocks/rds/1

}