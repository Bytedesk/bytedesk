# ACD 队列模块

本模块提供自动呼叫分配（ACD）系统的队列管理功能。

## 概述

队列模块实现了 ACD 队列管理系统，支持：

- 多级队列管理
- 队列成员管理
- 队列优先级设置
- 队列负载均衡
- 队列统计和监控

## 核心组件

- `QueueEntity`：队列配置实体
- `QueueMemberEntity`：队列成员管理实体
- `QueueService`：队列管理服务
- `QueueController`：REST API 接口

## 功能特性

1. 队列管理
   - 队列创建和配置
   - 队列优先级设置
   - 队列状态监控
   - 队列容量管理
   - 队列溢出处理

2. 队列成员管理
   - 成员添加和移除
   - 成员状态跟踪
   - 成员技能管理
   - 成员工作负载均衡
   - 成员绩效监控

3. 队列操作
   - 队列加入和离开操作
   - 队列转接操作
   - 队列溢出处理
   - 队列超时管理
   - 队列公告设置

4. 队列统计
   - 队列长度监控
   - 队列等待时间跟踪
   - 队列放弃率统计
   - 队列服务水平监控
   - 队列性能报告

## 使用示例

```java
// 示例：创建新队列
QueueEntity queue = QueueEntity.builder()
    .name("技术支持队列")
    .description("技术问题支持队列")
    .priority(1)
    .maxLength(100)
    .timeout(300)
    .retry(3)
    .build();

// 示例：添加队列成员
QueueMemberEntity member = QueueMemberEntity.builder()
    .queue(queue)
    .agent(agent)
    .penalty(0)
    .paused(false)
    .build();
```

## 最佳实践

1. 实施适当的队列容量规划
2. 设置合适的队列超时时间
3. 监控队列性能指标
4. 平衡队列成员工作负载
5. 定期分析队列统计数据
6. 实现队列溢出策略
7. 维护队列成员技能

## 相关文档

- [队列管理指南](../queue-management.md)
- [队列成员管理](../queue-member.md)
- [队列统计分析](../queue-statistics.md)
- [队列 API 参考](../api/queue-api.md) 