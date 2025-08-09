# ACD 佇列模組

本模組提供自動呼叫分配（ACD）系統的佇列管理功能。

## 概述

佇列模組實現了 ACD 佇列管理系統，支援：

- 多級佇列管理
- 佇列成員管理
- 佇列優先級設置
- 佇列負載均衡
- 佇列統計和監控

## 核心組件

- `QueueEntity`：佇列配置實體
- `QueueMemberEntity`：佇列成員管理實體
- `QueueService`：佇列管理服務
- `QueueController`：REST API 介面

## 功能特性

1. 佇列管理
   - 佇列創建和配置
   - 佇列優先級設置
   - 佇列狀態監控
   - 佇列容量管理
   - 佇列溢出處理

2. 佇列成員管理
   - 成員添加和移除
   - 成員狀態跟蹤
   - 成員技能管理
   - 成員工作負載均衡
   - 成員績效監控

3. 佇列操作
   - 佇列加入和離開操作
   - 佇列轉接操作
   - 佇列溢出處理
   - 佇列超時管理
   - 佇列公告設置

4. 佇列統計
   - 佇列長度監控
   - 佇列等待時間跟蹤
   - 佇列放棄率統計
   - 佇列服務水平監控
   - 佇列性能報告

## 使用示例

```java
// 示例：創建新佇列
QueueEntity queue = QueueEntity.builder()
    .name("技術支援佇列")
    .description("技術問題支援佇列")
    .priority(1)
    .maxLength(100)
    .timeout(300)
    .retry(3)
    .build();

// 示例：添加佇列成員
QueueMemberEntity member = QueueMemberEntity.builder()
    .queue(queue)
    .agent(agent)
    .penalty(0)
    .paused(false)
    .build();
```

## 最佳實踐

1. 實施適當的佇列容量規劃
2. 設置合適的佇列超時時間
3. 監控佇列性能指標
4. 平衡佇列成員工作負載
5. 定期分析佇列統計數據
6. 實現佇列溢出策略
7. 維護佇列成員技能

## 相關文檔

- [佇列管理指南](../queue-management.md)
- [佇列成員管理](../queue-member.md)
- [佇列統計分析](../queue-statistics.md)
- [佇列 API 參考](../api/queue-api.md) 