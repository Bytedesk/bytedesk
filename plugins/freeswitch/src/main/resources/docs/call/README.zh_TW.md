# FreeSwitch 呼叫管理套件

本套件提供 FreeSwitch 整合的呼叫管理功能。

## 概述

呼叫管理套件實現了 FreeSwitch 呼叫管理系統，支援：

- 呼叫追蹤和監控
- 呼叫佇列管理
- 客服人員分配和路由
- 呼叫狀態追蹤
- 呼叫時長管理

## 核心元件

- `FreeSwitchCallEntity`: 呼叫管理實體
- `FreeSwitchCallService`: 呼叫管理服務
- `FreeSwitchCallController`: REST API 介面

## 功能特性

1. 呼叫管理
   - 主叫和被叫號碼追蹤
   - 呼叫類型管理（呼入、呼出、內部）
   - 呼叫狀態監控（排隊中、振鈴中、通話中、已完成、失敗、放棄）
   - 呼叫時長和等待時間追蹤

2. 佇列管理
   - 佇列分配
   - 客服人員分配
   - 基於技能的路由
   - 等待時間監控

3. 呼叫分析
   - 呼叫類型分析
   - 呼叫狀態追蹤
   - 時長分析
   - 佇列效能監控
   - 客服人員效能追蹤

## 使用範例

```java
// 範例：建立新的呼叫記錄
FreeSwitchCallEntity call = FreeSwitchCallEntity.builder()
    .callUuid("123e4567-e89b-12d3-a456-426614174000")
    .type(CallType.INBOUND)
    .callerNumber("1234567890")
    .calleeNumber("0987654321")
    .status(CallStatus.QUEUED)
    .startTime(LocalDateTime.now())
    .queueId(1L)
    .agentId(1L)
    .skills("{\"language\":\"zh_TW\",\"department\":\"support\"}")
    .build();
```

## 最佳實踐

1. 實現合適的呼叫路由策略
2. 監控佇列效能
3. 追蹤客服人員可用性和效能
4. 維護呼叫品質指標
5. 定期進行呼叫資料分析
6. 實施適當的呼叫資料保留策略

## 相關文件

- [FreeSwitch 呼叫管理](https://freeswitch.org/confluence/display/FREESWITCH/Call+Management)
- [佇列管理指南](https://freeswitch.org/confluence/display/FREESWITCH/Queue+Management)
- [客服人員管理](https://freeswitch.org/confluence/display/FREESWITCH/Agent+Management) 