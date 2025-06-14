# FreeSwitch 会议室包

本包提供 FreeSwitch 集成的会议室管理功能。

## 概述
会议室包实现了 FreeSwitch 会议系统，支持：
- 会议室创建和管理
- 参会者管理
- 会议录音
- 会议室访问控制

## 核心组件
- `FreeSwitchConferenceEntity`: 会议室配置实体
- `FreeSwitchConferenceService`: 会议室管理服务
- `FreeSwitchConferenceController`: REST API 接口

## 功能特性
1. 会议室管理
   - 创建和配置会议室
   - 设置会议室密码和访问控制
   - 管理最大参会人数限制
   - 启用/禁用会议室

2. 录音管理
   - 启用/禁用会议录音
   - 配置录音路径
   - 管理录音文件
   - 跟踪录音状态

3. 会议室配置
   - 会议室名称和描述
   - 密码保护
   - 成员限制
   - JSON 格式的扩展配置

## 使用示例
```java
// 示例：创建新的会议室
FreeSwitchConferenceEntity conference = FreeSwitchConferenceEntity.builder()
    .conferenceName("销售会议")
    .description("每周销售团队会议")
    .password("123456")
    .maxMembers(10)
    .recordEnabled(true)
    .recordPath("/recordings/sales")
    .enabled(true)
    .build();
```

## 最佳实践
1. 实现适当的密码策略
2. 定期维护会议室
3. 监控会议室容量
4. 管理录音存储
5. 定期备份会议室配置

## 相关文档
- [FreeSwitch 会议室文档](https://freeswitch.org/confluence/display/FREESWITCH/Conference)
- [会议室命令参考](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Commands)
- [会议录音指南](https://freeswitch.org/confluence/display/FREESWITCH/Conference+Recording) 