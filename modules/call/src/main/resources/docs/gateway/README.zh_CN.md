# FreeSwitch 网关包

本包提供 FreeSwitch 集成的网关管理功能。

## 概述

网关包实现了 FreeSwitch 网关管理系统，支持：

- 网关配置和管理
- SIP 中继连接处理
- 网关状态监控
- 呼叫路由管理

## 核心组件

- `FreeSwitchGatewayEntity`: 网关配置实体
- `FreeSwitchGatewayService`: 网关管理服务
- `FreeSwitchGatewayController`: REST API 接口

## 功能特性

1. 网关管理
   - 创建、更新和删除网关配置
   - 启用/禁用网关
   - 监控网关状态
   - 配置 SIP 中继参数

2. 状态监控
   - 实时网关状态跟踪
   - 连接健康监控
   - 自动故障转移支持
   - 状态变更通知

3. 配置管理
   - SIP 服务器设置
   - 认证凭据
   - 注册参数
   - 自定义网关设置

## 使用示例

```java
// 示例：创建新网关
FreeSwitchGatewayEntity gateway = FreeSwitchGatewayEntity.builder()
    .gatewayName("供应商1")
    .proxy("sip.provider.com")
    .username("user123")
    .password("pass123")
    .register(true)
    .build();
```

## 最佳实践

1. 始终使用安全密码
2. 实现适当的网关监控
3. 配置合适的故障转移策略
4. 定期进行网关健康检查
5. 维护网关日志

## 相关文档

- [FreeSwitch 文档](https://freeswitch.org/confluence/display/FREESWITCH/FreeSWITCH+Documentation)
- [SIP 协议参考](https://tools.ietf.org/html/rfc3261)
- [网关配置指南](https://freeswitch.org/confluence/display/FREESWITCH/Gateways)
