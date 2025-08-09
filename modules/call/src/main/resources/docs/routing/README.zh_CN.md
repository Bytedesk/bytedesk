# ACD 路由模块

本模块提供自动呼叫分配（ACD）系统的路由规则管理功能。

## 概述

路由模块实现了 ACD 路由系统，支持：

- 自定义路由规则创建
- 多条件路由
- 基于优先级的路由
- 基于时间的路由
- 基于技能的路由

## 核心组件

- `RoutingRuleEntity`：路由规则配置实体
- `RoutingStrategyEntity`：路由策略管理实体
- `RoutingService`：路由管理服务
- `RoutingController`：REST API 接口

## 功能特性

1. 路由规则管理
   - 规则创建和配置
   - 规则优先级设置
   - 规则状态管理
   - 规则条件管理
   - 规则动作管理

2. 路由策略
   - 轮询路由
   - 最少最近路由
   - 随机路由
   - 基于权重的路由
   - 基于技能的路由

3. 路由条件
   - 基于时间的条件
   - 基于队列的条件
   - 基于坐席的条件
   - 基于客户的条件
   - 自定义条件

4. 路由动作
   - 队列分配
   - 坐席分配
   - 转接动作
   - 通知动作
   - 自定义动作

## 使用示例

```java
// 示例：创建路由规则
RoutingRuleEntity rule = RoutingRuleEntity.builder()
    .name("工作时间规则")
    .description("在工作时间内路由到支持队列")
    .priority(1)
    .enabled(true)
    .condition("time >= '09:00' && time <= '18:00'")
    .action("assign_to_queue('support')")
    .build();

// 示例：创建路由策略
RoutingStrategyEntity strategy = RoutingStrategyEntity.builder()
    .name("基于技能的路由")
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

## 最佳实践

1. 设计清晰且可维护的路由规则
2. 实现适当的规则优先级
3. 监控路由效果
4. 定期优化规则
5. 维护路由策略文档
6. 全面测试路由规则
7. 实现备用路由

## 相关文档

- [路由规则管理指南](../routing-management.md)
- [路由策略指南](../routing-strategy.md)
- [路由条件参考](../routing-conditions.md)
- [路由 API 参考](../api/routing-api.md) 