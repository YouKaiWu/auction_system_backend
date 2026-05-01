package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.auction.AuctionResultResponse;
import com.example.auction_system_backend.entity.AuctionResult;
import com.example.auction_system_backend.entity.Bid;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.mapper.AuctionResultMapper;
import com.example.auction_system_backend.mapper.BidMapper;
import com.example.auction_system_backend.mapper.ItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AuctionService {

    private final ItemMapper itemMapper;
    private final BidMapper bidMapper;
    private final AuctionResultMapper auctionResultMapper;

    public AuctionService(ItemMapper itemMapper,
                          BidMapper bidMapper,
                          AuctionResultMapper auctionResultMapper) {
        this.itemMapper = itemMapper;
        this.bidMapper = bidMapper;
        this.auctionResultMapper = auctionResultMapper;
    }

    /**
     * 🛑 結標（手動 / scheduler 共用）
     */
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
                        .eq(AuctionResult::getItemId, itemId)
        );

        if (existingResult != null) {
            return toResponse(existingResult);
        }

        // 3. 找最高 bid
        Bid highestBid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1")
        );

        Long winnerId = null;
        BigDecimal finalPrice = BigDecimal.ZERO;

        if (highestBid != null) {
            winnerId = highestBid.getBidderId();
            finalPrice = highestBid.getBidPrice();
        }

        // 4. 更新 item 狀態
        item.setStatus("CLOSED");
        itemMapper.updateById(item);

        // 5. 寫入 auction_result
        AuctionResult result = new AuctionResult();
        result.setItemId(itemId);
        result.setWinnerId(winnerId);
        result.setFinalPrice(finalPrice);
        result.setClosedAt(LocalDateTime.now());

        auctionResultMapper.insert(result);

        // 6. 通知（先 stub，之後接 Notification / Mail / WS）
        notifyWinner(winnerId, item);

        return toResponse(result);
    }

    /**
     * 🏁 查結標結果
     */
    public AuctionResultResponse getAuctionResult(Long itemId) {

        AuctionResult result = auctionResultMapper.selectOne(
                new LambdaQueryWrapper<AuctionResult>()
                        .eq(AuctionResult::getItemId, itemId)
        );

        if (result == null) {
            throw new RuntimeException("Auction not closed yet");
        }

        return toResponse(result);
    }

    /**
     * 📩 通知 winner（先 stub）
     */
    private void notifyWinner(Long winnerId, Item item) {

        if (winnerId == null) {
            return; // 沒人出價
        }

        // TODO: NotificationService / MailService / WebSocket
        System.out.println("🏆 User " + winnerId +
                " won item: " + item.getName());
    }

    /**
     * 🔁 DTO mapping
     */
    private AuctionResultResponse toResponse(AuctionResult r) {

        AuctionResultResponse res = new AuctionResultResponse();
        res.setItemId(r.getItemId());
        res.setWinnerUserId(r.getWinnerId());
        res.setFinalPrice(r.getFinalPrice());
        res.setStatus("CLOSED");
        return res;
    }
}