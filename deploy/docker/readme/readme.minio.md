<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# MinIO 对象存储（MinIO Object Storage）

## 中文说明

MinIO（`compose/compose-minio.yaml`，一镜像一文件）默认不随主栈启动，通过 `start.sh` 关键字或 compose 组合启用。MinIO 用于存储图片、音频、视频等上传文件。

访问地址：

- MinIO Console: <http://127.0.0.1:19001>（使用 `.env` 中的 `MINIO_ROOT_USER` / `MINIO_ROOT_PASSWORD` 登录）
- MinIO API: <http://127.0.0.1:19000>

```bash
# 方式 A（推荐）：启动中间件/应用栈时附带 MinIO
./start.sh all minio
./start.sh middleware minio
./stop.sh stop all minio
./stop.sh down all minio

# 方式 B：仅 MinIO（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-minio.yaml up -d

# 方式 C：与现有 compose 组合
docker compose --env-file .env -f compose/compose-minio.yaml up -d

# 查看 MinIO 运行状态
docker compose --env-file .env -f compose/compose-minio.yaml ps
docker compose --env-file .env -f compose/compose-minio.yaml logs -f
```

### 启用应用侧 MinIO 存储

仅启动 MinIO 容器不会改变应用的上传行为。应用通过 `bytedesk.minio.enabled` 配置决定是否使用 MinIO：

- 关闭（默认）：上传文件保存在应用本地磁盘，应用可独立启动，不依赖 MinIO 容器
- 开启：上传文件写入 MinIO（应用启动时会自动创建存储桶并设置为公开读取）

```bash
# docker 全量启动时，在 .env 中配置（容器内通过服务名访问）
BYTEDESK_MINIO_ENABLED=true
BYTEDESK_MINIO_ENDPOINT=http://bytedesk-minio:9000
BYTEDESK_MINIO_BUCKET_NAME=bytedesk

# 或源码本地运行时，在 properties 中配置（宿主机端口）
# bytedesk.minio.enabled=true
# bytedesk.minio.endpoint=http://127.0.0.1:19000
# bytedesk.minio.access-key=...
# bytedesk.minio.secret-key=...
# bytedesk.minio.bucket-name=bytedesk
```

### 代码侧开关说明

- `bytedesk.minio.enabled=false`（默认）时，`MinioConfig`（MinioClient Bean）与 `UploadMinioService` 均不会装配，应用启动完全不依赖 MinIO
- `UploadRestService` 通过 `ObjectProvider<UploadMinioService>` 可选注入；上传接口在开关关闭或 Bean 不存在时自动回退本地存储
- 开启后可调用 `/api/v1/minio/*` 系列接口进行上传、预签名下载等操作

## English

MinIO runs in its own file `compose/compose-minio.yaml` (one image per file) and does not start with the default stack; enable it via the `minio` keyword or compose composition. MinIO stores uploaded files (images, audio, video, etc.).

Access:

- MinIO Console: <http://127.0.0.1:19001> (sign in with `MINIO_ROOT_USER` / `MINIO_ROOT_PASSWORD` from `.env`)
- MinIO API: <http://127.0.0.1:19000>

```bash
# Option A (recommended): start the stack with MinIO attached
./start.sh all minio
./start.sh middleware minio
./stop.sh stop all minio
./stop.sh down all minio

# Option B: MinIO only (requires bytedesk-network to exist first; run from deploy/docker)
docker compose --env-file .env -f compose/compose-minio.yaml up -d

# Option C: combine with existing compose files
docker compose --env-file .env -f compose/compose-minio.yaml up -d

# Inspect MinIO status and logs
docker compose --env-file .env -f compose/compose-minio.yaml ps
docker compose --env-file .env -f compose/compose-minio.yaml logs -f
```

### Enable MinIO storage on the app side

Starting the MinIO container alone does not change upload behavior. The app decides via the `bytedesk.minio.enabled` config:

- Disabled (default): uploads are stored on the app's local disk; the app starts without any MinIO dependency
- Enabled: uploads are written to MinIO (the app auto-creates the bucket with a public-read policy on startup)

```bash
# Docker full stack: configure in .env (the app container reaches MinIO via the service name)
BYTEDESK_MINIO_ENABLED=true
BYTEDESK_MINIO_ENDPOINT=http://bytedesk-minio:9000
BYTEDESK_MINIO_BUCKET_NAME=bytedesk

# Or for local source runs, configure in properties (host port)
# bytedesk.minio.enabled=true
# bytedesk.minio.endpoint=http://127.0.0.1:19000
# bytedesk.minio.access-key=...
# bytedesk.minio.secret-key=...
# bytedesk.minio.bucket-name=bytedesk
```

### How the code-side switch works

- When `bytedesk.minio.enabled=false` (default), `MinioConfig` (the MinioClient bean) and `UploadMinioService` are not loaded at all; startup has zero MinIO dependency
- `UploadRestService` injects `UploadMinioService` via `ObjectProvider` (optional); upload endpoints fall back to local storage when the switch is off or the bean is absent
- Once enabled, the `/api/v1/minio/*` endpoints provide upload and presigned-download operations
