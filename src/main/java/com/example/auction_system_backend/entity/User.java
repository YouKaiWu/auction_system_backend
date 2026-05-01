package com.example.auction_system_backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@TableName("users")
@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String account;

    private String passwd;

    @TableField("id_num")
    private String idNum;

    @TableField("phone_num")
    private String phoneNum;

    private String email;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
