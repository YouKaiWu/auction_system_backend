package com.example.auction_system_backend.DTO.message;
import lombok.Data;

@Data
public class MessageRequest {
    private String content;
    private Integer rating;
}