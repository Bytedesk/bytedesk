# FreeSwitch 通話詳單套件

本套件提供 FreeSwitch 整合的通話詳單（CDR）管理功能。

## 概述

通話詳單套件實現了 FreeSwitch 通話記錄系統，支援：

- 通話詳單記錄和追蹤
- 通話時長和計費管理
- 通話品質監控
- 通話歷史分析

## 核心元件

- `FreeSwitchCdrEntity`: 通話詳單配置實體
- `FreeSwitchCdrService`: 通話詳單管理服務
- `FreeSwitchCdrController`: REST API 介面

## 功能特性

1. 通話詳單記錄
   - 主叫和被叫資訊追蹤
   - 通話時長和計費時間記錄
   - 通話開始、接通和結束時間追蹤
   - 通話狀態和掛斷原因監控

2. 通話品質監控
   - 編解碼器資訊追蹤
   - 通話方向監控
   - 通話狀態追蹤
   - 錄音檔案管理

3. 通話分析
   - 通話時長格式化
   - 通話成功率分析
   - 通話狀態描述
   - JSON 格式的擴展資訊

## 使用範例

```java
// 範例：建立新的通話詳單記錄
FreeSwitchCdrEntity cdr = FreeSwitchCdrEntity.builder()
    .callerIdName("張三")
    .callerIdNumber("1234567890")
    .destinationNumber("0987654321")
    .startStamp(LocalDateTime.now())
    .duration(300)
    .billsec(280)
    .hangupCause("NORMAL_CLEARING")
    .direction("outbound")
    .build();
```

## 最佳實踐

1. 定期備份通話詳單資料
2. 實現適當的通話詳單資料保留策略
3. 監控通話品質指標
4. 分析通話模式和趨勢
5. 維護通話錄音儲存

## 相關文檔

- [FreeSwitch 通話詳單文檔](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR)
- [通話詳單格式參考](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR+Format)
- [通話錄音指南](https://freeswitch.org/confluence/display/FREESWITCH/Recording)
