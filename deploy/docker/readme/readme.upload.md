# 文件存储切换指南：Local / MinIO / RustFS

本文面向部署和运维人员，说明如何将新上传文件从本地磁盘切换到 MinIO 或 RustFS，以及如何切回本地。

## 1. 默认行为与切换前准备

- `./start.sh` 默认启动应用和基础中间件，**不启动 MinIO/RustFS，应用默认使用本地存储**。
- `./start.sh all minio` / `./start.sh all rustfs` 仅额外启动对应组件，**不会自动切换应用存储**。
- MinIO 和 RustFS 均使用 S3 协议，共用 `bytedesk.storage.*` 配置，无需单独创建 `rustfs.properties`。
- 源码运行的上传目录、安全限制及 MinIO/RustFS 配置已统一放入各 profile 的 `upload.properties`；
  Docker 部署则通过 `.env` 中的环境变量配置，无需修改源码配置文件。
- 以下 Docker 命令均在 `deploy/docker` 目录执行；已有 `.env` 不要直接用示例文件覆盖。
- 使用包含存储配置绑定修复的应用镜像；旧镜像可能报 `No setter found for property: storage-access-key`，详见 [RustFS 排障说明](./readme.rustfs.md)。

切换前：

1. 备份数据库、现有上传目录和部署配置。
2. 确认目标存储容量、访问权限和网络连通性。
3. 生产环境设置独立强凭据，不使用默认密码；不要将真实凭据提交到 Git。
4. 确认文件访问地址能被应用容器和最终用户浏览器访问。

> **重要：切换配置只影响后续上传，不会自动搬迁历史文件或更新数据库中的文件 URL。**
> Docker 本地上传默认保存在 `bytedesk_upload_data` 数据卷，容器内目录为 `/app/uploads`。
> 不要删除旧数据卷或使用 `down -v` 清理存量数据。

## 2. Docker：Local 切换到 MinIO

### 2.1 修改 `.env`

使用统一 S3 配置，添加或修改以下键；每个键只保留一个有效定义：

```dotenv
# MinIO 服务端凭据：替换占位符
MINIO_ROOT_USER=REPLACE_WITH_ACCESS_KEY
MINIO_ROOT_PASSWORD=REPLACE_WITH_STRONG_SECRET

# 应用侧：使用统一 S3 配置
BYTEDESK_STORAGE_PROVIDER=s3
BYTEDESK_STORAGE_ENDPOINT=https://storage.example.com
BYTEDESK_STORAGE_ACCESS_KEY=REPLACE_WITH_ACCESS_KEY
BYTEDESK_STORAGE_SECRET_KEY=REPLACE_WITH_STRONG_SECRET
BYTEDESK_STORAGE_BUCKET_NAME=bytedesk
BYTEDESK_STORAGE_PUBLIC_READ=true
```

应用侧 access/secret 必须与服务端凭据一致，或使用已授权访问该桶的专用账号。
`storage.example.com` 是占位域名，需事先配置 DNS、TLS 和指向 MinIO S3 API 的反向代理。
也可以使用应用和浏览器均可访问的 `http://服务器地址:19000`。

### 2.2 启动

```bash
./start.sh all minio
docker logs --tail 100 minio-bytedesk
docker logs --tail 200 bytedesk
```

若使用 PostgreSQL、RabbitMQ 等非默认组合，保留原来的关键字，例如：

```bash
./start.sh postgresql rabbitmq all minio
```

MinIO API 映射到宿主机 `19000`，控制台映射到 `19001`。
控制台使用 `MINIO_ROOT_USER` / `MINIO_ROOT_PASSWORD` 登录。

## 3. Docker：Local 切换到 RustFS

### 3.1 修改 `.env`

```dotenv
# RustFS 服务端凭据：替换占位符
RUSTFS_ACCESS_KEY=REPLACE_WITH_ACCESS
RUSTFS_SECRET_KEY=REPLACE_WITH_STRONG_SECRET

BYTEDESK_STORAGE_PROVIDER=s3
BYTEDESK_STORAGE_ENDPOINT=https://storage.example.com
BYTEDESK_STORAGE_ACCESS_KEY=REPLACE_WITH_ACCESS
BYTEDESK_STORAGE_SECRET_KEY=REPLACE_WITH_STRONG_SECRET
BYTEDESK_STORAGE_BUCKET_NAME=bytedesk
BYTEDESK_STORAGE_PUBLIC_READ=true
```

