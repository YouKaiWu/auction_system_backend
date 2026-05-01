package com.example.auction_system_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("user_message")
@Data
public class UserMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("target_user_id")
    private Long targetUserId;

    @TableField("author_user_id")
    private Long authorUserId;

    private String content;

    private Integer rating;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}