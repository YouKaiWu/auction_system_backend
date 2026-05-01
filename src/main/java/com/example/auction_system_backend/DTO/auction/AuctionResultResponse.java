package com.example.auction_system_backend.DTO.auction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AuctionResultResponse {

    private Long itemId;

    private Long winnerUserId;

    private BigDecimal finalPrice;

    private String status; // CLOSED / ACTIVE
}