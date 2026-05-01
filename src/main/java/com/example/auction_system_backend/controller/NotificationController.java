package com.example.auction_system_backend.controller;

import com.example.auction_system_backend.DTO.notification.NotificationResponse;
import com.example.auction_system_backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 📬 查通知
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {

        Long userId = getCurrentUserId(); // 🔥 JWT 之後替換

        return ResponseEntity.ok(
                notificationService.getNotifications(userId));
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}