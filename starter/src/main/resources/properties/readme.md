<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-27 09:49:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-27 09:49:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# Properties

- open - 开源
- local - 本地
- prod - 生产

## local 模块拆分约定

- 目录：`properties/local/`
- 命名：`NN-module-name.properties`（`NN` 为两位数字，控制加载顺序）
- 入口：`application-local.properties` 通过 `spring.config.import` 统一引用
- `spring.config.import` 支持换行（每行一个文件），使用 `\` 续行

当前已拆分示例：

- `30-core-business.properties`
- `40-oauth-ldap-logging.properties`
- `50-datasource.properties`
- `51-jpa-web-actuator.properties`
- `60-mq-mail-quartz.properties`
- `70-ai-batch-liquibase.properties`
- `80-flowable-log.properties`
- `90-thirdparty-cloud.properties`
- `99-docker-compose.properties`

## noai 模块拆分约定

- 目录：`properties/noai/`
- 命名：`NN-module-name.properties`（`NN` 为两位数字，控制加载顺序）
- 入口：`application-noai.properties` 通过 `spring.config.import` 统一引用
- `spring.config.import` 支持换行（每行一个文件），使用 `\` 续行

当前已拆分示例：

- `30-core-business.properties`
- `40-oauth-ldap-logging.properties`
- `50-datasource.properties`
- `51-jpa-web-actuator.properties`
- `60-mq-mail-quartz.properties`
- `70-ai-batch-liquibase.properties`
- `80-flowable-log.properties`
- `90-thirdparty-cloud.properties`
- `99-docker-compose.properties`

## open 模块拆分约定

- 目录：`properties/open/`
- 命名：`NN-module-name.properties`（`NN` 为两位数字，控制加载顺序）
- 入口：`application-open.properties` 通过 `spring.config.import` 统一引用
- `spring.config.import` 支持换行（每行一个文件），使用 `\` 续行

当前已拆分示例：

- `30-core-business.properties`
- `40-oauth-ldap-logging.properties`
- `50-datasource.properties`
- `51-jpa-web-actuator.properties`
- `60-mq-mail-quartz.properties`
- `70-ai-batch-liquibase.properties`
- `80-flowable-log.properties`
- `90-thirdparty-cloud.properties`
- `99-docker-compose.properties`

## prod 模块拆分约定

- 目录：`properties/prod/`
- 命名：`NN-module-name.properties`（`NN` 为两位数字，控制加载顺序）
- 入口：`application-prod.properties` 通过 `spring.config.import` 统一引用
- `spring.config.import` 支持换行（每行一个文件），使用 `\` 续行

当前已拆分示例：

- `30-core-business.properties`
- `40-oauth-ldap-logging.properties`
- `50-datasource.properties`
- `51-jpa-web-actuator.properties`
- `60-mq-mail-quartz.properties`
- `70-ai-batch-liquibase.properties`
- `80-flowable-log.properties`
- `90-thirdparty-cloud.properties`
- `99-docker-compose.properties`
