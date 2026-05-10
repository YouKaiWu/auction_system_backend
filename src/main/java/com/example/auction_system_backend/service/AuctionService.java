package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.auction_system_backend.DTO.auction.AuctionResultResponse;
import com.example.auction_system_backend.entity.AuctionResult;
import com.example.auction_system_backend.entity.Bid;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.mapper.AuctionResultMapper;
import com.example.auction_system_backend.mapper.BidMapper;
import com.example.auction_system_backend.mapper.ItemMapper;
import com.example.auction_system_backend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final ItemMapper itemMapper;
    private final BidMapper bidMapper;
    private final AuctionResultMapper auctionResultMapper;

    private final MailService mailService;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Transactional
    public AuctionResultResponse closeAuction(Integer itemId) {

        int updated = itemMapper.update(
                null,
                new LambdaUpdateWrapper<Item>()
                        .eq(Item::getId, itemId)
                        .eq(Item::getStatus, "ACTIVE")
                        .set(Item::getStatus, "ENDED"));

        if (updated == 0) {

            AuctionResult existing = auctionResultMapper.selectOne(
                    new LambdaQueryWrapper<AuctionResult>()
                            .eq(AuctionResult::getItemId, itemId));

            if (existing == null) {
                throw new RuntimeException("Item not found or already closed");
            }

            return toResponse(existing);
        }

        Item item = itemMapper.selectById(itemId);

        Bid highestBid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        // 沒人出價 → 流標處理
        if (highestBid == null) {

            AuctionResult result = new AuctionResult();
            result.setItemId(itemId);
            result.setWinnerId(null);
            result.setFinalPrice(BigDecimal.ZERO);
            result.setClosedAt(LocalDateTime.now());

            auctionResultMapper.insert(result);

            notificationService.createNotification(
                    null,
                    "SYSTEM",
                    "Auction Ended",
                    "Item " + item.getName() + " ended with no bids");

            return toResponse(result);
        }

        Long winnerId = highestBid.getBidderId();
        BigDecimal finalPrice = highestBid.getBidPrice();

        AuctionResult result = new AuctionResult();
        result.setItemId(itemId);
        result.setWinnerId(winnerId);
        result.setFinalPrice(finalPrice);
        result.setClosedAt(LocalDateTime.now());

        try {
            auctionResultMapper.insert(result);
        } catch (org.springframework.dao.DuplicateKeyException e) {

            AuctionResult existing = auctionResultMapper.selectOne(
                    new LambdaQueryWrapper<AuctionResult>()
                            .eq(AuctionResult::getItemId, itemId));

            return toResponse(existing);
        }

        notifyWinner(winnerId, item, finalPrice);

        return toResponse(result);
    }

    public AuctionResultResponse getAuctionResult(Long itemId) {

        AuctionResult result = auctionResultMapper.selectOne(
                new LambdaQueryWrapper<AuctionResult>()
                        .eq(AuctionResult::getItemId, itemId));

        if (result == null) {
            throw new RuntimeException("Auction not closed yet");
        }

        return toResponse(result);
    }

    private void notifyWinner(Long winnerId, Item item, BigDecimal finalPrice) {

        // 沒得標直接跳過
        if (winnerId == null) {
            return;
        }

        // 取得使用者 email
        String email = userMapper.selectById(winnerId).getEmail();

        // 通知標題與內容
        String title = "Auction Result";
        String content = "You won the auction for item: "
                + item.getName()
                + ", final price: "
                + finalPrice;

        // 寫入 notification table
        notificationService.createNotification(
                winnerId,
                "WIN_AUCTION",
                title,
                content);

        // 寄 email（新增）
        try {
            mailService.sendWinAuctionMail(
                    email,
                    item.getName(),
                    finalPrice.toString());
        } catch (Exception e) {
            // email 失敗不影響主流程
            System.out.println("Email send failed: " + email);
            e.printStackTrace();
        }
    }

    // DTO mapping
    private AuctionResultResponse toResponse(AuctionResult r) {

        AuctionResultResponse res = new AuctionResultResponse();
        res.setItemId(r.getItemId());
        res.setWinnerUserId(r.getWinnerId());
        res.setFinalPrice(r.getFinalPrice());
        res.setStatus("ENDED");
        return res;
    }
}