两处应用与服务端凭据须一致或具有相应授权。按所部署的 RustFS 版本检查凭据格式限制；
当前部署说明建议 access key 为 5～20 字符、secret key 为 8～40 字符。
地址要求与 MinIO 相同，域名/反向代理应指向 RustFS S3 API。

### 3.2 启动

```bash
./start.sh all rustfs
docker logs --tail 100 rustfs-bytedesk
docker logs --tail 200 bytedesk
```

RustFS 同样使用宿主机 `19000`（API）和 `19001`（控制台）。
控制台使用 `RUSTFS_ACCESS_KEY` / `RUSTFS_SECRET_KEY` 登录。

> MinIO 与 RustFS 默认占用相同宿主机端口，不能同时启动。
> 如之前已启动另一种存储，确认无其他业务依赖后，使用
> `docker stop minio-bytedesk` 或 `docker stop rustfs-bytedesk` 停止旧容器。
> 不要为了切换而删除旧存储的数据卷。

## 4. 地址、公开读与私有桶

### 地址如何选择

| 场景 | S3 API endpoint 示例 |
| --- | --- |
| 应用和存储均在本机运行 | `http://127.0.0.1:19000`（只适合本机访问验证） |
| 应用在 Docker 中，存储在同一网络 | MinIO：`http://bytedesk-minio:9000`；RustFS：`http://bytedesk-rustfs:9000`（仅容器内部可达） |
| 浏览器需要访问上传文件 | 应用和浏览器均可达的域名或服务器地址，例如 `https://storage.example.com` |

**当前实现用同一个 endpoint 连接存储并生成文件 URL。** 容器内部服务名通常不能被浏览器解析，
因此不能仅以“容器连接成功”判断配置正确。Docker 容器中的 `127.0.0.1` 也不是宿主机。
不要将控制台端口 `19001` 配置为 S3 endpoint。

### 访问权限

- `BYTEDESK_STORAGE_PUBLIC_READ=true`：应用尝试设置桶公开读策略，返回不带签名的直链。
  仅在业务允许匿名读取文件时使用。
- `BYTEDESK_STORAGE_PUBLIC_READ=false`：应用跳过设置公开读策略，返回预签名 URL。
  **这不会撤销桶上已有的公开读策略**；切换为私有模式前，应在存储端检查并移除旧公开权限。
- 预签名地址会过期，最长 7 天，不应将其当作永久有效链接；需评估聊天历史、知识库等长期引用场景。
- 预签名 URL 的域名参与签名，不要在返回后替换域名。反向代理需正确保留签名相关的 Host、路径和查询参数。

## 5. 源码运行：修改对应 profile 的 properties

### 5.1 配置文件位置

应用侧配置无需依赖 Docker 环境变量，修改实际生效的上传与存储配置文件：

| profile | 配置位置 |
| --- | --- |
| local | [properties/local/upload.properties](../../../starter/src/main/resources/properties/local/upload.properties) |
| open | [properties/open/upload.properties](../../../starter/src/main/resources/properties/open/upload.properties) |
| noai | [properties/noai/upload.properties](../../../starter/src/main/resources/properties/noai/upload.properties) |
| prod | 默认导入运行目录下的 `./config/prod/upload.properties`；参考 [仓库模板](../../../starter/src/main/resources/properties/prod/upload.properties) |
| kingbase | [application-kingbase.properties](../../../starter/src/main/resources/application-kingbase.properties) |

上传与存储配置统一放在 `upload.properties`，不再单独维护 `storage.properties` 或 `minio.properties` 模板，
存储仅使用 `bytedesk.storage.*`，不再读取旧存储配置键，也不回退到旧配置。
prod 仅从 `./config/prod/upload.properties` 加载上传与存储配置，不再导入旧存储文件。
升级前先备份，将原有设置转换为 `bytedesk.storage.*` 并合并到该文件；
使用对象存储时必须显式设置 `bytedesk.storage.provider=s3` 并填写地址和凭据。
不要直接用仓库模板覆盖生产配置。其他 profile 的 classpath 只导入合并后的文件，自定义导入路径也需同步更新。

### 5.2 上传配置与存储开关的关系

上传及存储配置集中管理，存储仅由 provider 选择：

