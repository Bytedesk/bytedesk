# 快速开始 / Quick Start

## 安装步骤 / Installation

```bash
# 1. 克隆项目 / clone
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker

# 2. 配置环境变量 / configure env
cp .env.example .env
# 编辑 .env，至少修改以下敏感值：
#   MYSQL_ROOT_PASSWORD / REDIS_PASSWORD / ELASTIC_PASSWORD
#   BYTEDESK_ADMIN_PASSWORD / BYTEDESK_JWT_SECRET_KEY
# 完整变量说明见 readme.env.md

# 3. 启动默认全栈 / start default stack
./start.sh
# 等待应用就绪后访问：
#   管理后台: http://127.0.0.1:9003
#   默认账号: admin@email.com / admin
```

## 目录结构 / Layout

```bash
deploy/docker/
├── compose/               # compose 文件统一目录（一镜像一文件，共 20 个）
│   ├── compose-<镜像名>.yaml
│   ├── grafana/ logstash/ searxng/   # 各组件配置（与对应 compose 文件同目录）
│   ├── prometheus.yml      # Prometheus 抓取配置
│   └── ik-plugin-cache/    # ES IK 分词插件离线缓存
├── start.sh / stop.sh     # 关键字组合启停脚本
├── watchdog.sh            # bytedesk 应用看门狗
├── .env / .env.example    # 环境变量（敏感信息集中在 .env）
├── readme/                # 详细说明文档
└── one/                   # all-in-one 单文件部署（可选）
```

## 常见首次启动问题 / FAQ

**Q: 启动后 9003 无法访问？**

```bash
# 查看应用日志
docker logs -f bytedesk
# 常见原因：
# 1. .env 中必填变量为空（如 MYSQL_ROOT_PASSWORD）
# 2. 端口冲突（9003/9885 被占用）
# 3. 应用还在启动中（首次启动需 1-2 分钟初始化数据库）
```

**Q: 如何确认所有容器正常？**

```bash
docker compose --env-file .env -p bytedesk \
  -f compose/compose-redis.yaml -f compose/compose-elasticsearch.yaml \
  -f compose/compose-mysql.yaml -f compose/compose-artemis.yaml \
  -f compose/compose-bytedesk.yaml ps
```

**Q: .env 保存注意事项**

- 务必使用 UTF-8 编码（推荐无 BOM）
- 值中不要包含中文全角引号或首尾空格
- `start.sh`/`stop.sh` 通过 `--env-file` 自动加载，无需 source

**Q: 源码本地开发如何配合？**

```bash
# 仅启动中间件
./start.sh middleware
# 然后在项目根目录用 mvnw 启动 starter（默认 profile: local）
JASYPT_ENCRYPTOR_PASSWORD=<value> ./starter/mvnw -f starter/pom.xml spring-boot:run
```
