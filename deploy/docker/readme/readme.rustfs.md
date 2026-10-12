<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
-->
# RustFS 对象存储（RustFS Object Storage）

## 中文说明

[RustFS](https://github.com/rustfs/rustfs) 是 Rust 编写、Apache 2.0 许可的高性能分布式对象存储，100% S3 协议兼容。MinIO 仓库已 archived 并转向闭源/AGPL，RustFS 是其开源替代——同端口布局（API 9000 / Console 9001）、同 path-style URL，微语应用侧可无缝切换。

`compose/compose-rustfs.yaml`（一镜像一文件）默认不随主栈启动，通过 `start.sh` 关键字启用：

```bash
# 方式 A（推荐）：启动中间件/应用栈时附带 RustFS
./start.sh all rustfs
./start.sh middleware rustfs
./stop.sh stop all rustfs
./stop.sh down all rustfs

# 方式 B：仅 RustFS（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-rustfs.yaml up -d
```

访问地址：

- RustFS Console: <http://127.0.0.1:19001>（使用 `.env` 中的 `RUSTFS_ACCESS_KEY` / `RUSTFS_SECRET_KEY` 登录）
- RustFS S3 API: <http://127.0.0.1:19000>（健康检查 `/minio/health/live`，MinIO 兼容）

注意：RustFS 与 MinIO 使用相同宿主端口（19000/19001），**二者互斥不可同时启动**；切换前先 `docker stop minio-bytedesk`。

### 启用应用侧 RustFS 存储

仅启动 RustFS 容器不会改变应用的上传行为，需在 `.env` 中配置应用侧开关（推荐新命名空间 `bytedesk.storage.*`）：

```bash
# .env（docker 部署，容器内通过服务名访问）
BYTEDESK_STORAGE_PROVIDER=s3
BYTEDESK_STORAGE_ENDPOINT=http://bytedesk-rustfs:9000
BYTEDESK_STORAGE_ACCESS_KEY=rustfsadmin
BYTEDESK_STORAGE_SECRET_KEY=rustfsadmin
BYTEDESK_STORAGE_BUCKET_NAME=bytedesk
```

或使用旧命名空间 `BYTEDESK_MINIO_ENABLED=true` + `BYTEDESK_MINIO_ENDPOINT=http://bytedesk-rustfs:9000` + 对应凭据（向下兼容，效果等同）。

源码本地运行时在 `properties/<profile>/minio.properties` 中配置（宿主机端口 127.0.0.1:19000）。

### 凭据说明

- 默认凭据 `rustfsadmin` / `rustfsadmin`（access 与 secret 相同），**生产必须修改**
- 格式约束：access key 5~20 字符、secret key 8~40 字符，建议纯字母数字（含特殊字符的组合可能鉴权失败）
- 容器以非 root 用户（uid 10001）运行，数据用命名卷 `bytedesk_rustfs_data` 存储；bind-mount 宿主目录需 `chown 10001`

### 从 MinIO 迁移

1. 停 MinIO：`docker stop minio-bytedesk`（RustFS 复用相同端口）
2. 启 RustFS：`./start.sh all rustfs`
3. 迁移存量数据（任选其一）：
   - `rc mirror`（rustfs-cli，Tier 1 支持：`rc alias set src http://127.0.0.1:19000 <AK> <SK>` 后 `rc mirror src/bytedesk dst/bytedesk`）
   - `mc mirror`（MinIO 客户端，Tier 2 支持：`mc alias set` 同理）
4. 应用侧切 endpoint 到 `http://bytedesk-rustfs:9000` 并重启

兼容性实测（2026-10-02，`io.minio:8.6.0` 客户端 + rustfs/rustfs 镜像）：建桶、PutBucketPolicy 公读策略、上传、stat、下载、7 天预签名 URL、匿名直链、删除全部通过——应用侧零代码改动即可切换。

## English

[RustFS](https://github.com/rustfs/rustfs) is a high-performance distributed object storage written in Rust, Apache 2.0 licensed, 100% S3 compatible. With MinIO archived and moving to closed source/AGPL, RustFS is its open-source successor — same port layout (API 9000 / Console 9001), same path-style URLs; the bytedesk app can switch over seamlessly.

`compose/compose-rustfs.yaml` (one image per file) does not start with the default stack; enable it via the `rustfs` keyword:

```bash
# Option A (recommended): start the stack with RustFS attached
./start.sh all rustfs
./start.sh middleware rustfs
./stop.sh stop all rustfs
./stop.sh down all rustfs

# Option B: RustFS only (requires bytedesk-network to exist first; run from deploy/docker)
docker compose --env-file .env -f compose/compose-rustfs.yaml up -d
```

Access:

- RustFS Console: <http://127.0.0.1:19001> (sign in with `RUSTFS_ACCESS_KEY` / `RUSTFS_SECRET_KEY` from `.env`)
- RustFS S3 API: <http://127.0.0.1:19000> (health check `/minio/health/live`, MinIO compatible)

Note: RustFS and MinIO share the same host ports (19000/19001) — they are mutually exclusive; `docker stop minio-bytedesk` before switching.

### Enable RustFS storage on the app side

Starting the RustFS container alone does not change upload behavior; configure the app-side switch in `.env` (new `bytedesk.storage.*` namespace recommended):

```bash
# .env (docker deployment; the app container reaches RustFS via the service name)
BYTEDESK_STORAGE_PROVIDER=s3
BYTEDESK_STORAGE_ENDPOINT=http://bytedesk-rustfs:9000
BYTEDESK_STORAGE_ACCESS_KEY=rustfsadmin
BYTEDESK_STORAGE_SECRET_KEY=rustfsadmin
BYTEDESK_STORAGE_BUCKET_NAME=bytedesk
```

Or use the legacy namespace `BYTEDESK_MINIO_ENABLED=true` + `BYTEDESK_MINIO_ENDPOINT=http://bytedesk-rustfs:9000` + credentials (backward compatible, equivalent).

For local source runs, configure in `properties/<profile>/minio.properties` (host port 127.0.0.1:19000).

### Credentials

- Defaults are `rustfsadmin` / `rustfsadmin` (access and secret identical) — **change them for production**
- Constraints: access key 5–20 chars, secret key 8–40 chars, alphanumeric recommended (combinations with special characters may fail authentication)
- The container runs as a non-root user (uid 10001); data lives in the named volume `bytedesk_rustfs_data`. Bind-mounted host dirs need `chown 10001`

### Migrating from MinIO

1. Stop MinIO: `docker stop minio-bytedesk` (RustFS reuses the same ports)
2. Start RustFS: `./start.sh all rustfs`
3. Migrate existing data (either tool works):
   - `rc mirror` (rustfs-cli, Tier 1: `rc alias set src http://127.0.0.1:19000 <AK> <SK>`, then `rc mirror src/bytedesk dst/bytedesk`)
   - `mc mirror` (MinIO client, Tier 2 support: same `mc alias set` flow)
4. Point the app endpoint to `http://bytedesk-rustfs:9000` and restart

Compatibility verified (2026-10-02, `io.minio:8.6.0` client + rustfs/rustfs image): bucket creation, PutBucketPolicy public-read, upload, stat, download, 7-day presigned URL, anonymous direct link, delete — all passed. The app switches over with zero code changes.
