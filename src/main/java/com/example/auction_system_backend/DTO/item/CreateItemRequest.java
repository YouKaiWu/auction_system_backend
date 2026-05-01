package com.example.auction_system_backend.DTO.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateItemRequest {
    private String name;
    private Integer categoryId;
    private BigDecimal startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String imagePath;
}