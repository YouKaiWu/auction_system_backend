package com.example.auction_system_backend.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction_system_backend.DTO.item.CreateItemRequest;
import com.example.auction_system_backend.entity.Item;
import com.example.auction_system_backend.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void create(
            @RequestPart("item") CreateItemRequest req,
            @RequestPart("image") MultipartFile image) throws IOException {

 
        String uploadDir = System.getProperty("user.dir") + "/images/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // ✔ 2. 防呆（避免空檔名）
        String originalName = image.getOriginalFilename();
        if (originalName == null) {
            originalName = "file.png";
        }

        // ✔ 3. 檔名避免衝突
        String fileName = UUID.randomUUID() + "_" + originalName;

        File targetFile = new File(dir, fileName);

        // ✔ 4. 寫入檔案
        image.transferTo(targetFile);

        // ✔ 5. DB 存取路徑（給前端用）
        req.setImagePath("/images/" + fileName);

        itemService.create(req);
    }

    @GetMapping
    public List<Item> getItems(
            @RequestParam(required = false) Integer categoryId) {
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