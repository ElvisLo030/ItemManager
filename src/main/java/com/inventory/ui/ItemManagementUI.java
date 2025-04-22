package com.inventory.ui;

import com.inventory.controller.ItemManager;
import com.inventory.model.Item;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

public class ItemManagementUI extends JFrame {
    private JPanel contentPane;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private JTextField txtCode;
    private JTextField txtPrice;
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnClear;
    private JLabel lblStatus;

    private final ItemManager itemManager;
    private boolean isEditing = false;
    private String currentCode = "";

    // 表格欄位名稱
    private final String[] columns = {"品名", "編號", "加入時間", "價格"};

    public ItemManagementUI() {
        itemManager = new ItemManager();
        initializeUI();
        loadAllItems();
    }

    private void initializeUI() {
        setTitle("物品管理系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 650);  // 增加窗口大小
        
        // 設置更現代的配色方案
        Color bgDark = new Color(34, 34, 34);
        Color bgMedium = new Color(45, 45, 45);
        Color bgLight = new Color(55, 55, 55);
        Color accentMain = new Color(78, 100, 204);  // 主要強調色
        Color textNormal = new Color(220, 220, 220);
        Color textHighlight = new Color(255, 255, 255);
        
        // 設置全局UI屬性
        UIManager.put("Panel.background", bgDark);
        UIManager.put("Table.background", bgMedium);
        UIManager.put("Table.foreground", textNormal);
        UIManager.put("Table.selectionBackground", accentMain);
        UIManager.put("Table.selectionForeground", textHighlight);
        UIManager.put("TableHeader.background", bgDark);
        UIManager.put("TableHeader.foreground", textNormal);
        UIManager.put("TextField.background", bgLight);
        UIManager.put("TextField.foreground", textNormal);
        UIManager.put("Label.foreground", textNormal);
        
        // 創建圓角邊框
        Border roundedBorder = new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentMain);
                g2.drawRoundRect(x, y, width - 1, height - 1, 10, 10);
                g2.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 8, 5, 8);
            }
            
            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                insets.left = insets.top = insets.right = insets.bottom = 5;
                return insets;
            }
        };
        
        // 創建主面板
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        contentPane.setBackground(bgDark);
        setContentPane(contentPane);

        // 卡片式布局的頂部面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 15));
        topPanel.setBackground(bgMedium);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 3, 0, accentMain),  // 底部有強調色邊框
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // 創建標題標籤
        JLabel lblTitle = new JLabel("物品資訊");
        lblTitle.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        lblTitle.setForeground(textHighlight);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);
        
        // 輸入區域 - 使用網格布局
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(bgMedium);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 品名輸入
        JLabel lblName = new JLabel("品名:");
        lblName.setForeground(textNormal);
        lblName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(lblName, gbc);
        
        txtName = new JTextField(15);
        txtName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtName.setBackground(bgLight);
        txtName.setForeground(textNormal);
        txtName.setCaretColor(Color.WHITE);
        txtName.setBorder(BorderFactory.createCompoundBorder(
            roundedBorder,
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        inputPanel.add(txtName, gbc);
        
        // 編號輸入
        JLabel lblCode = new JLabel("編號:");
        lblCode.setForeground(textNormal);
        lblCode.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(lblCode, gbc);
        
        txtCode = new JTextField(10);
        txtCode.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtCode.setBackground(bgLight);
        txtCode.setForeground(textNormal);
        txtCode.setCaretColor(Color.WHITE);
        txtCode.setBorder(BorderFactory.createCompoundBorder(
            roundedBorder,
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.8;
        inputPanel.add(txtCode, gbc);
        
        // 價格輸入
        JLabel lblPrice = new JLabel("價格:");
        lblPrice.setForeground(textNormal);
        lblPrice.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(lblPrice, gbc);
        
        txtPrice = new JTextField(10);
        txtPrice.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtPrice.setBackground(bgLight);
        txtPrice.setForeground(textNormal);
        txtPrice.setCaretColor(Color.WHITE);
        txtPrice.setBorder(BorderFactory.createCompoundBorder(
            roundedBorder,
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.8;
        inputPanel.add(txtPrice, gbc);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        // 搜索面板 - 放在主面板的頂部，但右對齊
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(bgMedium);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        // 填充空間，使搜索框靠右
        searchPanel.add(Box.createHorizontalGlue());
        
        JLabel lblSearch = new JLabel("搜索:");
        lblSearch.setForeground(textNormal);
        lblSearch.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        searchPanel.add(lblSearch);
        searchPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtSearch.setBackground(bgLight);
        txtSearch.setForeground(textNormal);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setMaximumSize(new Dimension(200, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            roundedBorder,
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchPanel.add(txtSearch);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        btnSearch = new JButton("搜索");
        styleButton(btnSearch, new Color(103, 58, 183), textHighlight);
        searchPanel.add(btnSearch);
        
        // 操作按鈕面板 - 在頂部面板底部
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(bgMedium);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnAdd = new JButton("新增");
        styleButton(btnAdd, new Color(76, 175, 80), textHighlight);
        buttonPanel.add(btnAdd);
        
        btnUpdate = new JButton("修改");
        styleButton(btnUpdate, new Color(33, 150, 243), textHighlight);
        btnUpdate.setEnabled(false);
        buttonPanel.add(btnUpdate);
        
        btnDelete = new JButton("刪除");
        styleButton(btnDelete, new Color(244, 67, 54), textHighlight);
        btnDelete.setEnabled(false);
        buttonPanel.add(btnDelete);
        
        btnClear = new JButton("清除");
        styleButton(btnClear, new Color(158, 158, 158), textHighlight);
        buttonPanel.add(btnClear);
        
        // 狀態標籤
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(255, 152, 0));
        lblStatus.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(lblStatus);
        
        // 將所有面板組合到頂部
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(bgMedium);
        controlPanel.add(searchPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // 添加頂部面板到主面板
        contentPane.add(topPanel, BorderLayout.NORTH);
        
        // 創建表格
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 設置表格不可編輯
            }
        };
        
        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.setRowHeight(32);  // 增加行高
        itemTable.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        itemTable.setGridColor(new Color(70, 70, 70));
        itemTable.setIntercellSpacing(new Dimension(10, 5));  // 增加單元格間距
        itemTable.getTableHeader().setFont(new Font("微軟正黑體", Font.BOLD, 14));
        itemTable.setBackground(bgMedium);
        itemTable.setForeground(textNormal);
        itemTable.setShowGrid(false);  // 不顯示網格線
        itemTable.setShowHorizontalLines(true);  // 只顯示水平線
        
        // 設置表格列寬
        TableColumnModel columnModel = itemTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(250);  // 品名列寬
        columnModel.getColumn(1).setPreferredWidth(150);  // 編號列寬
        columnModel.getColumn(2).setPreferredWidth(200);  // 時間列寬
        columnModel.getColumn(3).setPreferredWidth(100);  // 價格列寬
        
        // 創建自定義的滾動面板
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.getViewport().setBackground(bgMedium);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // 添加表格到帶滾動條的面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(bgMedium);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentMain, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPane.add(tablePanel, BorderLayout.CENTER);
        
        // 註冊事件監聽器
        registerEventHandlers();
    }

    private void registerEventHandlers() {
        // 新增按鈕事件
        btnAdd.addActionListener(e -> {
            if (isEditing) {
                lblStatus.setText("請先取消編輯狀態");
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String code = txtCode.getText().trim();
                String priceText = txtPrice.getText().trim();
                
                // 表單驗證
                if (name.isEmpty() || code.isEmpty() || priceText.isEmpty()) {
                    lblStatus.setText("請填寫所有欄位");
                    return;
                }
                
                double price = Double.parseDouble(priceText);
                
                // 創建並添加新物品
                Item item = new Item(name, code, price);
                boolean success = itemManager.addItem(item);
                
                if (success) {
                    lblStatus.setText("物品新增成功");
                    clearForm();
                    loadAllItems();
                } else {
                    lblStatus.setText("編號已存在，無法新增");
                }
            } catch (NumberFormatException ex) {
                lblStatus.setText("價格必須是數字");
            }
        });
        
        // 修改按鈕事件
        btnUpdate.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                String priceText = txtPrice.getText().trim();
                
                // 表單驗證
                if (name.isEmpty() || priceText.isEmpty()) {
                    lblStatus.setText("請填寫所有欄位");
                    return;
                }
                
                double price = Double.parseDouble(priceText);
                
                // 創建更新的物品
                Item updatedItem = new Item(name, currentCode, price);
                boolean success = itemManager.updateItem(currentCode, updatedItem);
                
                if (success) {
                    lblStatus.setText("物品更新成功");
                    clearForm();
                    loadAllItems();
                    
                    // 重置編輯狀態
                    isEditing = false;
                    currentCode = "";
                    btnAdd.setEnabled(true);
                    txtCode.setEditable(true);
                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);
                } else {
                    lblStatus.setText("更新失敗，物品不存在");
                }
            } catch (NumberFormatException ex) {
                lblStatus.setText("價格必須是數字");
            }
        });
        
        // 刪除按鈕事件
        btnDelete.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "確定要刪除這個物品嗎？",
                "確認刪除",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                boolean success = itemManager.deleteItem(currentCode);
                
                if (success) {
                    lblStatus.setText("物品刪除成功");
                    clearForm();
                    loadAllItems();
                    
                    // 重置編輯狀態
                    isEditing = false;
                    currentCode = "";
                    btnAdd.setEnabled(true);
                    txtCode.setEditable(true);
                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);
                } else {
                    lblStatus.setText("刪除失敗，物品不存在");
                }
            }
        });
        
        // 搜索按鈕事件
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            List<Item> searchResults = itemManager.searchItems(keyword);
            updateTable(searchResults);
            lblStatus.setText("找到 " + searchResults.size() + " 個物品");
        });
        
        // 清除按鈕事件
        btnClear.addActionListener(e -> {
            clearForm();
            lblStatus.setText("");
            
            // 重置編輯狀態
            isEditing = false;
            currentCode = "";
            btnAdd.setEnabled(true);
            txtCode.setEditable(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        });
        
        // 表格點擊事件
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = itemTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String code = (String) tableModel.getValueAt(selectedRow, 1);
                    Optional<Item> itemOpt = itemManager.getItemByCode(code);
                    
                    if (itemOpt.isPresent()) {
                        Item item = itemOpt.get();
                        
                        // 填充表單
                        txtName.setText(item.getName());
                        txtCode.setText(item.getCode());
                        txtPrice.setText(String.valueOf(item.getPrice()));
                        
                        // 設置編輯狀態
                        isEditing = true;
                        currentCode = item.getCode();
                        txtCode.setEditable(false);
                        btnAdd.setEnabled(false);
                        btnUpdate.setEnabled(true);
                        btnDelete.setEnabled(true);
                    }
                }
            }
        });
    }

    private void loadAllItems() {
        List<Item> items = itemManager.getAllItems();
        updateTable(items);
    }

    private void updateTable(List<Item> items) {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 添加物品到表格
        for (Item item : items) {
            Object[] rowData = {
                item.getName(),
                item.getCode(),
                item.getFormattedAddedTime(),
                String.format("%.2f", item.getPrice())
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtCode.setText("");
        txtPrice.setText("");
        itemTable.clearSelection();
    }

    /**
     * 統一設置按鈕樣式
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 35));
        
        // 添加懸停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brightenColor(bgColor, 1.1f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    /**
     * 調亮顏色
     */
    private Color brightenColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }

    public static void main(String[] args) {
        try {
            // 設置系統外觀
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        EventQueue.invokeLater(() -> {
            try {
                ItemManagementUI frame = new ItemManagementUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 