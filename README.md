<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-03-29 18:17:21
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

## Getting Started

```bash
git clone https://github.com/Bytedesk/bytedesk.git
mvn install -Dmaven.test.skip=true
# open bytedesk/starter/src/main/resources/application-dev.properties
# change the value of spring.datasource.url and spring.datasource.username and spring.datasource.password
# change the value of spring.redis.host and spring.redis.port
cd bytedesk/starter
mvn spring-boot:run
# local preview:
# web: http://localhost:9003/
# admin: http://localhost:9003/admin, default user/password: admin/admin
# api docs: http://localhost:9003/swagger-ui/index.html
```

## Preview

- [Web](https://www.weiyuai.cn/)
- [Web Admin](https://www.weiyuai.cn/admin)

## Contact

- [Email](mailto:270580156@qq.com)
- [Wechat](./wechat.png)

## [Notice](https://www.weiyuai.cn/)

- support business usage
- selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
- 仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售，请勿用于非法用途。
