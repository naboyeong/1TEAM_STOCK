package com.example.backend.websocket;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import java.io.IOException;

@Service
public class KisWebSocketService {
    @Value("${kis.api.baseUrl}")
    private String baseUrl;
    
    private final String APPROVAL_PATH = "/oauth2/Approval";
    @Value("${kis.api.appKey}")
    private String appKey;

    @Value("${kis.api.appSecret}")
    private String appSecret;

    public String getWebSocketApprovalKey() throws IOException {
        OkHttpClient client = new OkHttpClient();
        
        String jsonBody = String.format(
            "{\"grant_type\":\"client_credentials\",\"appkey\":\"%s\",\"secretkey\":\"%s\"}",
            appKey, appSecret
        );
        
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"),
            jsonBody
        );
        
        String fullUrl = baseUrl.trim() + APPROVAL_PATH;
        
        Request request = new Request.Builder()
            .url(fullUrl)
            .post(body)
            .addHeader("content-type", "application/json")
            .build();
            
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}


