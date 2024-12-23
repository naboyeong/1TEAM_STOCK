package com.example.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@RedisHash(value = "REAL_TIME_DB", timeToLive = 60) // TTL: 60초 설정
public class RealTimeRedis implements Serializable {

    @Id
    private String stockId; // 주식 코드 (Key)
    private String currentPrice;   // 주식 현재가 (체결 가격)
    private String fluctuationPrice; // 전일 대비
    private String fluctuationRate; // 전일 대비율 (등락률)
    private String fluctuationSign; // 전일 대비 부호
    private String transactionVolume; // 체결 거래량
    private String tradingTime;    // 주식 체결 시간

    // Getters and Setters
    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getFluctuationPrice() {
        return fluctuationPrice;
    }

    public void setFluctuationPrice(String fluctuationPrice) {
        this.fluctuationPrice = fluctuationPrice;
    }

    public String getFluctuationRate() {
        return fluctuationRate;
    }

    public void setFluctuationRate(String fluctuationRate) {
        this.fluctuationRate = fluctuationRate;
    }

    public String getFluctuationSign() {
        return fluctuationSign;
    }

    public void setFluctuationSign(String fluctuationSign) {
        this.fluctuationSign = fluctuationSign;
    }

    public String getTransactionVolume() {
        return transactionVolume;
    }

    public void setTransactionVolume(String transactionVolume) {
        this.transactionVolume = transactionVolume;
    }

    public String getTradingTime() {
        return tradingTime;
    }

    public void setTradingTime(String tradingTime) {
        this.tradingTime = tradingTime;
    }
}