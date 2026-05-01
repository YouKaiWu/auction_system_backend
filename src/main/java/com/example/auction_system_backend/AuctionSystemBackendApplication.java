package com.example.auction_system_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.auction_system_backend.mapper")
@EnableScheduling
public class AuctionSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionSystemBackendApplication.class, args);
    }
}