package com.example.auction_system_backend.controller;

import com.example.auction_system_backend.DTO.auction.AuctionResultResponse;
import com.example.auction_system_backend.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * 🛑 手動結標（admin / debug / 測試用）
     */
    @PostMapping("/{itemId}/close")
    public ResponseEntity<AuctionResultResponse> closeAuction(
            @PathVariable Integer itemId
    ) {
        return ResponseEntity.ok(
                auctionService.closeAuction(itemId)
        );
    }

    /**
     * 🏁 查結標結果（winner / price / item info）
     */
    @GetMapping("/{itemId}/result")
    public ResponseEntity<AuctionResultResponse> getResult(
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                auctionService.getAuctionResult(itemId)
        );
    }
}