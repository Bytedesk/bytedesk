# ACD 路由模組

本模組提供自動呼叫分配（ACD）系統的路由規則管理功能。

## 概述

路由模組實現了 ACD 路由系統，支援：

- 自定義路由規則創建
- 多條件路由
- 基於優先級的路由
- 基於時間的路由
- 基於技能的路由

## 核心組件

- `RoutingRuleEntity`：路由規則配置實體
- `RoutingStrategyEntity`：路由策略管理實體
- `RoutingService`：路由管理服務
- `RoutingController`：REST API 介面

## 功能特性

1. 路由規則管理
   - 規則創建和配置
   - 規則優先級設置
   - 規則狀態管理
   - 規則條件管理
   - 規則動作管理

2. 路由策略
   - 輪詢路由
   - 最少最近路由
   - 隨機路由
   - 基於權重的路由
   - 基於技能的路由

3. 路由條件
   - 基於時間的條件
   - 基於佇列的條件
   - 基於坐席的條件
   - 基於客戶的條件
   - 自定義條件

4. 路由動作
   - 佇列分配
   - 坐席分配
   - 轉接動作
   - 通知動作
   - 自定義動作

## 使用示例

```java
// 示例：創建路由規則
RoutingRuleEntity rule = RoutingRuleEntity.builder()
    .name("工作時間規則")
    .description("在工作時間內路由到支援佇列")
    .priority(1)
    .enabled(true)
    .condition("time >= '09:00' && time <= '18:00'")
    .action("assign_to_queue('support')")
    .build();

// 示例：創建路由策略
RoutingStrategyEntity strategy = RoutingStrategyEntity.builder()
    .name("基於技能的路由")
    .type("SKILL_BASED")
    .parameters(Map.of(
        "skill_weights", Map.of(
            "technical", 3,
            "sales", 2,
            "general", 1
        )
    ))
    .build();
```

## 最佳實踐

1. 設計清晰且可維護的路由規則
2. 實現適當的規則優先級
3. 監控路由效果
4. 定期優化規則
5. 維護路由策略文檔
6. 全面測試路由規則
7. 實現備用路由

## 相關文檔

- [路由規則管理指南](../routing-management.md)
- [路由策略指南](../routing-strategy.md)
- [路由條件參考](../routing-conditions.md)
- [路由 API 參考](../api/routing-api.md) 