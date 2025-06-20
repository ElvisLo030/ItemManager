# 物品管理系統More actions

41211147 11402程式設計期中/期末實作

## 功能特點

- **新增物品**：添加新物品到系統中
- **修改物品**：更新現有物品的資訊
- **刪除物品**：從系統中移除物品
- **查詢物品**：根據關鍵字搜索物品
- **檔案儲存**：所有檔案自動保存到data.json檔案中

## 物品資訊

每個物品包含以下資訊：
- 品名
- 編號 (唯一識別碼，會自動產生)
- 加入時間 (自動記錄)
- 價格

## 系統需求

- Java 11 或更高版本
- Maven 3.6 或更高版本

## 如何編譯和運行

1. 克隆或下載此項目到本地
   ```
   git clone https://github.com/ElvisLo030/ItemManager
   ```

2. 使用Maven編譯項目
   ```
   mvn clean package
   ```

3. 運行應用程式
   ```
   java -jar target/inventory-management-system-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## 使用說明

1. **新增物品**
   - 填寫物品名稱和價格
   - 點擊「新增」按鈕

2. **修改物品**
   - 在表格中點擊要修改的物品
   - 修改表單中的資訊
   - 點擊「修改」按鈕

3. **刪除物品**
   - 在表格中點擊要刪除的物品
   - 點擊「刪除」按鈕
   - 確認刪除操作

4. **搜索物品**
   - 在搜索欄位輸入關鍵字
   - 點擊「搜索」按鈕
   - 表格將顯示匹配的結果

5. **清除表單**
   - 點擊「清除」按鈕可重置輸入欄位

## 檔案存儲

系統使用data.json檔案存儲所有物品資訊。此檔案將在應用程式首次啟動時自動創建，並在每次操作後自動更新。 

