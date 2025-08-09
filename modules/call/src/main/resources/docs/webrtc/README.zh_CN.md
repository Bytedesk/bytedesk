# FreeSwitch WebRTC 包

本包提供 FreeSwitch 集成的 WebRTC 视频客服功能。

## 概述

WebRTC 包实现了 FreeSwitch WebRTC 系统，支持：

- WebRTC 视频客服
- 基于浏览器的视频通话
- 实时通信
- 客服系统集成
- 视频质量控制
- 通话录音
- 坐席状态管理

## 核心组件

- `FreeswitchWebRTCEntity`: WebRTC 配置实体
- `FreeswitchWebRTCService`: WebRTC 管理服务
- `FreeswitchWebRTCController`: REST API 接口

## 功能特性

1. 视频客服管理
   - 坐席状态跟踪（在线/离线/忙碌）
   - 视频通话质量设置
   - 并发通话管理
   - 通话录音配置
   - 坐席分组和技能管理
   - 工作时间管理

2. 视频通话功能
   - 基于浏览器的视频通话
   - 视频质量控制（高清/标清/流畅）
   - 最大并发通话限制
   - 实时状态监控
   - 通话录音支持

3. 坐席管理
   - 坐席状态控制
   - 基于技能的路由
   - 工作时间配置
   - 分组管理
   - 性能跟踪

## 实体结构

```java
public class FreeswitchWebRTCEntity extends BaseEntity {
    private String name;                    // 坐席名称（唯一）
    private String description;             // 坐席描述
    private String type;                    // WebRTC类型（客服/坐席/队列）
    private String status;                  // 坐席状态（在线/离线/忙碌）
    private Boolean enabled;                // 是否启用
    private Integer maxVideoCalls;          // 最大并发视频数
    private String videoQuality;            // 视频质量（高清/标清/流畅）
    private Boolean recordEnabled;          // 是否允许录音
    private String recordPath;              // 录音文件路径
    private String groupName;               // 坐席分组
    private String skillTags;               // 坐席技能（逗号分隔）
    private String workingHours;            // 工作时间（JSON格式）
    private String configJson;              // 扩展配置
    private String remarks;                 // 备注
}
```

## 使用示例

```java
// 示例1：创建新的视频客服坐席
FreeswitchWebRTCEntity agent = FreeswitchWebRTCEntity.builder()
    .name("视频客服坐席")
    .description("专业视频客服服务")
    .type(FreeswitchWebRTCTypeEnum.AGENT.name())
    .status("ONLINE")
    .maxVideoCalls(3)
    .videoQuality("HIGH")
    .recordEnabled(true)
    .recordPath("/recordings/video-support")
    .groupName("技术支持")
    .skillTags("技术,产品,英语")
    .workingHours("{\"monday\":{\"start\":\"09:00\",\"end\":\"18:00\"}}")
    .enabled(true)
    .build();

// 示例2：检查坐席状态
if (agent.isOnline()) {
    // 处理在线坐席
} else if (agent.isBusy()) {
    // 处理忙碌坐席
}

// 示例3：获取坐席技能
List<String> skills = agent.getSkillTagList();
```

## 最佳实践

1. 视频通话管理
   - 根据坐席能力设置合适的最大并发视频数
   - 根据网络条件配置视频质量
   - 实现适当的通话录音策略
   - 监控视频通话质量

2. 坐席管理
   - 定期状态监控
   - 合理的技能标签管理
   - 工作时间配置
   - 分组组织
   - 性能跟踪

3. 系统配置
   - 安全的 WebRTC 连接
   - 适当的录音存储管理
   - 定期配置备份
   - 监控系统资源

## 相关文档

- [FreeSwitch WebRTC 指南](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC)
- [WebRTC 配置指南](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC+Configuration)
- [视频客服指南](https://freeswitch.org/confluence/display/FREESWITCH/Video+Customer+Service)
- [WebRTC 安全最佳实践](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC+Security)
