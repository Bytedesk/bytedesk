<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:44:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-23 23:21:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 微语

开源企业协作管理系统，支持企业IM、在线客服、知识库、工单系统、AI助手等

## 语言

- [English](./README.md)
- [中文](./README.zh.md)

## 介绍

### 智能客服

- 基于大模型 LLM && RAG 的智能客服
- 支持Ollama
- 智谱AI

### AI助手

- 更适合团队使用，一人配置，多人使用。完美权限控制，即可私用，也可公用

### 在线客服：跟客户聊

- 支持多渠道：
  - 平台渠道：Web/H5/React/Android/iOS/Uniapp/Flutter、
  - 社交渠道：微信公众号/小程序/企业微信/小红书/抖音/快手/百度/微博/知乎、
  - 电商渠道：淘宝/天猫/京东/千牛/抖店
  - 海外渠道：Facebook/Instagram//Whatsapp/Line
- 多种路由策略、
- 详细考核指标
- 坐席工作台、
- 工单系统、
- 坐席管理、
- 数据看板、
- 人工知识库、
- 技能组管理、
- 实时监控、
- 公告、
- 敏感词、
- CRM、
- 报表功能，
- 为客户提供一体化客服工作台服务

### 社交IM：跟好友聊

- 好友
- 群组

### 企业IM：跟同事聊

- 多层组织架构、
- 角色管理
- 权限管理
- 聊天记录管理
- 同事对话、同事群组

## 快速开始

```bash
# 注意: 此开源版本处于早期阶段，许多功能尚未完善或测试未完成，文档尚待完善，请勿在生产环境使用
git clone https://github.com/Bytedesk/bytedesk.git
# 配置文件: bytedesk/starter/src/main/resources/application-dev.properties
# 推荐开发环境：vscode + maven
#
# java --version
# java 17.0.4 2022-07-19 LTS
# 
# mvn --version
# Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
# OS name: "mac os x", version: "14.2.1", arch: "aarch64", family: "mac"
# 
# 项目使用了protobuf，可能需要安装 protobuf 编译工具
# protoc --version
# libprotoc 25.3
# 
cd bytedesk
mvn install -Dmaven.test.skip=true
# 
cd starter
mvn spring-boot:run
# 
# 本地预览
开发者入口: http://127.0.0.1:9003/dev
web: http://127.0.0.1:9003/
管理后台: http://127.0.0.1:9003/admin, 用户名: admin@email.com, 密码: admin
客服端: http://127.0.0.1:9003/agent, 用户名: admin@email.com, 密码: admin
访客: http://127.0.0.1:9003/chat?org=df_org_uid&t=0&sid=df_ag_uid&
api文档: http://127.0.0.1:9003/swagger-ui/index.html
actuator: http://127.0.0.1:9003/actuator
h2数据库: http://127.0.0.1:9003/h2-console, 路径: ./h2db/weiyuim, 用户名: sa, 密码: sa
```

## 文档

