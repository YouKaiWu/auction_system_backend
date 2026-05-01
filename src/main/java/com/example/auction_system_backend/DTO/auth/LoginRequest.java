package com.example.auction_system_backend.DTO.auth;

import lombok.Data;

@Data
public class LoginRequest {

    private String account;
    private String passwd;
}