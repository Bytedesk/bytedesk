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

- open - 开源发布
- noai - 本地开发（关闭 AI）
- local - 私有本地开发
- prod - 私有线上部署（模板源，部署时拷贝到 jar 外部 `./config/`）
- （kingbase 单体 `application-kingbase.properties` 暂未纳入本结构，见 docs/plans/2026-08-26-starter-properties-refactor-plan.md）

## 主题拆分约定（2026-08-26 重组）

- 目录：`properties/{profile}/`，每个 profile 目录内**一主题一文件，文件名即内容**（如 `redis.properties`、`ai-zhipuai.properties`、`call-freeswitch.properties`），跨 profile 同名同义，仅值不同；
- 入口：`application-{profile}.properties` 通过 `spring.config.import` 显式清单引用（local/noai/open 为 classpath，prod 为 jar 外部 `file:./config/`）；
- **Spring Boot 4.x import 不支持 `*.properties` 文件通配（仅目录通配 `*/`）**，因此新增主题文件后必须同步更新对应 application-{profile}.properties 的 import 清单；
- 一致性校验：仓库根目录执行 `python3 scripts/check_properties_import.py`（清单与目录一致性 + 跨文件重复键检查）；
- `datasource/` 子目录（local/noai/open）：`common.properties` + 按库文件 `{mysql,postgresql,oracle,kingbase,h2}.properties`，通过 `${bytedesk.datasource.active}` 占位符选择导入；prod 为扁平单文件 `datasource.properties`；
- 同一 profile 目录内不同主题文件不得定义同名活动键（校验脚本强制）。

## 主题文件清单（各 profile 目录内基本一致）

core-custom / core-organization / core-admin / core-jwt / core-testing / core-booking /
socket / upload / minio / preview / kbase / ip / push / sms / watermark /
call-freeswitch / call-mrcp / webrtc / redis / oauth / logging /
jpa / web / actuator / springdoc / mq / mail / quartz / multipart /
ai-models / ai-neo4j / ai-zhipuai / ai-dashscope / ai-deepseek / ai-ollama / ai-providers / ai-observation / ai-asr-tts / ai-searxng / ai-mcp / ai-skills /
elasticsearch / flowable / batch / thirdparty-aliyun / thirdparty-tencent / thirdparty-wechat / thirdparty-douyin / translate / docker-compose / etcd

prod 另有：datasource.properties（单文件）、liangshibao.properties（客户项目）
noai/open 无：ai-deepseek.properties（该 profile 无 DeepSeek 键，属真实状态）

## 历史对照（旧编号 → 新主题）

| 旧 | 新 |
| --- | --- |
| 30-core-business | core-* / socket / upload / minio / preview / kbase / ip / push / sms / watermark / etcd |
| 31-call-freeswitch | call-freeswitch / call-mrcp |
| 32-webrtc | webrtc |
| 33-cache-redis | redis |
| 40-oauth-ldap | oauth |
| 41-logging | logging |
| 51-jpa-web-actuator | jpa / web / actuator / springdoc / ai-observation |
| 60-mq-mail-quartz | mq / mail / quartz / multipart |
| 70-ai-batch | ai-models / ai-* 系列 / batch |
| 71-elasticsearch | elasticsearch / kbase |
| 75-mcp | ai-mcp |
| 76-skills | ai-skills |
| 80-flowable | flowable |
| 90-thirdparty-cloud | thirdparty-* / translate / upload(provider) / liangshibao(prod) |
| 99-docker-compose | docker-compose |

- `99-docker-compose.properties`
