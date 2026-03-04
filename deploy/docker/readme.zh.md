<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:27:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

## 文件说明

```bash
.
├── compose-base.yaml # 公共基础中间件服务（不含 bytedesk 镜像）
├── compose-db-mysql.yaml # MySQL 数据库覆盖（默认）
├── compose-db-postgresql.yaml # PostgreSQL 数据库覆盖
├── compose-db-oracle.yaml # Oracle 数据库覆盖
├── compose-mq-artemis.yaml # Artemis MQ 组件覆盖（默认）
├── compose-mq-rabbitmq.yaml # RabbitMQ MQ 组件覆盖
├── compose-app-bytedesk.yaml # bytedesk 镜像服务（已从 base 拆分）
├── compose-app-mq-artemis.yaml # bytedesk 的 Artemis 配置覆盖
├── compose-app-mq-rabbitmq.yaml # bytedesk 的 RabbitMQ 配置覆盖
├── compose-scenario-call.yaml # 呼叫中心场景扩展（coturn/freeswitch/janus）
├── compose-call-db-mysql.yaml # call 场景下 FreeSWITCH 的 MySQL DSN 覆盖
├── compose-call-db-postgresql.yaml # call 场景下 FreeSWITCH 的 PostgreSQL DSN 覆盖
├── compose-scenario-noai.yaml # 不使用 AI 的场景覆盖
├── compose-scenario-standard.yaml # 标准场景覆盖
├── start.sh # 组合启动脚本：start.sh <db> <mq> <scenario> [all|middleware]
└── stop.sh # 组合停止脚本：stop.sh <db> <mq> <scenario> [stop|down] [all|middleware]
```

## 微语启动步骤

```bash
# https://www.weiyuai.cn/docs/zh-CN/docs/deploy/docker
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git
# 进入docker目录
cd bytedesk/deploy/docker
# 配置环境变量，根据需要修改
cp .env.example .env
# 重要：密码/API Key/JWT 等敏感信息已统一迁移到 .env
# start.sh/stop.sh 会通过 --env-file 自动加载 deploy/docker/.env
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
# 说明：ollama 已经放到 compose-base.yaml 公共组件
# 仅启动中间件（用于源码启动）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml up -d

# 全量启动（中间件 + bytedesk 镜像）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-rabbitmq.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-rabbitmq.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d

# 切换数据库示例
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d
docker compose -p bytedesk -f compose-base.yaml -f compose-db-oracle.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 脚本方式（推荐）
# 参数格式：
# start.sh <db> <mq> <scenario> <target>
# stop.sh  <db> <mq> <scenario> <action> <target>

# 本地测试（middleware，仅启动中间件）
# 1) MySQL + Artemis + 标准场景（源码本地开发）
./start.sh mysql artemis standard middleware
./stop.sh mysql artemis standard stop middleware
./stop.sh mysql artemis standard down middleware

# 2) MySQL + RabbitMQ + 标准场景（MQ 切换联调）
./start.sh mysql rabbitmq standard middleware
./stop.sh mysql rabbitmq standard stop middleware
./stop.sh mysql rabbitmq standard down middleware

# 3) PostgreSQL + Artemis + 标准场景（数据库切换验证）
./start.sh postgresql artemis standard middleware
./stop.sh postgresql artemis standard stop middleware
./stop.sh postgresql artemis standard down middleware

# 4) PostgreSQL + RabbitMQ + noai（禁用 AI 依赖）
./start.sh postgresql rabbitmq noai middleware
./stop.sh postgresql rabbitmq noai stop middleware
./stop.sh postgresql rabbitmq noai down middleware

# 5) Oracle + Artemis + noai（源码本地调试推荐）
./start.sh oracle artemis noai middleware
./stop.sh oracle artemis noai stop middleware
./stop.sh oracle artemis noai down middleware

# 6) 呼叫中心中间件场景
./start.sh mysql artemis call middleware
./stop.sh mysql artemis call stop middleware
./stop.sh mysql artemis call down middleware

./start.sh mysql rabbitmq call middleware
./stop.sh mysql rabbitmq call stop middleware
./stop.sh mysql rabbitmq call down middleware

./start.sh postgresql rabbitmq call middleware
./stop.sh postgresql rabbitmq call stop middleware
./stop.sh postgresql rabbitmq call down middleware

# 线上发布（all，中间件 + bytedesk 应用镜像）
# 1) MySQL + Artemis + 标准场景（默认发布组合）
./start.sh mysql artemis standard all
./stop.sh mysql artemis standard stop all
./stop.sh mysql artemis standard down all

# 2) PostgreSQL + RabbitMQ + 标准场景（RabbitMQ 方案发布）
./start.sh postgresql rabbitmq standard all
./stop.sh postgresql rabbitmq standard stop all
./stop.sh postgresql rabbitmq standard down all

# 3) MySQL + RabbitMQ + 呼叫中心场景（呼叫中心发布）
./start.sh mysql rabbitmq call all
./stop.sh mysql rabbitmq call stop all
./stop.sh mysql rabbitmq call down all

# 4) PostgreSQL + RabbitMQ + 呼叫中心场景（呼叫中心发布）
./start.sh postgresql rabbitmq call all
./stop.sh postgresql rabbitmq call stop all
./stop.sh postgresql rabbitmq call down all

# 参数速查：
# db: mysql | postgresql | oracle
# mq: artemis | rabbitmq
# scenario: standard | noai | call
# 注意：call 场景支持 mysql 与 postgresql（oracle 尚未验证）
# target: middleware | all
# action: stop(停止容器) | down(删除容器，保留卷)

# 组合说明（建议收藏）
# 默认值（缺省参数时）：
# start.sh 等价于：./start.sh mysql artemis standard all
# stop.sh  等价于：./stop.sh  mysql artemis standard stop all
# db 也支持别名：pg -> postgresql
# 可通过环境变量自定义 compose 项目名（默认 bytedesk）：
# PROJECT_NAME=bytedesk-dev ./start.sh mysql artemis standard middleware

# 拉取ollama模型
# 对话模型
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# 嵌入模型
docker exec ollama-bytedesk ollama pull bge-m3:latest
# 重新排序Rerank模型
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# 停止标准模式
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
# 停止呼叫中心模式
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
# 停止（noai）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml down
```

