package com.example.auction_system_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
  
    public void sendMail(String to,
                         String subject,
                         String content) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        message.setFrom("auctionsystem@gmail.com");

        mailSender.send(message);
    }

    public void sendWinAuctionMail(String email,
                                   String itemName,
                                   String price) {

        sendMail(
                email,
                "🎉 恭喜你得標了！",
                "你已成功得標商品：" + itemName +
                        "\n得標價格：" + price
        );
    }

    public void sendOutbidMail(String email,
                              String itemName,
                              String newPrice) {

        sendMail(
                email,
                "⚠️ 你的出價已被超越",
                "商品：" + itemName +
                        "\n目前最高價：" + newPrice +
                        "\n請盡快重新出價"
        );
    }
}