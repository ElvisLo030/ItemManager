package com.inventory.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.inventory.model.Item;
import com.inventory.utils.JsonHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ItemManager {
    private List<Item> items;
    private final JsonHandler jsonHandler;
    private static final String DATA_FILE = "data.json";
    private static final String IMAGE_DIR = "data"; // 圖片儲存目錄

    public ItemManager() {
        this.jsonHandler = new JsonHandler();
        this.items = jsonHandler.loadItems(DATA_FILE);
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        // 確保圖片目錄存在
        createImageDirectory();
    }

    // 確保圖片儲存目錄存在
    private void createImageDirectory() {
        File imageDir = new File(IMAGE_DIR);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }

    // 複製圖片到data目錄並返回新的檔名
    public String copyImageToDataDir(String sourceImagePath) throws IOException {
        if (sourceImagePath == null || sourceImagePath.trim().isEmpty()) {
            return "";
        }

        Path sourcePath = Paths.get(sourceImagePath);
        if (!Files.exists(sourcePath)) {
            throw new IOException("來源圖片檔案不存在: " + sourceImagePath);
        }

        // 取得檔案副檔名
        String fileName = sourcePath.getFileName().toString();
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
        }

        // 產生唯一檔名
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(IMAGE_DIR, uniqueFileName);

        // 複製檔案
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFileName;
    }

    // 刪除圖片檔案
    private void deleteImageFile(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            Path imageFile = Paths.get(IMAGE_DIR, imagePath);
            try {
                Files.deleteIfExists(imageFile);
            } catch (IOException e) {
                System.err.println("刪除圖片檔案時發生錯誤: " + e.getMessage());
            }
        }
    }

    // 自動產生編號
    private String generateAutoCode() {
        int maxNumber = 0;
        String prefix = "ITEM";
        
        // 找出現有編號中的最大數字
        for (Item item : items) {
            String code = item.getCode();
            if (code.startsWith(prefix)) {
                try {
                    String numberPart = code.substring(prefix.length());
                    int number = Integer.parseInt(numberPart);
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // 忽略無法解析的編號
                }
            }
        }
        
        // 產生新編號
        return prefix + String.format("%04d", maxNumber + 1);
    }

    // 新增物品的方法，自動產生編號
    public boolean addItem(String name, double price, String imagePath) {
        String autoCode = generateAutoCode();
        Item item = new Item(name, autoCode, price, imagePath);
        items.add(item);
        saveItems();
        return true;
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
            
            // 如果圖片路徑有變更，需要處理舊圖片的刪除
            String oldImagePath = item.getImagePath();
            String newImagePath = updatedItem.getImagePath();
            
            item.setName(updatedItem.getName());
            item.setPrice(updatedItem.getPrice());
            item.setImagePath(newImagePath);
            // 保留原始的加入時間
            
            // 如果有舊圖片且與新圖片不同，刪除舊圖片
            if (oldImagePath != null && !oldImagePath.equals(newImagePath) && !oldImagePath.trim().isEmpty()) {
                deleteImageFile(oldImagePath);
            }
            
            saveItems();
            return true;
        }
        return false;
    }

    public boolean deleteItem(String code) {
        Optional<Item> item = getItemByCode(code);
        if (item.isPresent()) {
            // 刪除關聯的圖片檔案
            deleteImageFile(item.get().getImagePath());
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

    // 匯出資料到Excel檔案
    public boolean exportToExcel(String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("物品清單");
            
            // 建立標題樣式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            
            // 建立資料樣式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            
            // 建立標題行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"品名", "編號", "加入時間", "價格", "圖片狀態"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填入資料
            int rowNum = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                
                // 品名
                Cell nameCell = row.createCell(0);
                nameCell.setCellValue(item.getName());
                nameCell.setCellStyle(dataStyle);
                
                // 編號
                Cell codeCell = row.createCell(1);
                codeCell.setCellValue(item.getCode());
                codeCell.setCellStyle(dataStyle);
                
                // 加入時間
                Cell timeCell = row.createCell(2);
                timeCell.setCellValue(item.getFormattedAddedTime());
                timeCell.setCellStyle(dataStyle);
                
                // 價格
                Cell priceCell = row.createCell(3);
                priceCell.setCellValue(item.getPrice());
                priceCell.setCellStyle(dataStyle);
                
                // 圖片狀態
                Cell imageCell = row.createCell(4);
                String imageStatus = (item.getImagePath() != null && !item.getImagePath().trim().isEmpty()) 
                    ? "有圖片" : "無圖片";
                imageCell.setCellValue(imageStatus);
                imageCell.setCellStyle(dataStyle);
            }
            
            // 自動調整欄寬
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 確保最小寬度
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }
            
            // 新增摘要資訊
            int summaryRowStart = rowNum + 2;
            Row summaryTitleRow = sheet.createRow(summaryRowStart);
            Cell summaryTitleCell = summaryTitleRow.createCell(0);
            summaryTitleCell.setCellValue("統計摘要");
            summaryTitleCell.setCellStyle(headerStyle);
            
            Row totalCountRow = sheet.createRow(summaryRowStart + 1);
            totalCountRow.createCell(0).setCellValue("總物品數量:");
            totalCountRow.createCell(1).setCellValue(items.size());
            
            Row withImageRow = sheet.createRow(summaryRowStart + 2);
            withImageRow.createCell(0).setCellValue("有圖片物品:");
            long withImageCount = items.stream().filter(item -> 
                item.getImagePath() != null && !item.getImagePath().trim().isEmpty()).count();
            withImageRow.createCell(1).setCellValue(withImageCount);
            
            Row withoutImageRow = sheet.createRow(summaryRowStart + 3);
            withoutImageRow.createCell(0).setCellValue("無圖片物品:");
            withoutImageRow.createCell(1).setCellValue(items.size() - withImageCount);
            
            Row exportTimeRow = sheet.createRow(summaryRowStart + 4);
            exportTimeRow.createCell(0).setCellValue("匯出時間:");
            exportTimeRow.createCell(1).setCellValue(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 寫入檔案
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("匯出Excel檔案時發生錯誤: " + e.getMessage());
            return false;
        }
    }
} 