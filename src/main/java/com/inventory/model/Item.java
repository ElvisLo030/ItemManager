package com.inventory.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Item {
    private String name;
    private String code;
    private LocalDateTime addedTime;
    private double price;

    public Item() {
        this.addedTime = LocalDateTime.now();
    }

    public Item(String name, String code, double price) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.addedTime = LocalDateTime.now();
    }

    public Item(String name, String code, LocalDateTime addedTime, double price) {
        this.name = name;
        this.code = code;
        this.addedTime = addedTime;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(LocalDateTime addedTime) {
        this.addedTime = addedTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFormattedAddedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return addedTime.format(formatter);
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", addedTime=" + getFormattedAddedTime() +
                ", price=" + price +
                '}';
    }
} 