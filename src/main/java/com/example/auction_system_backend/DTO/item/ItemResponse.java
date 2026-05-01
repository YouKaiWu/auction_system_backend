package com.example.auction_system_backend.DTO.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ItemResponse {
    private Integer id;
    private String name;
    private Integer ownerId;
    private Integer categoryId;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String imagePath;
}