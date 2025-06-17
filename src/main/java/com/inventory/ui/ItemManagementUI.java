package com.inventory.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;

import com.inventory.controller.ItemManager;
import com.inventory.model.Item;

public class ItemManagementUI extends JFrame {
    private JPanel contentPane;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnSelectImage;
    private JButton btnExport;
    private JLabel lblStatus;
    private JLabel lblImagePreview;
    private JScrollPane imageScrollPane;

    private final ItemManager itemManager;
    private boolean isEditing = false;
    private String currentCode = "";
    private String selectedImagePath = "";
    
    // 原始資料，用於檢測是否有變更
    private String originalName = "";
    private String originalPrice = "";
    private String originalImagePath = "";

    // 表格欄位名稱和排序狀態
    private final String[] columns = {"品名", "編號", "加入時間", "價格", "圖片"};
    private final boolean[] sortAscending = {true, true, true, true, true}; // 記錄每欄的排序方向

    public ItemManagementUI() {
        itemManager = new ItemManager();
        initializeUI();
        loadAllItems();
    }

    private void initializeUI() {
        setTitle("物品管理系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 750);  // 調整窗口大小
        
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
        
        // 創建主輸入面板，分為左右兩部分
        JPanel mainInputPanel = new JPanel(new BorderLayout(20, 0));
        mainInputPanel.setBackground(bgMedium);
        
        // 輸入區域 - 使用網格布局，移除編號輸入
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
        
        txtName = new JTextField(20);
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
        
        // 價格輸入
        JLabel lblPrice = new JLabel("價格:");
        lblPrice.setForeground(textNormal);
        lblPrice.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(lblPrice, gbc);
        
        txtPrice = new JTextField(20);
        txtPrice.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        txtPrice.setBackground(bgLight);
        txtPrice.setForeground(textNormal);
        txtPrice.setCaretColor(Color.WHITE);
        txtPrice.setBorder(BorderFactory.createCompoundBorder(
            roundedBorder,
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        inputPanel.add(txtPrice, gbc);
        
        // 圖片選擇按鈕
        JLabel lblImage = new JLabel("圖片:");
        lblImage.setForeground(textNormal);
        lblImage.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(lblImage, gbc);
        
        btnSelectImage = new JButton("選擇圖片");
        styleButton(btnSelectImage, new Color(156, 39, 176), textHighlight);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(btnSelectImage, gbc);
        
        mainInputPanel.add(inputPanel, BorderLayout.WEST);
        
        // 圖片預覽區域
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(bgMedium);
        imagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(accentMain),
            "圖片預覽",
            0, 0, new Font("微軟正黑體", Font.BOLD, 14), textNormal
        ));
        
        lblImagePreview = new JLabel("尚未選擇圖片", SwingConstants.CENTER);
        lblImagePreview.setForeground(textNormal);
        lblImagePreview.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        lblImagePreview.setPreferredSize(new Dimension(200, 150));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(bgLight));
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(bgLight);
        
        imageScrollPane = new JScrollPane(lblImagePreview);
        imageScrollPane.setPreferredSize(new Dimension(220, 170));
        imageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        imagePanel.add(imageScrollPane, BorderLayout.CENTER);
        
        mainInputPanel.add(imagePanel, BorderLayout.EAST);
        
        topPanel.add(mainInputPanel, BorderLayout.CENTER);
        
        // 搜索和匯出面板 - 右上角
        JPanel searchExportPanel = new JPanel();
        searchExportPanel.setLayout(new BoxLayout(searchExportPanel, BoxLayout.X_AXIS));
        searchExportPanel.setBackground(bgMedium);
        searchExportPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        // 填充空間，使搜索框和匯出按鈕靠右
        searchExportPanel.add(Box.createHorizontalGlue());
        
        // 匯出按鈕
        btnExport = new JButton("匯出Excel");
        styleButton(btnExport, new Color(255, 152, 0), textHighlight);
        btnExport.setPreferredSize(new Dimension(100, 35));
        searchExportPanel.add(btnExport);
        searchExportPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        
        JLabel lblSearch = new JLabel("搜索:");
        lblSearch.setForeground(textNormal);
        lblSearch.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        searchExportPanel.add(lblSearch);
        searchExportPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        
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
        searchExportPanel.add(txtSearch);
        searchExportPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        btnSearch = new JButton("搜索");
        styleButton(btnSearch, new Color(103, 58, 183), textHighlight);
        searchExportPanel.add(btnSearch);
        
        // 操作按鈕面板
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
        
        // 狀態標籤
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(255, 152, 0));
        lblStatus.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(lblStatus);
        
