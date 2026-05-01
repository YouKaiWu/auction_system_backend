package com.example.auction_system_backend.DTO.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private Long userId;     // target
    private Long senderId;   // author
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
}