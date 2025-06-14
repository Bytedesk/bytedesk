# FreeSwitch 通话详单包

本包提供 FreeSwitch 集成的通话详单（CDR）管理功能。

## 概述
通话详单包实现了 FreeSwitch 通话记录系统，支持：
- 通话详单记录和跟踪
- 通话时长和计费管理
- 通话质量监控
- 通话历史分析

## 核心组件
- `FreeSwitchCdrEntity`: 通话详单配置实体
- `FreeSwitchCdrService`: 通话详单管理服务
- `FreeSwitchCdrController`: REST API 接口

## 功能特性
1. 通话详单记录
   - 主叫和被叫信息跟踪
   - 通话时长和计费时间记录
   - 通话开始、接通和结束时间跟踪
   - 通话状态和挂断原因监控

2. 通话质量监控
   - 编解码器信息跟踪
   - 通话方向监控
   - 通话状态跟踪
   - 录音文件管理

3. 通话分析
   - 通话时长格式化
   - 通话成功率分析
   - 通话状态描述
   - JSON 格式的扩展信息

## 使用示例
```java
// 示例：创建新的通话详单记录
FreeSwitchCdrEntity cdr = FreeSwitchCdrEntity.builder()
    .callerIdName("张三")
    .callerIdNumber("1234567890")
    .destinationNumber("0987654321")
    .startStamp(LocalDateTime.now())
    .duration(300)
    .billsec(280)
    .hangupCause("NORMAL_CLEARING")
    .direction("outbound")
    .build();
```

## 最佳实践
1. 定期备份通话详单数据
2. 实现适当的通话详单数据保留策略
3. 监控通话质量指标
4. 分析通话模式和趋势
5. 维护通话录音存储

## 相关文档
- [FreeSwitch 通话详单文档](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR)
- [通话详单格式参考](https://freeswitch.org/confluence/display/FREESWITCH/XML+CDR+Format)
- [通话录音指南](https://freeswitch.org/confluence/display/FREESWITCH/Recording) 