## 敏感变量（统一放在 `.env`）

启动前至少配置以下变量：

- 数据库与消息队列：`MYSQL_ROOT_PASSWORD`、`POSTGRES_PASSWORD`、`ORACLE_PASSWORD`、`ORACLE_APP_USER_PASSWORD`、`ARTEMIS_PASSWORD`、`RABBITMQ_DEFAULT_PASS`
- 中间件：`REDIS_PASSWORD`、`ELASTIC_PASSWORD`、`MINIO_ROOT_PASSWORD`
- 应用认证：`BYTEDESK_ADMIN_PASSWORD`、`BYTEDESK_ADMIN_VALIDATE_CODE`、`BYTEDESK_MEMBER_PASSWORD`、`BYTEDESK_JWT_SECRET_KEY`
- 呼叫场景：`COTURN_PASS`、`FREESWITCH_ESL_PASSWORD`
- 可选 API 密钥：`SPRING_AI_*_API_KEY`、`BYTEDESK_TRANSLATE_BAIDU_*`、`BYTEDESK_LICENSE_KEY`

## 密钥与 Jasypt（可选）

如果在 docker compose 环境变量中把敏感信息写成 `ENC(...)`，则需要在容器启动时注入解密口令；未使用加密时可以忽略。

```bash
# 1. 将口令写入 .env（仅保留在本地，切勿提交）
echo 'JASYPT_ENCRYPTOR_PASSWORD=请修改成强口令' >> .env

# 2. 正常启动全量 compose 文件，服务会自动读取变量
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
# 或
docker compose -p bytedesk -f compose-base.yaml -f compose-db-postgresql.yaml -f compose-mq-artemis.yaml -f compose-scenario-call.yaml -f compose-call-db-postgresql.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
# 或
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-noai.yaml -f compose-app-bytedesk.yaml -f compose-app-mq-artemis.yaml up -d
```

- 当没有加密内容时，保持该变量为空即可，Jasypt 会自动降级为明文。
- 需要调整算法或迭代次数时，可额外设置 `BYTEDESK_SECURITY_JASYPT_ALGORITHM`、`BYTEDESK_SECURITY_JASYPT_KEY_OBTENTION_ITERATIONS` 等环境变量。

## FreeSWITCH 数据库兼容说明（MySQL / PostgreSQL）

数据库端口、账号、密码等信息已统一从 `deploy/docker/.env` 读取，不再写死在 compose 覆盖文件和 `switch.conf.*.xml` 中。

建议至少配置以下变量：

```dotenv
MYSQL_DATABASE=bytedesk
MYSQL_ROOT_USER=root
MYSQL_ROOT_PASSWORD=请改成强密码
MYSQL_PORT=3306
MYSQL_HOST_PORT=13306

POSTGRES_DB=bytedesk
POSTGRES_USER=postgres
POSTGRES_PASSWORD=请改成强密码
POSTGRES_PORT=5432
POSTGRES_HOST_PORT=15432

FREESWITCH_DB_HOST=bytedesk-db
```

为避免 FreeSWITCH 在不同数据库场景下出现 `core-db-dsn` 初始化失败，`call` 场景使用了按数据库覆盖的方式：

