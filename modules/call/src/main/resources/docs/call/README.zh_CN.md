# FreeSwitch 呼叫管理包

本包提供 FreeSwitch 集成的呼叫管理功能。

## 概述

呼叫管理包实现了 FreeSwitch 呼叫管理系统，支持：

- 呼叫跟踪和监控
- 呼叫队列管理
- 坐席分配和路由
- 呼叫状态跟踪
- 呼叫时长管理

## 核心组件

- `FreeSwitchCallEntity`: 呼叫管理实体
- `FreeSwitchCallService`: 呼叫管理服务
- `FreeSwitchCallController`: REST API 接口

## 功能特性

1. 呼叫管理
   - 主叫和被叫号码跟踪
   - 呼叫类型管理（呼入、呼出、内部）
   - 呼叫状态监控（排队中、振铃中、通话中、已完成、失败、放弃）
   - 呼叫时长和等待时间跟踪

2. 队列管理
   - 队列分配
   - 坐席分配
   - 基于技能的路由
   - 等待时间监控

3. 呼叫分析
   - 呼叫类型分析
   - 呼叫状态跟踪
   - 时长分析
   - 队列性能监控
   - 坐席性能跟踪

## 使用示例

```java
// 示例：创建新的呼叫记录
FreeSwitchCallEntity call = FreeSwitchCallEntity.builder()
    .callUuid("123e4567-e89b-12d3-a456-426614174000")
    .type(CallType.INBOUND)
    .callerNumber("1234567890")
    .calleeNumber("0987654321")
    .status(CallStatus.QUEUED)
    .startTime(ZonedDateTime.now())
    .queueId(1L)
    .agentId(1L)
    .skills("{\"language\":\"zh\",\"department\":\"support\"}")
    .build();
```

## 最佳实践

1. 实现合适的呼叫路由策略
2. 监控队列性能
3. 跟踪坐席可用性和性能
4. 维护呼叫质量指标
5. 定期进行呼叫数据分析
6. 实施适当的呼叫数据保留策略

## 相关文档

- [FreeSwitch 呼叫管理](https://freeswitch.org/confluence/display/FREESWITCH/Call+Management)
- [队列管理指南](https://freeswitch.org/confluence/display/FREESWITCH/Queue+Management)
- [坐席管理](https://freeswitch.org/confluence/display/FREESWITCH/Agent+Management) 