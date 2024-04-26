<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-04-26 14:11:29
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

AI powered customer service & team collaboration

## Language

- [English](./README.md)
- [中文](./README.zh.md)

## Introduction

- team im
- customer service
- ai chatbot
- local file send, airdrop

## Technical Stack

- [springboot-3.2.0 jdk17/maven/mysql8.0 or postgresql](https://spring.io/projects/spring-boot) for backend
- [react-18.2.0](https://reactjs.org/) for frontend
- [react-native-0.73.4](https://reactnative.dev/) for mobile(ios&android)
- [electron-29.1.1](https://www.electronjs.org/) for desktop(windows&mac&linux)

## Getting Started

- Notice: this repo is still under ative development, many features are not completed or not stable，the docs are not completed

```bash
git clone https://github.com/Bytedesk/bytedesk.git
# open bytedesk/starter/src/main/resources/application-dev.properties
# change the value of spring.datasource.url and spring.datasource.username and spring.datasource.password
# change the value of spring.redis.host and spring.redis.port
cd bytedesk/starter
mvn spring-boot:run
# 
# local preview:
web: http://localhost:9003/
admin: http://localhost:9003/admin, default user/password: admin/admin
chat: http://localhost:9003/chat, default user/password: admin/admin
visitor: http://localhost:9003/v
api docs: http://localhost:9003/swagger-ui/index.html
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
<!-- - [Browser-Extension](https://github.com/bytedesk/bytedesk-browser-extension) -->
<!-- - [Vscode-plugin](https://github.com/bytedesk/bytedesk-vscode-plugin) -->

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
