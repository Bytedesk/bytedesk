<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-03-29 22:26:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# bytedesk - team im && customer service && ai

AI powered team cooperation & customer service

- [English](./README.md)
- [中文](./README.zh.md)

## Introduction

Bytedesk is a team collaboration and customer service platform.

## Technical Stack

- [springboot-3.2.0](https://spring.io/projects/spring-boot)
- [react-18.2.0](https://reactjs.org/)
- [electron-29.1.0](https://www.electronjs.org/)

## Getting Started

```bash
git clone https://github.com/Bytedesk/bytedesk.git
# open bytedesk/starter/src/main/resources/application-dev.properties
# change the value of spring.datasource.url and spring.datasource.username and spring.datasource.password
# change the value of spring.redis.host and spring.redis.port
cd bytedesk/starter
mvn spring-boot:run
# 
# other commands for reference
# mvn spring-boot:run
# mvn clean
# mvn dependency:tree
# mvn dependency:analyze
# mvn dependency:resolve
# mvn compile -Dmaven.test.skip=true
# mvn install -Dmaven.test.skip=true
# mvn package -Dmaven.test.skip=true
# lsof -i:port，其中：port 为端口号, lsof -i:9003
# kill -9 pid，其中：pid 为进程 pid
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
- [Chat](https://www.weiyuai.cn/chat)
- [Visitor](https://www.weiyuai.cn/v)

## Download

- [TODO: Desktop](https://www.weiyuai.cn/download.html)
- [TODO: Android](https://www.weiyuai.cn/download.html)
- [TODO: IOS](https://www.weiyuai.cn/download.html)

## Contact

- [Email](mailto:270580156@qq.com)
- [Wechat](./wechat.png)

## License

- support business usage
- selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
- 仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售，请勿用于非法用途。
