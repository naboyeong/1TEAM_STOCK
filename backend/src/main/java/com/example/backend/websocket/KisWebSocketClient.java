package com.example.backend.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response; 
import okhttp3.RequestBody; 
import org.json.JSONObject;

@Slf4j
@Service
public class KisWebSocketClient {
    private final String WS_URL = "ws://ops.koreainvestment.com:21000/tryitout/H0STCNT0";
    
    private WebSocket webSocket;
    private String approvalKey;

    //웹소켓 연결 코드(제일 먼저 실행)
    public void connect(String approvalKeyJson) {
        JSONObject jsonObject = new JSONObject(approvalKeyJson);
        this.approvalKey = jsonObject.getString("approval_key");
        OkHttpClient client = new OkHttpClient();
        
        
        Request request = new Request.Builder()
            .url(WS_URL)
            .addHeader("approval_key", approvalKey)            
            .addHeader("custtype", "P")                                  
            .addHeader("tr_type", "1") 
            .addHeader("content-type", "utf-8")
            .build();
            
        this.webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("웹소켓 연결 성공");
                String[] subscriptionlist = {"005930", "000660"}; //구독할 리스트(삼성전자, sk하이닉스)
                subscribeStocks(subscriptionlist);
            }
            
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    log.info("원본 데이터: {}", text);
                    
                    // 데이터가 |로 구분되어 있으므로 분리
                    String[] data = text.split("\\|");

                    if (data.length > 3) {
                        String text_encryption = data[0];
                        String text_tr_id = data[1];
                        int text_data_number = Integer.parseInt(data[2]);
                        String text_response_messages = data[3];

                        String[] responsedata = text_response_messages.split("\\^");
                        
                        for (int i = 0; i < text_data_number; i++) {
                            String MKSC_SHRN_ISCD = responsedata[i * 46]; //유가증권 단축 종목코드
                            String STCK_CNTG_HOUR = responsedata[i * 46 + 1]; //주식 체결 시간
                            String STCK_PRPR = responsedata[i * 46 + 2]; //주식 현재가
                            String PRDY_VRSS_SIGN = responsedata[i * 46 + 3]; //전일 대비 부호
                            String PRDY_CTRT = responsedata[i * 46 + 5]; // 전일 대비율
                            String CNTG_VOL = responsedata[i * 46 + 12]; //체결 거래량
        
                            //테스트용
                            log.info("유가증권 단축 종목코드(MKSC_SHRN_ISCD): {}, 주식 현재가(STCK_PRPR): {}, 주식 체결 시간(STCK_CNTG_HOUR): {}, 전일 대비 부호 (PRDY_VRSS_SIGN) : {}, 전일 대비율(PRDY_CTRT): {}, 체결 거래량(CNTG_VOL) : {}", 
                                MKSC_SHRN_ISCD, STCK_PRPR, STCK_CNTG_HOUR, PRDY_VRSS_SIGN, PRDY_CTRT, CNTG_VOL);
                            
                            //테스트용
                            if (MKSC_SHRN_ISCD.equals("005930")) {
                                log.info("삼성전자 현재가: {}, 시간: {}", STCK_PRPR, STCK_CNTG_HOUR);
                            } else if (MKSC_SHRN_ISCD.equals("000660")) {
                                log.info("sk하이닉스 현재가: {}, 시간: {}", STCK_PRPR, STCK_CNTG_HOUR);
                            }
                            

                        }
                    }

        
                    

                    
                    
                } catch (Exception e) {
                    log.error("데이터 처리 중 오류: ", e);
                }
            }
            
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("웹소켓 에러: ", t);
            }
        });
    }

    //종목 코드로 구독 요청 stockCodes 종목 코드
    public void subscribeStocks(String[] stockCodes) { //리스트형식으로 받음
        if (webSocket == null) {
            log.error("WebSocket이 연결되지 않았습니다.");
            return;
        }

        String requestTemplate = "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\"},\"body\":{\"input\":{\"tr_id\":\"H0STCNT0\",\"tr_key\":\"%s\"}}}";

        for (String stockCode : stockCodes) {
            String request = String.format(requestTemplate, approvalKey, stockCode);
            webSocket.send(request);
            log.info("종목 코드 {} 구독 요청 전송", stockCode);
        }
    }
}