- [文档](https://www.weiyuai.cn/docs/zh-CN/)

## 预览

### 管理后台

| 组织 | 客服 | ai |
| :----------: | :----------: | :----------: |
| <img src="./images/admin/team.png" width="250"> | <img src="./images/admin/service.png" width="250"> | <img src="./images/admin/ai.png" width="250"> |

## [桌面客户端](https://github.com/Bytedesk/bytedesk-desktop)

| 登录 | 对话 | 通讯录 | 设置 |
| :----------: | :----------: | :----------: | :----------: |
| <img src="./images/pc/login2.png" width="100"><img src="./images/pc/switch.png" width="100"> | <img src="./images/pc/chat.png" width="250"> | <img src="./images/pc/contact.png" width="250"> | <img src="./images/pc/setting.png" width="250"> |

| 客服-AI助手 | 客服-常用语 | 客服-访客信息 |
| :----------: | :----------: | :----------: |
| <img src="./images/pc/chat-ai.png" width="250">| <img src="./images/pc/chat-cs.png" width="250"> | <img src="./images/pc/chat-userinfo.png" width="250"> |

## [移动客户端](https://github.com/Bytedesk/bytedesk-mobile)

- [github](https://github.com/Bytedesk/bytedesk-mobile)

## [网页版](https://github.com/bytedesk/bytedesk-react)

| 自定义按钮颜色 |  按钮放在窗口左下角 | 自定义按钮边距 | 自定义聊天窗口边距 |
| :----------: | :----------: | :----------:  | :----------: |
| <img src="./images/visitor-web/button-color.png" width="250"> | <img src="./images/visitor-web/button-left.png" width="250"> | <img src="./images/visitor-web/button-margin.png" width="250"> | <img src="./images/visitor-web/iframe-margin.png" width="250"> |

| 自定义聊天窗口宽度 |  全屏聊天窗口 | iframe聊天窗口 | 嵌入式聊天窗口 |
| :----------: | :----------: | :----------:  | :----------: |
| <img src="./images/visitor-web/iframe-width.png" width="250"> | <img src="./images/visitor-web/chat-full.png" width="250"> | <img src="./images/visitor-web/chat-iframe.png" width="250"> | <img src="./images/visitor-web/chat-embed.png" width="250"> |

## 对话SDK

<!-- - [iOS-oc](./visitor/oc)
- [iOS-swift](./visitor/swift)
- [Android](./visitor/android)
- [Flutter](./visitor/flutter)
- [React](./visitor/react)
- [React-native](./visitor/react-native)
- [UniApp](./visitor/uniapp)
- [Web](./visitor/web) -->
<!-- - [iOS-oc](https://github.com/Bytedesk/bytedesk-oc) -->
<!-- - [React-native](https://github.com/bytedesk/bytedesk-react-native) -->
<!-- - [Vue](https://github.com/bytedesk/bytedesk-vue) -->
<!-- - [Browser-Extension](https://github.com/Bytedesk/bytedesk-browser-extention) -->
<!-- - [Vscode-plugin](https://github.com/bytedesk/bytedesk-vscode-plugin) -->
- [iOS-swift](https://github.com/Bytedesk/bytedesk-swift)
- [Android](https://github.com/bytedesk/bytedesk-android)
- [Flutter](https://github.com/bytedesk/bytedesk-flutter)
- [React](https://github.com/bytedesk/bytedesk-react)
- [UniApp](https://github.com/bytedesk/bytedesk-uniapp)
- [Web](https://github.com/bytedesk/bytedesk-web)

## 客户端

- [Windows](https://www.weiyuai.cn/download.html)
- [Mac](https://www.weiyuai.cn/download.html)
- [Linux](https://www.weiyuai.cn/download.html)
- [Android](https://www.weiyuai.cn/download.html)
- [IOS](https://www.weiyuai.cn/download.html)

## 技术栈

<!-- - [sofaboot](https://github.com/sofastack/sofa-boot/blob/master/README_ZH.md) for im server 基于金融级云原生架构-->
- [springboot-3.x for 后端](https://github.com/Bytedesk/bytedesk)
- [python for ai](https://github.com/Bytedesk/bytedesk-ai)
- [react for web前端](https://github.com/Bytedesk/bytedesk-react)
- [flutter for 移动客户端(ios&android)](https://github.com/Bytedesk/bytedesk-mobile)
- [electron for 桌面客户端(windows&mac&linux)](https://github.com/Bytedesk/bytedesk-desktop)

## 联系

- [Email](mailto:270580156@qq.com)
- [微信](./images/wechat.png)
- 微语技术支持群：
- <img src="./images/wechat_group.jpg" width="200">
- 如群二维码过期，请添加微信，备注: 微语
- <img src="./images/wechat.png" width="100">

## [写在前面](https://www.weiyuai.cn/)

<!-- - 在保留原有商标logo等信息前提下，支持免费商用。如需移除，需要获得授权 -->
<!-- - 仅支持企业内部员工自用，销售、二次销售或者部署SaaS方式销售需要获得授权。 -->
<!-- - 代理合作：您负责销售，我方负责售后，维护等，五五分成。联系[微信](./images/wechat.png) -->
<!-- - 此软件可能存在bug或不完善的地方，如造成损失，需自行负责 -->
- 此为开源社区版，支持完全免费商用
- 严禁用于含有木马、病毒、色情、赌博、诈骗等违法违规业务，否则我司将暂停或终止其服务，并协助有关行政机关等进行追索和查处
