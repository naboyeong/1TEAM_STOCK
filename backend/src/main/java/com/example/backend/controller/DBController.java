package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// MySQL
import com.example.backend.repository.StockRepository;
import com.example.backend.entity.Stock;
// Redis
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Optional;
import java.util.List;

@RestController
public class DBController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/redis-test")
    public String testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello from Redis!");
        return redisTemplate.opsForValue().get("testKey");
    }

    @Autowired
    private StockRepository stockRepository;

    // 1. 데이터 저장 (Save)
    @GetMapping("/save-stock")
    public String saveStock() {
        Stock stock = new Stock("005930", "72000");
        stockRepository.save(stock);
        return "Stock data saved to RDS!";
    }

    // 2. ID로 데이터 조회 (FindById)
    @GetMapping("/find-stock/{id}")
    public String findStockById(@PathVariable Long id) {
        Optional<Stock> stock = stockRepository.findById(id);
        return stock.map(s -> "Found: " + s.getStockName() + " - " + s.getPrice())
                    .orElse("Stock not found with ID: " + id);
    }

    // 3. 모든 데이터 조회 (FindAll)
    @GetMapping("/find-all-stocks")
    public List<Stock> findAllStocks() {
        return stockRepository.findAll();
    }

    // 4. ID로 데이터 삭제 (DeleteById)
    @GetMapping("/delete-stock/{id}")
    public String deleteStockById(@PathVariable Long id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
            return "Stock with ID: " + id + " has been deleted.";
        } else {
            return "Stock not found with ID: " + id;
        }
    }
}
