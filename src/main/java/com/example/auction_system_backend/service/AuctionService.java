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

    @Transactional
    public AuctionResultResponse closeAuction(Integer itemId) {

        // CAS 結標
        int updated = itemMapper.update(
                null,
                new LambdaUpdateWrapper<Item>()
                        .eq(Item::getId, itemId)
                        .eq(Item::getStatus, "ACTIVE")
                        .set(Item::getStatus, "ENDED"));

        // 已結標 or 不存在
        if (updated == 0) {

            AuctionResult existing = auctionResultMapper.selectOne(
                    new LambdaQueryWrapper<AuctionResult>()
                            .eq(AuctionResult::getItemId, itemId));

            if (existing == null) {
                throw new RuntimeException("Item not found or already closed");
            }

            return toResponse(existing);
        }

        //  查 item
        Item item = itemMapper.selectById(itemId);

        //  找最高 bid
        Bid highestBid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        Long winnerId = (highestBid != null) ? highestBid.getBidderId() : null;
        BigDecimal finalPrice = (highestBid != null)
                ? highestBid.getBidPrice()
                : BigDecimal.ZERO;

        //  寫 auction result
        AuctionResult result = new AuctionResult();
        result.setItemId(itemId);
        result.setWinnerId(winnerId);
        result.setFinalPrice(finalPrice);
        result.setClosedAt(LocalDateTime.now());

        try {
            auctionResultMapper.insert(result);

        } catch (org.springframework.dao.DuplicateKeyException e) {

            // 已經被別人 insert，直接回 existing
            AuctionResult existing = auctionResultMapper.selectOne(
                    new LambdaQueryWrapper<AuctionResult>()
                            .eq(AuctionResult::getItemId, itemId));

            return toResponse(existing);
        }

        //  通知（建議 async）
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

        if (winnerId == null) {
            return;
        }

        // 找 email
        String email = userMapper.selectById(winnerId).getEmail();

        // TODO: 寄信可用 mailtrap
        // 寄信
        // mailService.sendWinAuctionMail(
        // email,
        // item.getName(),
        // finalPrice.toString()
        // );

        // optional：log / notification table
        System.out.println("notify sent to " + email);
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