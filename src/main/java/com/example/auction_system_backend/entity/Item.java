package com.example.auction_system_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("item")
public class Item {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Long ownerId;

    private Integer categoryId;

    private BigDecimal startingPrice;

    private BigDecimal currentPrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String imagePath;
}