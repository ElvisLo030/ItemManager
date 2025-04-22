#!/bin/bash

# 創建目錄
mkdir -p target/classes

# 編譯源代碼
echo "正在編譯代碼..."
javac -d target/classes -cp lib/gson-2.10.1.jar $(find src -name "*.java")

if [ $? -ne 0 ]; then
    echo "編譯失敗，請檢查錯誤訊息"
    exit 1
fi

echo "編譯完成"

# 設置JVM參數以確保UI正常顯示
UI_OPTIONS="-Dsun.java2d.uiScale=1.0 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=on -Dapple.awt.application.name=物品管理系統"

# 添加針對不同操作系統的優化參數
OS=$(uname)
if [ "$OS" = "Darwin" ]; then
    # macOS 特定優化
    UI_OPTIONS="$UI_OPTIONS -Dapple.laf.useScreenMenuBar=true -Dapple.awt.application.appearance=system"
elif [ "$OS" = "Linux" ]; then
    # Linux 特定優化
    UI_OPTIONS="$UI_OPTIONS -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel"
fi

# 添加字體渲染優化參數
UI_OPTIONS="$UI_OPTIONS -Dprism.lcdtext=false -Dprism.text=t2k"

# 運行程序
echo "正在啟動物品管理系統..."
java $UI_OPTIONS -cp target/classes:lib/gson-2.10.1.jar com.inventory.Main

echo "應用程式已關閉" 