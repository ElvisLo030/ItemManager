package com.inventory.controller;

import com.inventory.model.Item;
import com.inventory.utils.JsonHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemManager {
    private List<Item> items;
    private final JsonHandler jsonHandler;
    private static final String DATA_FILE = "data.json";

    public ItemManager() {
        this.jsonHandler = new JsonHandler();
        this.items = jsonHandler.loadItems(DATA_FILE);
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
    }

    public boolean addItem(Item item) {
        // 檢查編號是否已存在
        if (getItemByCode(item.getCode()).isPresent()) {
            return false;
        }
        items.add(item);
        saveItems();
        return true;
    }

    public boolean updateItem(String code, Item updatedItem) {
        Optional<Item> existingItem = getItemByCode(code);
        if (existingItem.isPresent()) {
            Item item = existingItem.get();
            item.setName(updatedItem.getName());
            item.setPrice(updatedItem.getPrice());
            // 保留原始的加入時間
            saveItems();
            return true;
        }
        return false;
    }

    public boolean deleteItem(String code) {
        Optional<Item> item = getItemByCode(code);
        if (item.isPresent()) {
            items.remove(item.get());
            saveItems();
            return true;
        }
        return false;
    }

    public Optional<Item> getItemByCode(String code) {
        return items.stream()
                .filter(item -> item.getCode().equals(code))
                .findFirst();
    }

    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(items);
        }
        
        String lowerKeyword = keyword.toLowerCase();
        return items.stream()
                .filter(item -> 
                    item.getName().toLowerCase().contains(lowerKeyword) || 
                    item.getCode().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    private void saveItems() {
        jsonHandler.saveItems(items, DATA_FILE);
    }
} 