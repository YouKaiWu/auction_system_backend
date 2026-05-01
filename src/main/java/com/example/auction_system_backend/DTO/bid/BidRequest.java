package com.example.auction_system_backend.DTO.bid;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequest {

    private Long itemId;

    private Long bidderId;

    private BigDecimal bidPrice;
}