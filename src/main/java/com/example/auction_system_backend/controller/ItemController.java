package com.example.auction_system_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.auction_system_backend.DTO.item.CreateItemRequest;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // ======================
    // 建立商品（賣家）
    // ======================
    @PostMapping
    public void create(@RequestBody CreateItemRequest req) {
        itemService.create(req);
    }

    // ======================
    // 查詢商品（買家）
    // GET /api/items?categoryId=1
    // ======================
    @GetMapping
    public List<Item> getItems(
            @RequestParam(required = false) Integer categoryId
    ) {
        return itemService.getItems(categoryId);
    }

    // ======================
    // 商品詳細
    // ======================
    @GetMapping("/{id}")
    public Item getDetail(@PathVariable Integer id) {
        return itemService.getDetail(id);
    }

    // ======================
    // 我的商品
    // ======================
    @GetMapping("/my")
    public List<Item> getMyItems() {
        return itemService.getMyItems();
    }
}