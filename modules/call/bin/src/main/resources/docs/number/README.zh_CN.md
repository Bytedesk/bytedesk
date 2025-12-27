# FreeSwitch 用户包

本包提供 FreeSwitch 集成的 SIP 用户管理功能。

## 概述

用户包实现了 FreeSwitch 用户管理系统，支持：

- SIP 用户账户管理
- 用户注册跟踪
- 用户状态监控
- 用户认证

## 核心组件

- `FreeSwitchNumberEntity`: 用户配置实体
- `FreeSwitchNumberService`: 用户管理服务
- `FreeSwitchNumberController`: REST API 接口

## 功能特性

1. 用户账户管理
   - 创建和管理 SIP 用户账户
   - 配置用户凭据
   - 设置显示名称和邮箱
   - 启用/禁用用户账户

2. 注册管理
   - 跟踪用户注册状态
   - 监控注册 IP 地址
   - 跟踪用户代理信息
   - 处理注册超时

3. 用户配置
   - SIP 用户名和域名
   - 密码管理
   - 账户代码分配
   - 扩展用户信息

## 使用示例

```java
// 示例：创建新的 SIP 用户
FreeSwitchNumberEntity user = FreeSwitchNumberEntity.builder()
    .username("zhang.san")
    .domain("sip.example.com")
    .password("secure123")
    .displayName("张三")
    .email("zhang.san@example.com")
    .accountcode("ACC001")
    .enabled(true)
    .build();
```

## 最佳实践

1. 实现安全的密码策略
2. 定期维护用户账户
3. 监控用户注册状态
4. 跟踪用户活动
5. 定期备份用户配置

## 相关文档

- [FreeSwitch 用户指南](https://freeswitch.org/confluence/display/FREESWITCH/User+Directory)
- [SIP 认证指南](https://freeswitch.org/confluence/display/FREESWITCH/SIP+Authentication)
- [用户管理命令](https://freeswitch.org/confluence/display/FREESWITCH/User+Management+Commands)
