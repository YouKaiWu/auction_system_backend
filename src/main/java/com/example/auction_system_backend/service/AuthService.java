package com.example.auction_system_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.auth.LoginRequest;
import com.example.auction_system_backend.DTO.auth.RegisterRequest;
import com.example.auction_system_backend.entity.User;
import com.example.auction_system_backend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getAccount, req.getAccount());

        if (userMapper.selectOne(qw) != null) {
            throw new RuntimeException("Account already exists");
        }

        User user = new User();
        user.setAccount(req.getAccount());
        user.setPasswd(passwordEncoder.encode(req.getPasswd()));
        user.setIdNum(req.getIdNum());
        user.setPhoneNum(req.getPhoneNum());
        user.setEmail(req.getEmail());

        userMapper.insert(user);
    }

    public String login(LoginRequest req) {

        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getAccount, req.getAccount());

        User user = userMapper.selectOne(qw);

        if (user == null ||
                !passwordEncoder.matches(req.getPasswd(), user.getPasswd())) {

            throw new RuntimeException("Login failed");
        }

        return jwtService.generateToken(user.getId());
    }

    public User getMe(Long userId) {
        return userMapper.selectById(userId);
    }
}
