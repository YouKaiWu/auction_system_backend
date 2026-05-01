package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.notification.NotificationResponse;
import com.example.auction_system_backend.entity.Notification;
import com.example.auction_system_backend.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    /**
     * 📥 取得使用者通知
     */
    public List<NotificationResponse> getNotifications(Long userId) {

        List<Notification> list = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getSentAt)
        );

        return list.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 📤 建立通知（給其他 service 用）
     */
    public void createNotification(Long userId,
                                   String type,
                                   String title,
                                   String content) {

        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content);

        notificationMapper.insert(n);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setType(n.getType());
        res.setTitle(n.getTitle());
        res.setContent(n.getContent());
        res.setSentAt(n.getSentAt());
        return res;
    }
}