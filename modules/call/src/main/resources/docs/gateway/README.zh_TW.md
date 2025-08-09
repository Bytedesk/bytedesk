# FreeSwitch 閘道器套件

本套件提供 FreeSwitch 整合的閘道器管理功能。

## 概述

閘道器套件實現了 FreeSwitch 閘道器管理系統，支援：

- 閘道器配置和管理
- SIP 中繼連接處理
- 閘道器狀態監控
- 呼叫路由管理

## 核心元件

- `FreeSwitchGatewayEntity`: 閘道器配置實體
- `FreeSwitchGatewayService`: 閘道器管理服務
- `FreeSwitchGatewayController`: REST API 介面

## 功能特性

1. 閘道器管理
   - 建立、更新和刪除閘道器配置
   - 啟用/停用閘道器
   - 監控閘道器狀態
   - 配置 SIP 中繼參數

2. 狀態監控
   - 即時閘道器狀態追蹤
   - 連接健康監控
   - 自動故障轉移支援
   - 狀態變更通知

3. 配置管理
   - SIP 伺服器設定
   - 認證憑證
   - 註冊參數
   - 自訂閘道器設定

## 使用範例

```java
// 範例：建立新閘道器
FreeSwitchGatewayEntity gateway = FreeSwitchGatewayEntity.builder()
    .gatewayName("供應商1")
    .proxy("sip.provider.com")
    .username("user123")
    .password("pass123")
    .register(true)
    .build();
```

## 最佳實踐

1. 始終使用安全密碼
2. 實現適當的閘道器監控
3. 配置合適的故障轉移策略
4. 定期進行閘道器健康檢查
5. 維護閘道器日誌

## 相關文檔

- [FreeSwitch 文檔](https://freeswitch.org/confluence/display/FREESWITCH/FreeSWITCH+Documentation)
- [SIP 協議參考](https://tools.ietf.org/html/rfc3261)
- [閘道器配置指南](https://freeswitch.org/confluence/display/FREESWITCH/Gateways)
