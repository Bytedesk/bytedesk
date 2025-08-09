# FreeSwitch 用戶套件

本套件提供 FreeSwitch 整合的 SIP 用戶管理功能。

## 概述

用戶套件實現了 FreeSwitch 用戶管理系統，支援：

- SIP 用戶帳戶管理
- 用戶註冊追蹤
- 用戶狀態監控
- 用戶認證

## 核心元件

- `FreeSwitchNumberEntity`: 用戶配置實體
- `FreeSwitchNumberService`: 用戶管理服務
- `FreeSwitchNumberController`: REST API 介面

## 功能特性

1. 用戶帳戶管理
   - 建立和管理 SIP 用戶帳戶
   - 配置用戶憑證
   - 設定顯示名稱和電子郵件
   - 啟用/停用用戶帳戶

2. 註冊管理
   - 追蹤用戶註冊狀態
   - 監控註冊 IP 位址
   - 追蹤用戶代理資訊
   - 處理註冊超時

3. 用戶配置
   - SIP 用戶名和網域
   - 密碼管理
   - 帳戶代碼分配
   - 擴展用戶資訊

## 使用範例

```java
// 範例：建立新的 SIP 用戶
FreeSwitchNumberEntity user = FreeSwitchNumberEntity.builder()
    .username("zhang.san")
    .domain("sip.example.com")
    .password("secure123")
    .displayName("張三")
    .email("zhang.san@example.com")
    .accountcode("ACC001")
    .enabled(true)
    .build();
```

## 最佳實踐

1. 實現安全的密碼策略
2. 定期維護用戶帳戶
3. 監控用戶註冊狀態
4. 追蹤用戶活動
5. 定期備份用戶配置

## 相關文檔

- [FreeSwitch 用戶指南](https://freeswitch.org/confluence/display/FREESWITCH/User+Directory)
- [SIP 認證指南](https://freeswitch.org/confluence/display/FREESWITCH/SIP+Authentication)
- [用戶管理命令](https://freeswitch.org/confluence/display/FREESWITCH/User+Management+Commands)
