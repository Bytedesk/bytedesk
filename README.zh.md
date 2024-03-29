<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-03-29 18:17:42
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

- [English](./README.md)
- [中文](./README.zh.md)

## 快速开始

```bash
git clone https://github.com/Bytedesk/bytedesk.git
mvn install -Dmaven.test.skip=true
# 打开 bytedesk/starter/src/main/resources/application-dev.properties
# 修改 the value of spring.datasource.url and spring.datasource.username and spring.datasource.password
# 修改 the value of spring.redis.host and spring.redis.port
cd bytedesk/starter
mvn spring-boot:run
# 本地预览
# web: http://localhost:9003/
# admin: http://localhost:9003/admin
# api docs: http://localhost:9003/swagger-ui/index.html
```

## 预览

- [官网](https://www.weiyuai.cn/)
- [管理后台](https://www.weiyuai.cn/admin)

## 常用命令

```bash
# vscode 点击左侧的debug，运行和调试，会自动热加载
- cd bytedesk/starter
- 启动：mvn spring-boot:run
- 清理：mvn clean
- 查看依赖：mvn dependency:tree
- 依赖分析：mvn dependency:analyze
- 手动拉取依赖：mvn dependency:resolve
- mvn compile -Dmaven.test.skip=true
- 本地安装：mvn install -Dmaven.test.skip=true
- 本地打包：mvn package -Dmaven.test.skip=true
- 查看端口占用：lsof -i:port，其中：port 为端口号, lsof -i:9003
- 杀掉进程：kill -9 pid，其中：pid 为进程 pid
```

## 联系

- [Email](mailto:270580156@qq.com)
- [微信](./wechat.png)

## [声明](https://www.weiyuai.cn/)

- 支持商用
- 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售，请勿用于非法用途。
