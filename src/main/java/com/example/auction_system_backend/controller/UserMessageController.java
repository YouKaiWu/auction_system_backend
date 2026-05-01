package com.example.auction_system_backend.controller;

import com.example.auction_system_backend.DTO.message.MessageRequest;
import com.example.auction_system_backend.DTO.message.MessageResponse;
import com.example.auction_system_backend.service.UserMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserMessageController {

    private final UserMessageService messageService;

    public UserMessageController(UserMessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 📩 對使用者留言
     */
    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long id,
            @RequestBody MessageRequest request) {
        return ResponseEntity.ok(
                messageService.sendMessage(id, request));
    }

    /**
     * 📖 查看留言板
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                messageService.getMessages(id));
    }
}