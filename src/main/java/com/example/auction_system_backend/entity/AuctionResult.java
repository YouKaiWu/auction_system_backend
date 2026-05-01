package com.example.auction_system_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("auction_result")
@Data
public class AuctionResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itemId;

    private Long winnerId;

    private BigDecimal finalPrice;

    private LocalDateTime closedAt;
}