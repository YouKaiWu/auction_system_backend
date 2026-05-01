package com.example.auction_system_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.bid.BidRequest;
import com.example.auction_system_backend.DTO.bid.BidResponse;
import com.example.auction_system_backend.DTO.bid.BidStatusResponse;
import com.example.auction_system_backend.entity.Bid;
import com.example.auction_system_backend.mapper.BidMapper;
import com.example.auction_system_backend.mapper.ItemMapper;
import com.example.auction_system_backend.mapper.UserMapper;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    private final BidMapper bidMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    public BidService(BidMapper bidMapper,
            ItemMapper itemMapper,
            UserMapper userMapper) {
        this.bidMapper = bidMapper;
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public BidResponse placeBid(BidRequest request) {

        // 1. validate input
        if (request.getBidPrice() == null) {
            throw new RuntimeException("bidPrice cannot be null");
        }

        // 2. check item exists
        if (itemMapper.selectById(request.getItemId()) == null) {
            throw new RuntimeException("Item not found");
        }

        // 3. 取得 userId（
        Long userId = getCurrentUserId();

        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("User not found");
        }

        // 4. 取得目前最高價
        Bid highestBid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, request.getItemId())
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        BigDecimal highestPrice = (highestBid == null)
                ? BigDecimal.ZERO
                : highestBid.getBidPrice();

        // 5. 驗證價格
        if (request.getBidPrice().compareTo(highestPrice) <= 0) {
            throw new RuntimeException("Bid must be higher than current highest bid");
        }

        // 6. insert bid
        Bid bid = new Bid();
        bid.setItemId(request.getItemId());
        bid.setBidderId(userId); // 🔥 改這裡
        bid.setBidPrice(request.getBidPrice());
        bid.setBidTime(LocalDateTime.now());

        bidMapper.insert(bid);

        return toResponse(bid);
    }

    public List<BidResponse> getItemBids(Long itemId) {

        List<Bid> list = bidMapper.selectList(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice));

        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BidResponse getHighestBid(Long itemId) {

        Bid bid = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        return bid == null ? null : toResponse(bid);
    }

    // DTO mapping
    private BidResponse toResponse(Bid bid) {
        BidResponse res = new BidResponse();
        res.setBidId(bid.getId());
        res.setItemId(bid.getItemId());
        res.setUserId(bid.getBidderId());
        res.setBidPrice(bid.getBidPrice());
        res.setCreatedAt(
                bid.getBidTime() != null ? bid.getBidTime().toString() : null);
        return res;
    }

    public List<BidResponse> getMyBids() {

        Long userId = getCurrentUserId();

        List<Bid> list = bidMapper.selectList(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getBidderId, userId)
                        .orderByDesc(Bid::getBidTime));

        return list.stream()
                .map(this::toResponse)
                .toList();
    }

    public BidStatusResponse getBidStatus(Long itemId) {

        // 1. highest bid
        Bid highest = bidMapper.selectOne(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId)
                        .orderByDesc(Bid::getBidPrice)
                        .last("LIMIT 1"));

        // 2. count bids
        Long count = bidMapper.selectCount(
                new LambdaQueryWrapper<Bid>()
                        .eq(Bid::getItemId, itemId));

        BidStatusResponse res = new BidStatusResponse();
        res.setItemId(itemId);
        res.setHighestBid(highest != null ? highest.getBidPrice() : BigDecimal.ZERO);
        res.setBidCount(count);

        return res;
    }

    // =========================
    // JWT userId
    // =========================
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}