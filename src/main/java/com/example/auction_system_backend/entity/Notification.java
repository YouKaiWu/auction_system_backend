package com.example.auction_system_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("notification")
@Data
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String type;   // WIN_AUCTION / OUTBID / SYSTEM

    private String title;

    private String content;

    private LocalDateTime sentAt;
}