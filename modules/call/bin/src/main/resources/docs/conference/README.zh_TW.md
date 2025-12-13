# FreeSwitch 會議室套件

本套件提供 FreeSwitch 整合的會議室管理功能。

## 概述

會議室套件實現了 FreeSwitch 會議系統，支援：

- 會議室建立和管理
- 與會者管理
- 會議錄音
- 會議室存取控制

## 核心元件

- `FreeSwitchConferenceEntity`: 會議室配置實體
- `FreeSwitchConferenceService`: 會議室管理服務
- `FreeSwitchConferenceController`: REST API 介面

## 功能特性

1. 會議室管理
   - 建立和配置會議室
   - 設定會議室密碼和存取控制
   - 管理最大與會人數限制
   - 啟用/停用會議室

2. 錄音管理
   - 啟用/停用會議錄音
   - 配置錄音路徑
   - 管理錄音檔案
   - 追蹤錄音狀態

3. 會議室配置
   - 會議室名稱和描述
   - 密碼保護
   - 成員限制
   - JSON 格式的擴展配置

## 使用範例

```java
// 範例：建立新的會議室
FreeSwitchConferenceEntity conference = FreeSwitchConferenceEntity.builder()
    .conferenceName("銷售會議")
    .description("每週銷售團隊會議")
    .password("123456")
    .maxMembers(10)
    .recordEnabled(true)
    .recordPath("/recordings/sales")
    .enabled(true)
    .build();
```

## 最佳實踐

1. 實現適當的密碼策略
2. 定期維護會議室
3. 監控會議室容量
4. 管理錄音儲存
5. 定期備份會議室配置

## 相關文檔

- [FreeSwitch 會議室文檔](https://freeswitch.org/confluence/display/FREESWITCH/Conference)
- [會議室命令參考](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Commands)
- [會議錄音指南](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Recording)
