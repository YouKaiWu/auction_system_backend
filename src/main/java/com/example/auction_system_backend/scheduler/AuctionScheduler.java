package com.example.auction_system_backend.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.mapper.ItemMapper;
import com.example.auction_system_backend.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    private final ItemMapper itemMapper;
    private final AuctionService auctionService;

    public AuctionScheduler(ItemMapper itemMapper,
                            AuctionService auctionService) {
        this.itemMapper = itemMapper;
        this.auctionService = auctionService;
    }

    @Scheduled(fixedRate = 60000)
    public void autoCloseAuction() {

        // 1. 找出所有到期未結標 item
        List<Item> expiredItems = itemMapper.selectList(
                new LambdaQueryWrapper<Item>()
                        .lt(Item::getEndTime, LocalDateTime.now())
                        .eq(Item::getStatus, "ACTIVE")
        );

        // 2. 逐一結標
        for (Item item : expiredItems) {
            try {
                auctionService.closeAuction(item.getId());
            } catch (Exception e) {
                System.out.println("Close auction failed: " + item.getId());
                e.printStackTrace();
            }
        }
    }
}