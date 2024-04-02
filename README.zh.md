<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-04-02 23:40:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# 微语 - 企业即时通讯 & 在线客服系统 & 大模型AI助手

基于AI的企业IM和在线客服系统

## 语言

- [English](./README.md)
- [中文](./README.zh.md)

## 介绍

- 企业IM
- 在线客服
- 大模型AI助手
- 局域网文件传输，类似airdrop

## 技术栈

- [springboot-3.2.0 jdk17](https://spring.io/projects/spring-boot) for 后端
- [react-18.2.0](https://reactjs.org/) for web前端
- [react-native-0.73.4](https://reactnative.dev/) for 移动客户端(ios&android)
- [electron-29.1.0](https://www.electronjs.org/) for 桌面客户端(windows&mac&linux)

## 快速开始

```bash
# 注意: 此开源版本处于早期阶段，许多功能尚未完善或测试未完成，文档尚待完善，请勿在生产环境使用
git clone https://github.com/Bytedesk/bytedesk.git
mvn install -Dmaven.test.skip=true
# 打开 bytedesk/starter/src/main/resources/application-dev.properties
# 修改 the value of spring.datasource.url and spring.datasource.username and spring.datasource.password
# 修改 the value of spring.redis.host and spring.redis.port
cd bytedesk/starter
mvn spring-boot:run
# 
# 本地预览
web: http://localhost:9003/
管理后台: http://localhost:9003/admin, 用户名: admin, 密码: admin
web客服端: http://localhost:9003/chat, 用户名: admin, 密码: admin
访客对话窗口: http://localhost:9003/v
api文档: http://localhost:9003/swagger-ui/index.html
```

## 文档

- [文档](https://www.weiyuai.cn/docs/)

## 功能

- TODO:

## 预览

- [官网](https://www.weiyuai.cn/)
- [管理后台](https://www.weiyuai.cn/admin)
- [WebIM/客服端](https://www.weiyuai.cn/chat)
- [客服访客端](https://www.weiyuai.cn/v)

## Download

- [TODO: Windows](https://www.weiyuai.cn/download.html)
- [TODO: Mac](https://www.weiyuai.cn/download.html)
- [TODO: Linux](https://www.weiyuai.cn/download.html)
- [TODO: Android](https://www.weiyuai.cn/download.html)
- [TODO: IOS](https://www.weiyuai.cn/download.html)

## 联系

- [Email](mailto:270580156@qq.com)
- [微信](./wechat.png)

## [声明](https://www.weiyuai.cn/)

- 支持商用
- 仅支持企业内部员工自用，销售、二次销售或者部署SaaS方式销售需要获得授权，请勿用于非法用途。
