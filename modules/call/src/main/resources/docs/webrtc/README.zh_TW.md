# FreeSwitch WebRTC 套件

本套件提供 FreeSwitch 整合的 WebRTC 視訊客服功能。

## 概述

WebRTC 套件實現了 FreeSwitch WebRTC 系統，支援：

- WebRTC 視訊客服
- 基於瀏覽器的視訊通話
- 即時通訊
- 客服系統整合
- 視訊品質控制
- 通話錄音
- 客服狀態管理

## 核心元件

- `FreeswitchWebRTCEntity`: WebRTC 配置實體
- `FreeswitchWebRTCService`: WebRTC 管理服務
- `FreeswitchWebRTCController`: REST API 介面

## 功能特性

1. 視訊客服管理
   - 客服狀態追蹤（在線/離線/忙碌）
   - 視訊通話品質設定
   - 並發通話管理
   - 通話錄音配置
   - 客服分組和技能管理
   - 工作時間管理

2. 視訊通話功能
   - 基於瀏覽器的視訊通話
   - 視訊品質控制（高清/標清/流暢）
   - 最大並發通話限制
   - 即時狀態監控
   - 通話錄音支援

3. 客服管理
   - 客服狀態控制
   - 基於技能的路由
   - 工作時間配置
   - 分組管理
   - 效能追蹤

## 實體結構

```java
public class FreeswitchWebRTCEntity extends BaseEntity {
    private String name;                    // 客服名稱（唯一）
    private String description;             // 客服描述
    private String type;                    // WebRTC類型（客服/坐席/佇列）
    private String status;                  // 客服狀態（在線/離線/忙碌）
    private Boolean enabled;                // 是否啟用
    private Integer maxVideoCalls;          // 最大並發視訊數
    private String videoQuality;            // 視訊品質（高清/標清/流暢）
    private Boolean recordEnabled;          // 是否允許錄音
    private String recordPath;              // 錄音檔案路徑
    private String groupName;               // 客服分組
    private String skillTags;               // 客服技能（逗號分隔）
    private String workingHours;            // 工作時間（JSON格式）
    private String configJson;              // 擴展配置
    private String remarks;                 // 備註
}
```

## 使用範例

```java
// 範例1：建立新的視訊客服坐席
FreeswitchWebRTCEntity agent = FreeswitchWebRTCEntity.builder()
    .name("視訊客服坐席")
    .description("專業視訊客服服務")
    .type(FreeswitchWebRTCTypeEnum.AGENT.name())
    .status("ONLINE")
    .maxVideoCalls(3)
    .videoQuality("HIGH")
    .recordEnabled(true)
    .recordPath("/recordings/video-support")
    .groupName("技術支援")
    .skillTags("技術,產品,英語")
    .workingHours("{\"monday\":{\"start\":\"09:00\",\"end\":\"18:00\"}}")
    .enabled(true)
    .build();

// 範例2：檢查客服狀態
if (agent.isOnline()) {
    // 處理在線客服
} else if (agent.isBusy()) {
    // 處理忙碌客服
}

// 範例3：獲取客服技能
List<String> skills = agent.getSkillTagList();
```

## 最佳實踐

1. 視訊通話管理
   - 根據客服能力設定合適的最大並發視訊數
   - 根據網路條件配置視訊品質
   - 實現適當的通話錄音策略
   - 監控視訊通話品質

2. 客服管理
   - 定期狀態監控
   - 合理的技能標籤管理
   - 工作時間配置
   - 分組組織
   - 效能追蹤

3. 系統配置
   - 安全的 WebRTC 連接
   - 適當的錄音儲存管理
   - 定期配置備份
   - 監控系統資源

## 相關文件

- [FreeSwitch WebRTC 指南](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC)
- [WebRTC 配置指南](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC+Configuration)
- [視訊客服指南](https://freeswitch.org/confluence/display/FREESWITCH/Video+Customer+Service)
- [WebRTC 安全最佳實踐](https://freeswitch.org/confluence/display/FREESWITCH/WebRTC+Security)
