# ACD 坐席模块

本模块提供自动呼叫分配（ACD）系统的坐席管理功能。

## 概述

坐席模块实现了 ACD 坐席管理系统，支持：

- 坐席状态管理
- 坐席技能管理
- 坐席工作负载管理
- 坐席绩效跟踪
- 坐席可用性控制

## 核心组件

- `AgentEntity`：坐席配置实体
- `AgentStatusEntity`：坐席状态管理实体
- `AgentSkillEntity`：坐席技能管理实体
- `AgentService`：坐席管理服务
- `AgentController`：REST API 接口

## 功能特性

1. 坐席管理
   - 坐席注册和配置
   - 坐席档案管理
   - 坐席状态跟踪
   - 坐席可用性控制
   - 坐席认证

2. 坐席状态管理
   - 在线/离线状态
   - 忙碌/空闲状态
   - 休息状态
   - 自定义状态
   - 状态历史跟踪

3. 坐席技能管理
   - 技能分配
   - 技能等级管理
   - 技能组管理
   - 基于技能的路由
   - 技能绩效跟踪

4. 坐席绩效
   - 呼叫处理统计
   - 响应时间跟踪
   - 客户满意度指标
   - 工作负载分配
   - 绩效报告

## 使用示例

```java
// 示例：创建新坐席
AgentEntity agent = AgentEntity.builder()
    .name("张三")
    .username("zhangsan")
    .email("zhangsan@example.com")
    .status("OFFLINE")
    .maxConcurrentCalls(3)
    .build();

// 示例：设置坐席状态
AgentStatusEntity status = AgentStatusEntity.builder()
    .agent(agent)
    .status("ONLINE")
    .reason("准备接听电话")
    .startTime(LocalDateTime.now())
    .build();

// 示例：分配坐席技能
AgentSkillEntity skill = AgentSkillEntity.builder()
    .agent(agent)
    .skill("技术支持")
    .level(3)
    .build();
```

## 最佳实践

1. 实现适当的坐席认证
2. 监控坐席状态变化
3. 平衡坐席工作负载
4. 定期技能评估
5. 跟踪坐席绩效指标
6. 实现适当的休息管理
7. 维护坐席可用性记录

## 相关文档

- [坐席管理指南](../agent-management.md)
- [坐席状态管理](../agent-status.md)
- [坐席技能管理](../agent-skill.md)
- [坐席 API 参考](../api/agent-api.md) 