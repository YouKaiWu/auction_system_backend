package com.example.auction_system_backend.controller;

import com.example.auction_system_backend.DTO.bid.BidRequest;
import com.example.auction_system_backend.DTO.bid.BidResponse;
import com.example.auction_system_backend.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * 出價
     */
    @PostMapping("/bids")
    public ResponseEntity<BidResponse> placeBid(@RequestBody BidRequest request) {
        BidResponse response = bidService.placeBid(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查某商品所有出價
     */
    @GetMapping("/items/{itemId}/bids")
    public ResponseEntity<List<BidResponse>> getItemBids(@PathVariable Long itemId) {
        List<BidResponse> bids = bidService.getItemBids(itemId);
        return ResponseEntity.ok(bids);
    }

    /**
     * 查最高出價
     */
    @GetMapping("/items/{itemId}/highest-bid")
    public ResponseEntity<BidResponse> getHighestBid(@PathVariable Long itemId) {
        BidResponse highestBid = bidService.getHighestBid(itemId);
        return ResponseEntity.ok(highestBid);
    }

    /**
     * 查自己的出價紀錄（建議一定要有）
     */
    @GetMapping("/users/me/bids")
    public ResponseEntity<List<BidResponse>> getMyBids() {
        List<BidResponse> myBids = bidService.getMyBids();
        return ResponseEntity.ok(myBids);
    }

    /**
     * 查商品 bid 狀態（給前端即時更新用）
     */
    @GetMapping("/items/{itemId}/bid-status")
    public ResponseEntity<?> getBidStatus(@PathVariable Long itemId) {
        return ResponseEntity.ok(bidService.getBidStatus(itemId));
    }
}