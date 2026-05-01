package com.example.auction_system_backend.DTO.auth;

import lombok.Data;

@Data
public class RegisterRequest {

    private String account;
    private String passwd;
    private String idNum;
    private String phoneNum;
    private String email;
}
