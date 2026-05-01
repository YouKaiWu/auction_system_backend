package com.example.auction_system_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.auction_system_backend.entity.Item;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}