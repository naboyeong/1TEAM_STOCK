package com.example.backend.config;

import com.example.backend.websocket.StockWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(stockWebSocketHandler(), "/ws/stock")
                .setAllowedOrigins("https://1thebest.site"); // 특정 도메인만 허용
    }

    @Bean
    public StockWebSocketHandler stockWebSocketHandler() {
        return new StockWebSocketHandler();
    }
}