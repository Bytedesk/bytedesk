# 环境变量说明 / Environment Variables

所有敏感信息集中在 `deploy/docker/.env`（`cp .env.example .env` 后按需修改）。`start.sh`/`stop.sh` 通过 `--env-file` 自动加载。

> 注意：.env 务必使用 UTF-8 编码保存（推荐无 BOM），值中不要包含中文全角引号或首尾空格。

## 变量优先级（应用 DSN/MQ 连接）

`start.sh` 启用 bytedesk 应用时会按所选 db/mq 关键字自动注入连接参数并写入 `.env.app`（自动生成，勿手工编辑）：

```text
shell 环境变量 > .env 显式配置 > .env.app（脚本注入） > compose 文件内置默认值（mysql/artemis）
```

如需完全接管应用连接（例如指向外部数据库），在 `.env` 中显式配置：

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://external-db:3306/bytedesk?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
SPRING_DATASOURCE_USERNAME=bytedesk
SPRING_DATASOURCE_PASSWORD=your-password
# BYTEDESK_MQ_TYPE=artemis|rabbitmq
# SPRING_ARTEMIS_BROKER_URL=tcp://bytedesk-artemis:61616
# SPRING_RABBITMQ_HOST=bytedesk-rabbitmq
```

## 敏感变量清单（至少修改这些）

- **数据库**：`MYSQL_ROOT_PASSWORD`、`POSTGRES_PASSWORD`、`ORACLE_PASSWORD`、`ORACLE_APP_USER_PASSWORD`、`KINGBASE_DB_PASSWORD`、`KINGBASE_SYSTEM_PWD`
- **中间件**：`REDIS_PASSWORD`、`ELASTIC_PASSWORD`、`MINIO_ROOT_PASSWORD`、`KIBANA_SERVICE_ACCOUNT_TOKEN`
- **消息队列**：`ARTEMIS_PASSWORD`、`RABBITMQ_DEFAULT_PASS`
- **应用安全**：`BYTEDESK_ADMIN_PASSWORD`、`BYTEDESK_ADMIN_VALIDATE_CODE`、`BYTEDESK_MEMBER_PASSWORD`、`BYTEDESK_JWT_SECRET_KEY`、`BYTEDESK_LICENSE_KEY`
- **呼叫中心**：`COTURN_PASS`、`FREESWITCH_ESL_PASSWORD`、`FREESWITCH_DOMAIN`、`FREESWITCH_EXTERNAL_SIP_IP`、`FREESWITCH_EXTERNAL_RTP_IP`
- **可选 API Key**：`SPRING_AI_*_API_KEY`、`BYTEDESK_TRANSLATE_BAIDU_*`

## AI 开关（承接旧 noai 场景）

应用默认不启用任何 AI（`SPRING_AI_MODEL_CHAT=none`），等价于旧版 `noai` 场景。启用方法：在 `.env` 配置对应 API Key，并按需添加：

```bash
# 示例：启用智谱对话 + 向量
SPRING_AI_ZHIPUAI_API_KEY=your-key
SPRING_AI_MODEL_CHAT=zhipuai
SPRING_AI_MODEL_EMBEDDING=zhipuai
# 示例：启用 DashScope 语音
SPRING_AI_DASHSCOPE_API_KEY=your-key
SPRING_AI_MODEL_AUDIO_SPEECH=dashscope
SPRING_AI_MODEL_AUDIO_TRANSCRIPTION=dashscope
```

完整模型选择项见 `compose/compose-bytedesk.yaml` 的 AI provider 段落注释。

## 对外访问地址

```bash
# 将 127.0.0.1 替换为服务器 IP 或域名（影响上传/头像/帮助中心等链接）
BYTEDESK_UPLOAD_URL=http://127.0.0.1:9003
BYTEDESK_FEATURES_AVATAR_BASE_URL=http://127.0.0.1:9003
BYTEDESK_KBASE_API_URL=http://127.0.0.1:9003
BYTEDESK_KBASE_HELPCENTER_API_URL=http://127.0.0.1:9003
BYTEDESK_KBASE_BLOG_API_URL=http://127.0.0.1:9003
```

## 密钥与 Jasypt（可选）

部分 compose 配置值可写为 `ENC(...)`。仅在实际使用加密值时才需要把 Jasypt 口令传给容器：

```bash
# 1. 写入 .env（保留在本地，切勿提交）
echo 'JASYPT_ENCRYPTOR_PASSWORD=please-change-me' >> .env
# 2. 正常启动，服务自动读取
./start.sh
```

- 不使用加密值时留空或删除该行，启动回退明文
- 可通过 `BYTEDESK_SECURITY_JASYPT_ALGORITHM` 等变量覆盖算法/迭代次数
