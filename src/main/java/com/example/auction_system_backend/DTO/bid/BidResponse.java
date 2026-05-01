package com.example.auction_system_backend.DTO.bid;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidResponse {

    private Long bidId;

    private Long itemId;

    private Long userId;

    private BigDecimal bidPrice;

    private String createdAt;
}