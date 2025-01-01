package com.example.backend.config;

import com.example.backend.entity.Popular;
import com.example.backend.repository.PopularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private PopularRepository popularRepository;

    @Override
    public void run(String... args) {
        for (int i = 1; i <= 30; i++) {

            Popular popular = new Popular(String.valueOf(i), String.valueOf(i));

            popularRepository.save(popular);
        }
        System.out.println("Database initialized with values 1 to 30");
    }

}
