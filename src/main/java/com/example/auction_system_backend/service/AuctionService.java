package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

        // 1. 查 item
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }

        // 2. 如果已經結標（防重複執行）
        AuctionResult existingResult = auctionResultMapper.selectOne(
                new LambdaQueryWrapper<AuctionResult>()
                        .eq(AuctionResult::getItemId, itemId));

        if (existingResult != null) {
            return toResponse(existingResult);
        }

        // 3. 找最高 bid
        Bid highestBid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        Long winnerId = null;
        BigDecimal finalPrice = BigDecimal.ZERO;

        if (highestBid != null) {
            winnerId = highestBid.getBidderId();
            finalPrice = highestBid.getBidPrice();
        }

        // 4. 更新 item 狀態
        item.setStatus("ENDED");
        itemMapper.updateById(item);

        // 5. 寫入 auction_result
        AuctionResult result = new AuctionResult();
        result.setItemId(itemId);
        result.setWinnerId(winnerId);
        result.setFinalPrice(finalPrice);
        result.setClosedAt(LocalDateTime.now());

        auctionResultMapper.insert(result);

        // 6. 通知（先 stub，之後接 Notification / Mail / WS）
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