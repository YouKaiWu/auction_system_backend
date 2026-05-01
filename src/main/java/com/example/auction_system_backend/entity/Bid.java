package com.example.auction_system_backend.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("bid")
@Data
public class Bid {

    @TableId(type = IdType.AUTO) // PostgreSQL SERIAL / IDENTITY
    private Long id;

    private Long itemId;

    private Long bidderId;

    private BigDecimal bidPrice;

    private LocalDateTime bidTime;

}