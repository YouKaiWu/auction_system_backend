package com.example.auction_system_backend.DTO.notification;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private Long id;

    private String type;

    private String title;

    private String content;

    private LocalDateTime sentAt;
}