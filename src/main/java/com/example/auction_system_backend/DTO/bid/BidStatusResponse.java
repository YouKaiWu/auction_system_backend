package com.example.auction_system_backend.DTO.bid;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidStatusResponse {
    private Long itemId;
    private BigDecimal highestBid;
    private Long bidCount;
}