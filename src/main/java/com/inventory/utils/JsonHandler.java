package com.inventory.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.inventory.model.Item;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {
    private final Gson gson;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JsonHandler() {
        // 設定Gson以適當處理LocalDateTime
        GsonBuilder gsonBuilder = new GsonBuilder();
        
        // 序列化LocalDateTime
        JsonSerializer<LocalDateTime> serializer = (src, typeOfSrc, context) -> 
            new JsonPrimitive(formatter.format(src));
            
        // 反序列化LocalDateTime
        JsonDeserializer<LocalDateTime> deserializer = (json, typeOfT, context) -> 
            LocalDateTime.parse(json.getAsString(), formatter);
            
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, serializer);
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, deserializer);
        gsonBuilder.setPrettyPrinting();
        
        this.gson = gsonBuilder.create();
    }

    public List<Item> loadItems(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type itemListType = new TypeToken<ArrayList<Item>>(){}.getType();
            return gson.fromJson(reader, itemListType);
        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveItems(List<Item> items, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            System.err.println("寫入檔案時發生錯誤: " + e.getMessage());
        }
    }
} 