| 配置项 | 用途 |
| --- | --- |
| `bytedesk.upload.dir` / `bytedesk.upload.url` | 本地上传目录和访问地址 |
| `bytedesk.upload.security.*` | 上传文件大小、类型、文件名等安全限制，切换存储时仍需保留 |
| `bytedesk.storage.provider=s3` | 启用统一 S3 兼容存储，适用于 MinIO 和 RustFS |
| `bytedesk.storage.endpoint/access-key/secret-key/bucket-name` | 对象存储连接和桶配置 |

### 5.3 在同一文件中切换存储

MinIO 与 RustFS 使用相同的配置格式，填入对应服务地址和凭据：

```properties
# 以下为对象存储配置
bytedesk.storage.provider=s3
bytedesk.storage.endpoint=https://storage.example.com
bytedesk.storage.access-key=REPLACE_WITH_ACCESS_KEY
bytedesk.storage.secret-key=REPLACE_WITH_STRONG_SECRET
bytedesk.storage.bucket-name=bytedesk
bytedesk.storage.region=us-east-1
bytedesk.storage.public-read=true
# 私有桶可改为 false，并设置预签名有效天数（1～7）
# bytedesk.storage.presigned-expiry-days=7
```

替换文件中已有配置或取消对应示例注释，不要重复追加相同键。修改后按原有方式重新构建/重启应用；
classpath 内的配置修改需要重新打包。检查启动参数和环境变量，避免它们覆盖文件中的值。

**存储类型仅通过 `bytedesk.storage.provider` 选择，取值为 `local` 或 `s3`。**
当前普通文件上传流程使用 S3 开关决定存储分支；对象存储关闭时，才使用本地上传目录和 URL。
本地上传设置见 [upload.properties](../../../starter/src/main/resources/properties/local/upload.properties)。

## 6. 切换后如何验证

1. 检查存储服务已就绪，再检查应用日志，确认未出现配置绑定、连接、认证或桶策略错误。
   应用会记录“初始化 S3 兼容对象存储客户端”；**客户端创建成功不等于文件上传成功**。
2. 在应用中上传一张小图片和一个普通附件。
3. 登录存储控制台，确认目标桶中新增了对应对象。
4. 在实际用户浏览器中打开新返回的文件 URL，验证下载、图片显示和外网地址可达性。
5. 检查切换前的历史附件仍能访问，必要时保留旧文件服务入口。

修改 `.env` 后重新执行对应 `./start.sh ...` 更新应用容器，**仅 `docker restart bytedesk` 不会更新环境变量**。
脚本生成的 `.env.app` 不要手工维护；检查 shell 中是否有未更新的 `BYTEDESK_STORAGE_*`
导出变量，它们优先于 `.env`。

源码运行修改的是上述 `upload.properties`：classpath 配置需重新构建并重启；
prod 外部 `./config/prod/upload.properties` 修改后需重启应用。两种方式都应完成上述真实上传验证，
不能仅凭启动成功判断切换完成。排障时不要分享包含 access key、secret key 的完整配置或环境变量输出。

## 7. 切回本地存储

Docker 修改 `.env`：

```dotenv
BYTEDESK_STORAGE_PROVIDER=local
BYTEDESK_UPLOAD_URL=https://客服系统域名
```

随后执行 `./start.sh`，非默认数据库/MQ 部署保留原关键字。
源码运行则设置 `bytedesk.storage.provider=local`，
并检查对应 `upload.properties` 的 `bytedesk.upload.dir` / `bytedesk.upload.url`。

重新上传文件，验证落到本地上传目录且文件 URL 可访问。
`./start.sh` 不会自动停止此前已运行的对象存储容器；确认历史文件已迁移或不再依赖旧服务后再停止。

## 8. 历史文件迁移注意事项

- 配置切换不等于数据迁移：旧本地文件不会自动上传到对象存储，旧 URL 也不会自动改写。
- 迁移须同时考虑文件内容、对象 key、数据库引用及聊天消息/知识库等历史 URL。
  不要只复制文件或直接全库替换域名。
- 建议维护窗口内暂停上传，备份后制定映射方案，先用少量文件验证，再批量迁移。
- 回滚时同样要保留新存储中的文件：切回 local 不会自动下载已上传到 S3 的对象。

更多组件细节参见 [MinIO 部署说明](./readme.minio.md) 和 [RustFS 部署说明](./readme.rustfs.md)。
