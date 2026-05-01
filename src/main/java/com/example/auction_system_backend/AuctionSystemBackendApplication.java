package com.example.auction_system_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.auction_system_backend.mapper")
public class AuctionSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionSystemBackendApplication.class, args);
    }
}