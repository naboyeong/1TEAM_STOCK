package com.example.backend.service;

import com.example.backend.dto.PopularDto;
import com.example.backend.dto.ResponseOutputDTO;
import com.example.backend.entity.Popular;
import com.example.backend.repository.PopularRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class KafkaConsumerService {
    private PopularDto PopularDto;
    private Popular Popular;
    @Autowired
    private PopularRepository popularRepository; // JPA 또는 JDBC Repository

    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Popular getPopularByRanking(String dataRank) {
        return popularRepository.findByRanking(dataRank)
                .orElseThrow(() -> new RuntimeException("Popular not found for ranking: " + dataRank));
    }

    @Transactional
    public void updateStockIdByRanking(String mkscShrnIscd, String dataRank) {

        int updatedRows = popularRepository.updateStockIdByRanking(mkscShrnIscd, dataRank);
        if (updatedRows == 0) {
            throw new RuntimeException("Failed to update stockId for ranking: " + dataRank);
        }
    }

    @KafkaListener(topics = "volume-rank-topic", groupId = "volume-rank-consumer-group")
    @Transactional
    public void consumeMessage(String message) {
        try {
            // Deserialize JSON to DTO
            ResponseOutputDTO dto = objectMapper.readValue(message, ResponseOutputDTO.class);
            // Save to MySQL
            String dataRank = dto.getDataRank();
            String mkscShrnIscd = dto.getMkscShrnIscd();

            Optional<Popular> popular = popularRepository.findByRanking(dataRank);
            if (popular.isPresent()) {
                PopularDto popularDto = new PopularDto(popular.get());

                if (!popularDto.getStockId().equals(mkscShrnIscd)) {
                    updateStockIdByRanking(mkscShrnIscd, dataRank);
                    //System.out.println("DIFFERENT with MySQL Data"+dataRank+mkscShrnIscd);
                }
            }

            //System.out.println("Saved to MySQL. Time: " + LocalTime.now());
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}
