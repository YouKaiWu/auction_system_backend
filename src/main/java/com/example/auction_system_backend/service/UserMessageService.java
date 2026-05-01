package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.message.MessageRequest;
import com.example.auction_system_backend.DTO.message.MessageResponse;
import com.example.auction_system_backend.entity.UserMessage;
import com.example.auction_system_backend.mapper.UserMessageMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMessageService {

    private final UserMessageMapper messageMapper;

    @Transactional
    public MessageResponse sendMessage(Long targetUserId, MessageRequest request) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new RuntimeException("content cannot be empty");
        }

        if (request.getRating() != null &&
                (request.getRating() < 1 || request.getRating() > 5)) {
            throw new RuntimeException("rating must be 1~5");
        }

        UserMessage msg = new UserMessage();
        msg.setTargetUserId(targetUserId);
        msg.setAuthorUserId(getCurrentUserId()); 
        msg.setContent(request.getContent());
        msg.setRating(request.getRating());
        msg.setCreatedAt(LocalDateTime.now());

        messageMapper.insert(msg);

        return toResponse(msg);
    }

    public List<MessageResponse> getMessages(Long targetUserId) {

        List<UserMessage> list = messageMapper.selectList(
                new LambdaQueryWrapper<UserMessage>()
                        .eq(UserMessage::getTargetUserId, targetUserId)
                        .orderByDesc(UserMessage::getCreatedAt)
        );

        return list.stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(UserMessage msg) {
        MessageResponse res = new MessageResponse();
        res.setId(msg.getId());
        res.setUserId(msg.getTargetUserId());
        res.setSenderId(msg.getAuthorUserId());
        res.setContent(msg.getContent());
        res.setRating(msg.getRating());
        res.setCreatedAt(msg.getCreatedAt());
        return res;
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}