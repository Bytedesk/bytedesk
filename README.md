<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-05-15 12:10:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# bytedesk

AI powered customer service & team im

## Language

- [English](./README.md)
- [中文](./README.zh.md)

## Introduction

### Enterprise Instant Messaging/IM

- Multi-level organizational structure
- role management
- Permission management
- Chat record management

### Online customer service

- Support multiple channels, multiple routing strategies, and detailed assessment indicators
- Seating workbench
- Work order system
- Seat management
- Data dashboard
- manual knowledge base
- Skill group management
- Real-time monitoring
- Announcements
- sensitive words
- CRM
- Report function,
- Provide customers with integrated customer service workbench services

### Large model AI assistant

- More suitable for team use, one person configuration, for use throughout the company

### Local Area Network File Transfer

- No need to log in or connect to the internet, use WiFi/hotspots to transfer files across platforms

<!-- ## Technical Stack

- [springboot-3.2.0 jdk17/maven/mysql8.0 or postgresql](https://spring.io/projects/spring-boot) for backend
- [react-18.2.0](https://reactjs.org/) for frontend
- [react-native-0.73.4](https://reactnative.dev/) for mobile(ios&android)
- [electron-29.1.1](https://www.electronjs.org/) for desktop(windows&mac&linux) -->

## Getting Started

```bash
# Notice: this repo is still under ative development, 
# many features are not completed or not stable，the docs are not completed
git clone https://github.com/Bytedesk/bytedesk.git
# config file: bytedesk/starter/src/main/resources/application-dev.properties
cd bytedesk/starter
mvn spring-boot:run
# release jar:
cd bytedesk/starter
mvn package -Dmaven.test.skip=true
java -jar bytedesk-starter-0.0.1-SNAPSHOT.jar
# background run:
# nohup java -jar bytedesk-starter-0.0.1-SNAPSHOT.jar
# 
# local preview:
developer: http://localhost:9003/dev
web: http://localhost:9003/
admin: http://localhost:9003/admin, user/password: admin@email.com/admin
chat: http://localhost:9003/chat, user/password: admin@email.com/admin
visitor: http://localhost:9003/v
api docs: http://localhost:9003/swagger-ui/index.html
actuator: http://localhost:9003/im/actuator
h2-console: http://localhost:9003/h2-console, path: ./h2db/weiyuim, user/password: sa/sa
```

## Docs

- [Docs](https://www.weiyuai.cn/docs/)

## Features

- [x] Web

## Preview

- [Web](https://www.weiyuai.cn/)
- [Admin](https://www.weiyuai.cn/admin)
- [Web Chat](https://www.weiyuai.cn/chat)
- [Visitor](https://www.weiyuai.cn/v)

## SDK

- [iOS-oc](./visitor/oc)
- [iOS-swift](./visitor/swift)
- [Android](./visitor/android)
- [Flutter](./visitor/flutter)
- [React](./visitor/react)
- [React-native](./visitor/react-native)
- [UniApp](./visitor/uniapp)
- [Web](./visitor/web)
<!-- - [iOS-oc](https://github.com/Bytedesk/bytedesk-oc)
- [iOS-swift](https://github.com/Bytedesk/bytedesk-swift)
- [Android](https://github.com/bytedesk/bytedesk-android)
- [Flutter](https://github.com/bytedesk/bytedesk-flutter)
- [React](https://github.com/bytedesk/bytedesk-react)
- [React-native](https://github.com/bytedesk/bytedesk-react-native)
- [UniApp](https://github.com/bytedesk/bytedesk-uniapp)
- [Web](https://github.com/bytedesk/bytedesk-web)
- [Browser-Extension](https://github.com/bytedesk/bytedesk-browser-extension)
- [Vscode-plugin](https://github.com/bytedesk/bytedesk-vscode-plugin) -->

## Download

- [TODO: Windows](https://www.weiyuai.cn/download.html)
- [TODO: Mac](https://www.weiyuai.cn/download.html)
- [TODO: Linux](https://www.weiyuai.cn/download.html)

| chat | contact | setting |
| :----------: | :----------: | :----------: |
| <img src="./images/pc/chat.png" width="250"> | <img src="./images/pc/contact.png" width="250"> | <img src="./images/pc/setting.png" width="250"> |

- [TODO: Android](https://www.weiyuai.cn/download.html)
- [TODO: IOS](https://www.weiyuai.cn/download.html)

## Contact

- [Email](mailto:270580156@qq.com)
- [Wechat](./images/wechat.png)

## License

- support business usage
- selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
- 仅支持企业内部员工自用，销售、二次销售或者部署SaaS方式销售需要获得授权，请勿用于非法用途。
