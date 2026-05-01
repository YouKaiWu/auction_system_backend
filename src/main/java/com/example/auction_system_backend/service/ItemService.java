package com.example.auction_system_backend.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.auction_system_backend.DTO.item.CreateItemRequest;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.mapper.ItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;

    public void create(CreateItemRequest req) {

        Long userId = getCurrentUserId();

        Item item = new Item();
        item.setName(req.getName());
        item.setOwnerId(userId);
        item.setCategoryId(req.getCategoryId());
        item.setStartingPrice(req.getStartingPrice());

        // 初始化
        item.setCurrentPrice(req.getStartingPrice());
        item.setStartTime(req.getStartTime());
        item.setEndTime(req.getEndTime());
        item.setStatus("ACTIVE");
        item.setImagePath(req.getImagePath());

        itemMapper.insert(item);
    }

    public List<Item> getItems(Integer categoryId) {

        LambdaQueryWrapper<Item> qw = new LambdaQueryWrapper<>();

        qw.eq(Item::getStatus, "ACTIVE");

        if (categoryId != null) {
            qw.eq(Item::getCategoryId, categoryId);
        }

        return itemMapper.selectList(qw);
    }

    public Item getDetail(Integer id) {
        return itemMapper.selectById(id);
    }

    public List<Item> getMyItems() {

        Long userId = getCurrentUserId();

        return itemMapper.selectList(
                new LambdaQueryWrapper<Item>()
                        .eq(Item::getOwnerId, userId));
    }

    // =========================
    // JWT userId
    // =========================
    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}