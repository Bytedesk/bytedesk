# ACD 坐席模組

本模組提供自動呼叫分配（ACD）系統的坐席管理功能。

## 概述

坐席模組實現了 ACD 坐席管理系統，支援：

- 坐席狀態管理
- 坐席技能管理
- 坐席工作負載管理
- 坐席績效跟蹤
- 坐席可用性控制

## 核心組件

- `AgentEntity`：坐席配置實體
- `AgentStatusEntity`：坐席狀態管理實體
- `AgentSkillEntity`：坐席技能管理實體
- `AgentService`：坐席管理服務
- `AgentController`：REST API 介面

## 功能特性

1. 坐席管理
   - 坐席註冊和配置
   - 坐席檔案管理
   - 坐席狀態跟蹤
   - 坐席可用性控制
   - 坐席認證

2. 坐席狀態管理
   - 在線/離線狀態
   - 忙碌/空閒狀態
   - 休息狀態
   - 自定義狀態
   - 狀態歷史跟蹤

3. 坐席技能管理
   - 技能分配
   - 技能等級管理
   - 技能組管理
   - 基於技能的路由
   - 技能績效跟蹤

4. 坐席績效
   - 呼叫處理統計
   - 響應時間跟蹤
   - 客戶滿意度指標
   - 工作負載分配
   - 績效報告

## 使用示例

```java
// 示例：創建新坐席
AgentEntity agent = AgentEntity.builder()
    .name("張三")
    .username("zhangsan")
    .email("zhangsan@example.com")
    .status("OFFLINE")
    .maxConcurrentCalls(3)
    .build();

// 示例：設置坐席狀態
AgentStatusEntity status = AgentStatusEntity.builder()
    .agent(agent)
    .status("ONLINE")
    .reason("準備接聽電話")
    .startTime(LocalDateTime.now())
    .build();

// 示例：分配坐席技能
AgentSkillEntity skill = AgentSkillEntity.builder()
    .agent(agent)
    .skill("技術支援")
    .level(3)
    .build();
```

## 最佳實踐

1. 實現適當的坐席認證
2. 監控坐席狀態變化
3. 平衡坐席工作負載
4. 定期技能評估
5. 跟蹤坐席績效指標
6. 實現適當的休息管理
7. 維護坐席可用性記錄

## 相關文檔

- [坐席管理指南](../agent-management.md)
- [坐席狀態管理](../agent-status.md)
- [坐席技能管理](../agent-skill.md)
- [坐席 API 參考](../api/agent-api.md) 