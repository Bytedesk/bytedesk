<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:44:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-19 10:17:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 微语

企业级多租户即时通讯解决方案

:::tip
微语仍处于早期的快速迭代阶段，文档可能落后于开发，导致功能描述可能不符，以最新发布的软件版本为准
:::

## 语言

- [English](./README.md)
- [中文](./README.zh.md)

## 介绍

### 企业IM

- 局域网即时通讯
- 企业成员管理
- 聊天记录监控
- ...

### 全渠道客服

- 多渠道接入
- 人工客服
- 统计报表
- ...

### 知识库AI对话

- 对接大模型LLM
- 自定义知识库
- 多轮对话
- ...

### 工单系统

- 工单管理
- 工单SLA管理
- 工单统计和报表
- ...

## Docker 快速开始

### 克隆项目并启动docker compose容器

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### 停止容器

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## 演示

```bash
url: http://127.0.0.1:9003/dev
username: admin@email.com, password: admin
```

## 对话SDK

| Project     | Description           | Forks          | Stars             |
|-------------|-----------------------|----------------|-------------------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | iOS  | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift)                 |
| [Android](https://github.com/bytedesk/bytedesk-android) | Android | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android)  |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter)| ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Uniapp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Web | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |

## 链接

- [下载](https://www.weiyuai.cn/download.html)
- [文档](https://www.weiyuai.cn/docs/zh-CN/)

## 技术栈
<!-- - [sofaboot](https://github.com/sofastack/sofa-boot/blob/master/README_ZH.md) for im server 基于金融级云原生架构-->
- [springboot-3.x for 后端](https://github.com/Bytedesk/bytedesk)
- [react for web前端](https://github.com/Bytedesk/bytedesk-web)
- [flutter for 移动客户端(ios&android)](https://github.com/Bytedesk/bytedesk-mobile)
- [electron for 桌面客户端(windows&mac&linux)](https://github.com/Bytedesk/bytedesk-desktop)
<!-- - [python for ai](https://github.com/Bytedesk/bytedesk-ai) -->

## 联系

<!-- - [Email](mailto:270580156@qq.com) -->
<!-- - [微信](./images/wechat.png) -->
- 微语技术支持群：
- <img src="./images/wechat_group.jpg" width="200">
- 服务号
- <img src="./images/wechat_mp.jpg" width="100">
- 订阅号
- <img src="./images/wechatai_mp.jpg" width="100">

- 此为开源社区版，支持免费商用

## 版权

- [MIT](./LICENSE)
