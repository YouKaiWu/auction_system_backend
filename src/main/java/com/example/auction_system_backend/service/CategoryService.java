package com.example.auction_system_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.auction_system_backend.entity.Category;
import com.example.auction_system_backend.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }
}