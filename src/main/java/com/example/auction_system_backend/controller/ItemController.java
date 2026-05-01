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

    @PostMapping
    public void create(@RequestBody CreateItemRequest req) {
        itemService.create(req);
    }

    @GetMapping
    public List<Item> getItems(
            @RequestParam(required = false) Integer categoryId
    ) {
        return itemService.getItems(categoryId);
    }

    @GetMapping("/{id}")
    public Item getDetail(@PathVariable Integer id) {
        return itemService.getDetail(id);
    }

    @GetMapping("/my")
    public List<Item> getMyItems() {
        return itemService.getMyItems();
    }
}