---
sidebar_label: iOS
sidebar_position: 4
---

# iOS Swift SDK

## 部分功能

- 微语官方技术支持
- 全部基于Swift开发，100%全部开源，支持自定义界面
- 支持人工客服
- 支持机器人
- 支持文字、图片、语音、表情
- 支持消息预知：对方正在输入
- 支持消息状态：送达、已读
- 支持消息撤回
- 支持发送商品信息
- 未读消息数查询接口
- 对接第三方账号系统
- 支持多用户切换

## SDK源码及Demo下载

- [Gitee](https://gitee.com/270580156/bytedesk-swift)
- [Github](https://github.com/Bytedesk/bytedesk-swift)

## 集成方式

建议: Xcode Version 14.3，最低兼容: iOS 13

### 方法 1. 本地集成

此方法适用于有自定义界面需求的开发者

- 下载源码，直接拖到自己项目中
- 选择项目，选中项目TARGET，选中 General，在 framework 中添加 bytedesk_swift.framework

### 方法 2. Swift Package Manager (SPM)
  
```bash
dependencies: [
    .package(url: "https://gitee.com/270580156/bytedesk-swift", .upToNextMajor(from: "3.0.0"))
]
或
dependencies: [
    .package(url: "https://github.com/Bytedesk/bytedesk-swift", .upToNextMajor(from: "3.0.0"))
]
```

### 3. Carthage

```bash
github 'bytedesk-swift/bytedesk-swift' ~> 3.0.0
```
