<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-01-27 15:48:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# 微语

- 50 人以内免费
- 免费版需要保留 logo

## TODO:

- 压力测试
- 集群付费解决方案
- 后端创建 model，通过命令一键生成相关 api 文件，前端通过配置直接生成 CRUD 页面，并可生成统计 chart 页面

```bash
# proxy
export http_proxy=http://127.0.0.1:10818
export https_proxy=http://127.0.0.1:10818
#
# code ~/.zshrc
# source ~/.zshrc
# change to java 17
# spring --version
#
# brew install gradle
# gradle --version
# gradle dependencies
gradle --refresh-dependencies
# start
# ./gradlew bootRun
#
# run gradle with hot reload
# 1. open one terminal, run：
gradle build --continuous
# 2. open second terminal, run:
gradle bootRun
#
# build jar
gradle bootJar
# see jar detail
jar tvf build/libs/bytedesk-1.0.jar
# run jar
java -jar build/libs/bytedesk-1.0.jar
#
# http://localhost:9003
```

```bash
# flyway db migration

```

- [spring boot docs](https://docs.spring.io/spring-boot/docs/current/reference/html/index.html)
- [spring boot properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties.core)