- MySQL：
  - 覆盖文件：`compose-call-db-mysql.yaml`
  - 关键挂载：`switch.conf.mysql.xml`、`modules.conf.mysql.xml`、`pre_load_modules.conf.mysql.xml`
  - 关键 DSN：`mariadb://Server=bytedesk-db;Port=3306;Database=bytedesk;Uid=root;Pwd=...;`

- PostgreSQL：
  - 覆盖文件：`compose-call-db-postgresql.yaml`
  - 关键挂载：`switch.conf.pgsql.xml`、`modules.conf.pgsql.xml`、`pre_load_modules.conf.pgsql.xml`
  - 关键 DSN：`pgsql://host=bytedesk-db dbname=bytedesk user=postgres password=...`
  - 注意：`hostaddr` 只能写 IP，不能写服务名；容器网络服务名应使用 `host=bytedesk-db`。

### 常见问题排查

1) 报错 `CORE DATABASE INITIALIZATION FAILURE`：

- 优先检查当前数据库场景是否挂载了对应的 `switch.conf.*.xml` 与 `modules/pre_load` 覆盖文件。

1) 从 PostgreSQL 切到 MySQL（或反向切换）后仍连接旧库：

- 建议使用 `--remove-orphans` 重新创建容器，避免旧容器/网络别名残留。

```bash
docker compose -p bytedesk \
 -f compose-base.yaml \
 -f compose-db-mysql.yaml \
 -f compose-mq-rabbitmq.yaml \
 -f compose-scenario-call.yaml \
 -f compose-call-db-mysql.yaml \
 -f compose-app-bytedesk.yaml \
 -f compose-app-mq-rabbitmq.yaml \
 up -d --force-recreate --remove-orphans bytedesk-db bytedesk-freeswitch
```

1) 验证 FreeSWITCH 是否就绪：

```bash
docker exec freeswitch-bytedesk bash -lc "(echo >/dev/tcp/127.0.0.1/8021) >/dev/null 2>&1 && echo TCP_8021_OPEN || echo TCP_8021_CLOSED"
docker exec freeswitch-bytedesk fs_cli -H 127.0.0.1 -P 8021 -p bytedesk123 -x "status"
```

## 停止和重启服务

```bash
# 停止所有服务（保留数据）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down

# 停止所有服务并删除数据卷（谨慎操作，会删除所有数据）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down -v

# 重启特定服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml restart bytedesk

# 重启所有服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml restart
```

## 升级bytedesk镜像

```bash
# 1. 停止当前服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down
# 或
docker stop bytedesk redis-bytedesk elasticsearch-bytedesk ollama-bytedesk mysql-bytedesk artemis-bytedesk

# 2. 拉取最新镜像
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest

# 3. 重新启动服务（会自动使用最新镜像）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 或者使用以下命令强制重新构建并启动
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d --force-recreate bytedesk
```

## 删除MySQL数据挂载

如果需要删除MySQL数据挂载并重新初始化数据库，请按以下步骤操作：

```bash
# 1. 停止所有服务
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down

# 1. 强制删除MySQL容器（即使它已经退出）
docker rm -f mysql-bytedesk

# 2. 现在可以删除数据卷了
docker volume rm bytedesk_mysql_data

# 3. 重新启动服务（会自动创建新的数据卷和数据库）
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 注意：删除数据卷后，所有数据都会丢失，需要重新初始化管理员账户
```

## 数据卷管理

```bash
# 查看所有数据卷
docker volume ls | grep bytedesk

# 查看数据卷详细信息
docker volume inspect bytedesk_mysql_data
docker volume inspect bytedesk_redis_data
docker volume inspect bytedesk_elasticsearch_data
docker volume inspect bytedesk_upload_data
docker volume inspect bytedesk_ollama_models
docker volume inspect bytedesk_artemis_data

# 备份数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz -C /data .

# 恢复数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /data
```

## 故障排除

如果遇到数据库连接问题或服务启动失败，可以尝试以下步骤：

```bash
# 查看容器状态
docker ps -a

# 查看服务日志
docker logs mysql-bytedesk
docker logs bytedesk

# 如果服务启动失败，可以尝试重启
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml down
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml up -d

# 检查网络连接
docker network inspect bytedesk-network

# 清理未使用的资源
docker system prune -f
```

## 常用命令

```bash
# 查看服务状态
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml ps

# 查看服务日志
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml logs

# 查看特定服务日志
docker compose -p bytedesk -f compose-base.yaml -f compose-db-mysql.yaml -f compose-mq-artemis.yaml -f compose-scenario-standard.yaml logs bytedesk

# 进入容器内部
docker exec -it bytedesk /bin/bash
docker exec -it mysql-bytedesk mysql -u root -p

# 查看容器资源使用情况
docker stats

# 查看容器镜像架构
docker inspect registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest --format='{{.Architecture}}'
```
