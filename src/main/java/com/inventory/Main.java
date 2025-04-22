package com.inventory;

import com.inventory.ui.ItemManagementUI;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // 設置系統外觀
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // 設置黑暗主題
            setupDarkTheme();
            
            // 確保按鈕可以正確顯示自定義顏色
            UIManager.put("Button.background", UIManager.getColor("control"));
            UIManager.put("Button.select", UIManager.getColor("control"));
            UIManager.put("Button.focus", UIManager.getColor("controlHighlight"));
            UIManager.put("Button.opaque", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ItemManagementUI app = new ItemManagementUI();
            app.setVisible(true);
        });
    }
    
    /**
     * 設置全局黑暗主題
     */
    private static void setupDarkTheme() {
        // 設置整體背景為黑色
        UIManager.put("Panel.background", new Color(30, 30, 30));
        UIManager.put("OptionPane.background", new Color(30, 30, 30));
        UIManager.put("OptionPane.messageForeground", new Color(220, 220, 220));
        
        // 表格相關
        UIManager.put("Table.background", new Color(40, 40, 40));
        UIManager.put("Table.foreground", new Color(220, 220, 220));
        UIManager.put("Table.selectionBackground", new Color(60, 80, 100));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(70, 70, 70));
        UIManager.put("TableHeader.background", new Color(50, 50, 50));
        UIManager.put("TableHeader.foreground", new Color(220, 220, 220));
        
        // 文本框和標籤
        UIManager.put("TextField.background", new Color(50, 50, 50));
        UIManager.put("TextField.foreground", new Color(220, 220, 220));
        UIManager.put("TextField.caretForeground", Color.WHITE);
        UIManager.put("TextField.selectionBackground", new Color(60, 80, 100));
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("Label.foreground", new Color(220, 220, 220));
        
        // 滾動面板
        UIManager.put("ScrollPane.background", new Color(40, 40, 40));
        UIManager.put("ScrollBar.track", new Color(30, 30, 30));
        UIManager.put("ScrollBar.thumb", new Color(100, 100, 100));
        UIManager.put("ScrollBar.thumbDarkShadow", new Color(30, 30, 30));
        UIManager.put("ScrollBar.thumbHighlight", new Color(100, 100, 100));
        UIManager.put("ScrollBar.thumbShadow", new Color(50, 50, 50));
        
        // 對話框
        UIManager.put("OptionPane.background", new Color(40, 40, 40));
        UIManager.put("OptionPane.messageForeground", new Color(220, 220, 220));
        UIManager.put("OptionPane.buttonBackground", new Color(60, 60, 60));
        UIManager.put("OptionPane.buttonForeground", new Color(220, 220, 220));
        
        // 全局字體設置
        Font defaultFont = new Font("微軟正黑體", Font.PLAIN, 14);
        UIManager.put("Button.font", new Font("微軟正黑體", Font.BOLD, 14));
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", new Font("微軟正黑體", Font.BOLD, 14));
        UIManager.put("OptionPane.messageFont", defaultFont);
        UIManager.put("OptionPane.buttonFont", new Font("微軟正黑體", Font.BOLD, 14));
    }
} 