        // 提示標籤
        JLabel lblHint = new JLabel("提示：點擊空白處可清除表單，修改按鈕會在資料變更時啟用");
        lblHint.setForeground(new Color(158, 158, 158));
        lblHint.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(lblHint);
        
        // 將所有面板組合到頂部
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(bgMedium);
        controlPanel.add(searchExportPanel, BorderLayout.NORTH);
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
        columnModel.getColumn(0).setPreferredWidth(220);  // 品名列寬
        columnModel.getColumn(1).setPreferredWidth(120);  // 編號列寬
        columnModel.getColumn(2).setPreferredWidth(180);  // 時間列寬
        columnModel.getColumn(3).setPreferredWidth(100);  // 價格列寬
        columnModel.getColumn(4).setPreferredWidth(120);  // 圖片列寬
        
        // 設置表格標題可點擊排序
        setupTableHeaderSorting();
        
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
        
        // 添加點擊空白處清除表單的功能
        addClearFormListeners();
        
        // 添加輸入變更監聽器
        addInputChangeListeners();
        
        // 註冊事件監聽器
        registerEventHandlers();
    }

    // 添加輸入變更監聽器
    private void addInputChangeListeners() {
        DocumentListener changeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkForChanges();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkForChanges();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkForChanges();
            }
        };
        
        txtName.getDocument().addDocumentListener(changeListener);
        txtPrice.getDocument().addDocumentListener(changeListener);
    }

    // 檢查是否有變更
    private void checkForChanges() {
        if (!isEditing) {
            return;
        }
        
        String currentName = txtName.getText().trim();
        String currentPrice = txtPrice.getText().trim();
        String currentImagePath = selectedImagePath;
        
        boolean hasChanges = !currentName.equals(originalName) ||
                           !currentPrice.equals(originalPrice) ||
                           !currentImagePath.equals(originalImagePath);
        
        btnUpdate.setEnabled(hasChanges && !currentName.isEmpty() && !currentPrice.isEmpty());
        
        if (hasChanges) {
            lblStatus.setText("已偵測到資料變更");
        } else {
            lblStatus.setText("已選擇物品：" + originalName + " (編號：" + currentCode + ")");
        }
    }

    // 設置表格標題排序功能
    private void setupTableHeaderSorting() {
        JTableHeader header = itemTable.getTableHeader();
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = header.columnAtPoint(e.getPoint());
                if (column >= 0 && column < columns.length) {
                    sortTableByColumn(column);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                int column = header.columnAtPoint(e.getPoint());
                if (column >= 0) {
                    header.setToolTipText("點擊排序：" + columns[column]);
                }
            }
        });
    }

    // 依據欄位排序表格
    private void sortTableByColumn(int column) {
        List<Item> items = itemManager.getAllItems();
        
        // 切換排序方向
        sortAscending[column] = !sortAscending[column];
        boolean ascending = sortAscending[column];
        
        // 根據欄位進行排序
        switch (column) {
            case 0: // 品名
                items.sort(ascending ? 
                    Comparator.comparing(Item::getName) : 
                    Comparator.comparing(Item::getName).reversed());
                break;
            case 1: // 編號
                items.sort(ascending ? 
                    Comparator.comparing(Item::getCode) : 
                    Comparator.comparing(Item::getCode).reversed());
                break;
            case 2: // 加入時間
                items.sort(ascending ? 
                    Comparator.comparing(Item::getAddedTime) : 
                    Comparator.comparing(Item::getAddedTime).reversed());
                break;
            case 3: // 價格
                items.sort(ascending ? 
                    Comparator.comparing(Item::getPrice) : 
                    Comparator.comparing(Item::getPrice).reversed());
                break;
            case 4: // 圖片
                items.sort(ascending ? 
                    Comparator.comparing(item -> item.getImagePath().isEmpty() ? "無圖片" : "有圖片") : 
                    Comparator.comparing((Item item) -> item.getImagePath().isEmpty() ? "無圖片" : "有圖片").reversed());
                break;
        }
        
        updateTable(items);
        
        // 更新狀態提示
        String direction = ascending ? "正向" : "反向";
        lblStatus.setText(columns[column] + " " + direction + "排序");
    }

    // 添加點擊空白處清除表單的監聽器
    private void addClearFormListeners() {
        MouseAdapter clearFormListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 確保點擊的不是輸入元件或按鈕
                Component clickedComponent = e.getComponent();
                if (!(clickedComponent instanceof JTextField) && 
                    !(clickedComponent instanceof JButton) &&
                    !(clickedComponent instanceof JTable)) {
                    clearFormAndResetState();
                }
            }
        };
        
        // 為主面板和各個子面板添加監聽器
        contentPane.addMouseListener(clearFormListener);
        addMouseListenerRecursively(contentPane, clearFormListener);
    }

    // 遞歸添加鼠標監聽器
    private void addMouseListenerRecursively(Container container, MouseAdapter listener) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container && 
                !(component instanceof JTextField) &&
                !(component instanceof JButton) &&
                !(component instanceof JTable)) {
                component.addMouseListener(listener);
                addMouseListenerRecursively((Container) component, listener);
            }
        }
    }

    private void registerEventHandlers() {
        // 匯出按鈕事件
        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("匯出Excel檔案");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel檔案 (*.xlsx)", "xlsx"));
            
            // 設定預設檔名
            String defaultFileName = "物品清單_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                
                // 確保檔名以.xlsx結尾
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }
                
                boolean success = itemManager.exportToExcel(filePath);
                if (success) {
                    lblStatus.setText("Excel檔案匯出成功: " + selectedFile.getName());
                    JOptionPane.showMessageDialog(this, 
                        "Excel檔案已成功匯出至:\n" + filePath, 
                        "匯出成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    lblStatus.setText("Excel檔案匯出失敗");
                    JOptionPane.showMessageDialog(this, 
                        "匯出Excel檔案時發生錯誤，請檢查檔案路徑是否正確", 
                        "匯出失敗", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 圖片選擇按鈕事件
        btnSelectImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("選擇圖片");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "圖片檔案", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath = selectedFile.getAbsolutePath();
                displayImagePreview(selectedImagePath);
                lblStatus.setText("已選擇圖片: " + selectedFile.getName());
                
                // 觸發變更檢查
                checkForChanges();
            }
        });
        
        // 新增按鈕事件 - 使用自動產生編號
        btnAdd.addActionListener(e -> {
            if (isEditing) {
                lblStatus.setText("請先取消編輯狀態");
                return;
            }
            
            try {
                String name = txtName.getText().trim();
                String priceText = txtPrice.getText().trim();
                
                // 表單驗證
                if (name.isEmpty() || priceText.isEmpty()) {
                    lblStatus.setText("請填寫所有欄位");
                    return;
                }
                
                double price = Double.parseDouble(priceText);
                
                // 處理圖片複製
                String imagePath = "";
                if (!selectedImagePath.isEmpty()) {
                    try {
                        imagePath = itemManager.copyImageToDataDir(selectedImagePath);
                    } catch (IOException ex) {
                        lblStatus.setText("圖片複製失敗: " + ex.getMessage());
                        return;
                    }
                }
                
                // 使用自動產生編號的方法新增物品
                boolean success = itemManager.addItem(name, price, imagePath);
                
                if (success) {
                    lblStatus.setText("物品新增成功，編號自動產生");
                    clearFormAndResetState();
                    loadAllItems();
                } else {
                    lblStatus.setText("新增失敗");
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
                
                // 處理圖片複製
                String imagePath = "";
                if (!selectedImagePath.isEmpty()) {
                    try {
                        imagePath = itemManager.copyImageToDataDir(selectedImagePath);
                    } catch (IOException ex) {
                        lblStatus.setText("圖片複製失敗: " + ex.getMessage());
                        return;
                    }
                } else {
                    // 如果沒有選擇新圖片，保留原有圖片
                    Optional<Item> currentItem = itemManager.getItemByCode(currentCode);
                    if (currentItem.isPresent()) {
                        imagePath = currentItem.get().getImagePath();
                    }
                }
                
                // 創建更新的物品
                Item updatedItem = new Item(name, currentCode, price, imagePath);
                boolean success = itemManager.updateItem(currentCode, updatedItem);
                
                if (success) {
                    lblStatus.setText("物品更新成功");
                    clearFormAndResetState();
                    loadAllItems();
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
                    clearFormAndResetState();
                    loadAllItems();
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
                        
                        // 儲存原始資料
                        originalName = item.getName();
                        originalPrice = String.valueOf(item.getPrice());
                        originalImagePath = "";
                        
                        // 填充表單
                        txtName.setText(item.getName());
                        txtPrice.setText(String.valueOf(item.getPrice()));
                        
                        // 顯示圖片預覽
                        if (item.getImagePath() != null && !item.getImagePath().trim().isEmpty()) {
                            String imagePath = "data" + File.separator + item.getImagePath();
                            displayImagePreview(imagePath);
                            selectedImagePath = ""; // 清空選擇的新圖片路徑
                            originalImagePath = ""; // 保留原有圖片路徑
                        } else {
                            clearImagePreview();
                        }
                        
                        // 設置編輯狀態
                        isEditing = true;
                        currentCode = item.getCode();
                        btnAdd.setEnabled(false);
                        btnUpdate.setEnabled(false); // 初始時不啟用，等有變更才啟用
                        btnDelete.setEnabled(true);
                        
                        lblStatus.setText("已選擇物品：" + item.getName() + " (編號：" + item.getCode() + ")");
                    }
                }
            }
        });
    }

    private void displayImagePreview(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imageFile);
                
                // 縮放圖片以適應預覽區域
                int maxWidth = 190;
                int maxHeight = 140;
                Image scaledImage = scaleImage(originalImage, maxWidth, maxHeight);
                
                ImageIcon imageIcon = new ImageIcon(scaledImage);
                lblImagePreview.setIcon(imageIcon);
                lblImagePreview.setText("");
            } else {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("圖片檔案不存在");
            }
        } catch (IOException e) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("無法載入圖片");
            System.err.println("載入圖片時發生錯誤: " + e.getMessage());
        }
    }

    private void clearImagePreview() {
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("尚未選擇圖片");
        selectedImagePath = "";
    }

    private Image scaleImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // 計算縮放比例
        double scaleWidth = (double) maxWidth / originalWidth;
        double scaleHeight = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleWidth, scaleHeight);
        
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);
        
        return originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
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
            String imageStatus = (item.getImagePath() != null && !item.getImagePath().trim().isEmpty()) 
                ? "有圖片" : "無圖片";
            
            Object[] rowData = {
                item.getName(),
                item.getCode(),
                item.getFormattedAddedTime(),
                String.format("%.2f", item.getPrice()),
                imageStatus
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearFormAndResetState() {
        txtName.setText("");
        txtPrice.setText("");
        clearImagePreview();
        itemTable.clearSelection();
        
        // 重置編輯狀態
        isEditing = false;
        currentCode = "";
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
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