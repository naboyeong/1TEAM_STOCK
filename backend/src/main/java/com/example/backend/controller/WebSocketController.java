package com.example.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.backend.service.KisTokenService;
import com.example.backend.websocket.KisWebSocketService;
import com.example.backend.websocket.KisWebSocketClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {
    
    private final KisWebSocketService webSocketService;
    private final KisTokenService tokenService;
    private final KisWebSocketClient webSocketClient;
    
    public WebSocketController(KisWebSocketService webSocketService, 
                             KisTokenService tokenService,
                             KisWebSocketClient webSocketClient) {  // 생성자 수정
        this.webSocketService = webSocketService;
        this.tokenService = tokenService;
        this.webSocketClient = webSocketClient;
    }
    
    @GetMapping("/connect")
    public String connectWebSocket() throws Exception {
        String approvalKey = webSocketService.getWebSocketApprovalKey();
        webSocketClient.connect(approvalKey);
        return "WebSocket 연결 성공: " + approvalKey;

    }
}
