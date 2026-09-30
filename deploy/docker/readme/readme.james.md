<!--
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# James 私有邮件服务器（Apache James Mail Server）

微语提供独立的邮件服务器 compose 文件 `compose/compose-james.yaml`（一镜像一文件），包含 [Apache James](https://james.apache.org/server/index.html) JPA 单机版（SMTP/IMAP/POP3/WebAdmin，数据存储在内置 Derby 数据库），通过关键字 `james` 按需启动，自动接入 `bytedesk-network`，用于完善微语**企业内部协同**——企业内部员工之间的邮件沟通（员工邮箱账号、内部收发、邮件数据完全私有化）。

- 官方文档：<https://james.apache.org/server/index.html>
- 镜像：<https://hub.docker.com/r/apache/james>（tag: `jpa-latest`）

## 镜像选型说明

官方提供 5 个镜像变体，微语选用 **`jpa`**（零外部依赖单机版）：

| 变体 | 存储/依赖 | 评估 |
| --- | --- | --- |
| **`jpa`（选用）** | 内置 Derby + 文件系统 Lucene 索引，零外部依赖 | 单机 compose 一键拉起，数据卷持久化，规模匹配企业内部员工邮箱 |
| `distributed` | 需 Cassandra + RabbitMQ + S3 + OpenSearch | 面向多节点大规模部署，为内网邮件引入 4 个重依赖不划算 |
| `cassandra` | 需外部 Cassandra + ES | 官方新版本已标记弃用（让位 distributed/postgres） |
| `demo` | 同 jpa，预置固定账号/密码 | 试用定位；固定弱口令有安全隐患，且与 JAMES_DOMAIN/JAMES_USERNAME 初始化流程冲突 |
| `memory` | 纯内存 | 官方定位 testing，重启数据全丢，不可用于真实邮件 |

补充：`jpa-latest` 当前对应 3.9.x，生产环境建议在 `.env` 中固定版本（`JAMES_IMAGE=apache/james:jpa-3.9.0`）避免大版本漂移；未来若需将邮件数据并入 PostgreSQL 统一运维（微语 3.x 默认库），可评估 3.8+ 的 `postgres` 变体（外置 PG 存储，引入对 PG 实例的强依赖）。

- 注意：镜像仅 `linux/amd64`，Apple Silicon（M 系列 Mac）上通过 Rosetta 模拟运行，可用但启动较慢；生产环境建议 amd64 主机

## 功能定位（重要）

James **不是**用来替代第三方邮箱发送轻量邮件的，两者定位互补：

| 场景 | 推荐方案 | 说明 |
| --- | --- | --- |
| 系统通知、验证码、工单通知等轻量邮件 | **对接第三方邮箱**（阿里云企业邮箱、QQ 企业邮箱等） | 免运维、公网送达率高、自带反垃圾信誉；在管理后台「平台邮件设置」配置第三方 SMTP 即可，无需自建邮件服务器 |
| **企业内部员工之间的邮件沟通** | **自建 James（本组件）** | 完善微语企业内部协同能力：为员工分配 `姓名@企业域名` 邮箱、内部收发、IMAP/POP3 客户端接入，邮件数据完全私有化 |

当然，在纯内网/完全私有化环境中，也可以将平台邮件（验证码等）切换到 James 发送（见下文「微语应用对接 James（可选）」）；公网生产环境的对外邮件仍推荐第三方邮箱。

## 启动 James

`start.sh` / `stop.sh` 支持关键字 `james`，用于启停 `compose/compose-james.yaml`：

```bash
cd deploy/docker

# 方式 A（推荐）：在启动中间件/应用栈时附带 James
./start.sh all james                # 全量栈 + 私有邮件服务器
./start.sh middleware james         # 仅中间件 + James（源码本地开发）
./stop.sh stop all james
./stop.sh down all james            # 删除容器，数据卷保留

# 方式 B：仅 James（需先确保 bytedesk-network 存在；在 deploy/docker 目录执行）
docker compose --env-file .env -f compose/compose-james.yaml up -d
```

启动完成后，`start.sh` 会自动初始化（幂等，已存在则跳过）：

- 邮件域：`.env` 中的 `JAMES_DOMAIN`（默认 `bytedesk.local`）
- 默认邮箱账号：`JAMES_USERNAME`（默认 `support`，自动拼接为 `support@bytedesk.local`）/ `JAMES_PASSWORD`（默认 `bytedesk123`）

## 访问地址与端口

| 服务 | 宿主机地址 | 容器内地址（微语应用容器使用） | 说明 |
| --- | --- | --- | --- |
| SMTP | `127.0.0.1:10025` | `bytedesk-james:25` | 内网收信/中继，**对私网免认证**（供微语应用发信） |
| SMTPS | `127.0.0.1:10465` | `bytedesk-james:465` | SSL + 认证发信（邮件客户端） |
| Submission | `127.0.0.1:10587` | `bytedesk-james:587` | STARTTLS + 认证发信（邮件客户端） |
| IMAP | `127.0.0.1:10143` | `bytedesk-james:143` | 收信（明文） |
| IMAPS | `127.0.0.1:10993` | `bytedesk-james:993` | 收信（SSL） |
| POP3 / POP3S | `127.0.0.1:10110` / `127.0.0.1:10995` | `bytedesk-james:110` / `995` | 收信 |
| WebAdmin | <http://127.0.0.1:18000> | — | 管理接口，密码见下方说明 |

- TLS 证书：首次启动通过 `--generate-keystore` 自动生成**自签名证书**（写入 `james-conf` 数据卷持久保留）；465/587/993/995 均使用该证书，邮件客户端需信任自签名证书
- 生产环境建议替换为正式证书 keystore（挂载到 `/root/conf/keystore`，并同步修改 `compose/james/smtpserver.xml` 中的 `secret`，移除 compose 中的 `--generate-keystore`）

## 微语应用对接 James（可选）

> 默认推荐：验证码/系统通知等平台邮件继续对接**第三方邮箱**（送达率更好、免运维）。本节仅适用于纯内网/完全私有化部署，或希望统一走自建邮件服务器的场景。

### 发信（管理后台「平台邮件设置」）

微语平台邮件（验证码/系统通知）的 SMTP 配置存储在数据库中，通过管理后台配置（替代静态 `spring.mail.*`）。如需切换到 James：登录 admin 管理后台 → 系统设置 → 平台邮件设置，填写：

| 配置项 | Docker 全量（应用容器内） | 源码本地运行（宿主机） |
| --- | --- | --- |
| SMTP 服务器 | `bytedesk-james` | `127.0.0.1` |
| SMTP 端口 | `25` | `10025` |
| SSL | 关闭 | 关闭 |
| 发件邮箱（用户名） | `support@bytedesk.local` | `support@bytedesk.local` |
| 密码 | `JAMES_PASSWORD` 的值 | `JAMES_PASSWORD` 的值 |

原理：25 端口的 `authorizedAddresses` 已扩展至 Docker/内网私网段（见 `compose/james/smtpserver.xml`），微语应用容器与内网主机可**免认证中继外发**；该端口不广播 AUTH 能力，应用侧 `mail.smtp.auth=true` 也不会触发认证与 TLS 握手，规避自签名证书信任问题。配置完成后可点击「测试 SMTP 连接」验证。

### 收信（邮件客服/IMAP，规划中）

邮件接收（`bytedesk.mail.receiver.*`）当前为规划功能（properties 中为注释状态），James 的 IMAP 端点已就绪，后续微语实现员工邮箱收信（企业内部协同）功能时可直接对接：

- Docker 全量：`bytedesk-james:143`（IMAP）/ `bytedesk-james:993`（IMAPS）
- 源码本地：`127.0.0.1:10143` / `127.0.0.1:10993`

### 邮件客户端（Outlook/Foxmail 等）收发

- 收信：IMAPS `127.0.0.1:10993`（或 IMAP `10143`），账号 `support@bytedesk.local`
- 发信：SMTPS `127.0.0.1:10465` 或 Submission `127.0.0.1:10587`，需认证（`JAMES_PASSWORD`），并信任自签名证书

## 常用管理操作

```bash
# CLI（容器内 james-cli，JMX 9999）
docker exec james-bytedesk james-cli ListDomains                 # 列出邮件域
docker exec james-bytedesk james-cli ListUsers                   # 列出邮箱账号
docker exec james-bytedesk james-cli AddDomain mail.example.com  # 新增邮件域
docker exec james-bytedesk james-cli AddUser alice@mail.example.com '密码'  # 新增邮箱账号
docker exec james-bytedesk james-cli SetPassword alice@mail.example.com '新密码'

# WebAdmin REST（密码默认 bytedesk-james-admin，见 compose/james/webadmin.properties）
curl -H 'Password: bytedesk-james-admin' http://127.0.0.1:18000/domains
curl -XPUT -H 'Password: bytedesk-james-admin' http://127.0.0.1:18000/domains/mail.example.com
curl -XPUT -H 'Password: bytedesk-james-admin' -H 'Content-Type: application/json' \
  -d '{"password":"secret123"}' http://127.0.0.1:18000/users/alice@mail.example.com

# 查看 James 状态与日志
docker logs -f james-bytedesk
docker compose --env-file .env -f compose/compose-james.yaml ps
```

## 配置覆盖说明

`compose/james/` 目录存放覆盖官方镜像内置默认配置的文件（均 bind mount 到 `/root/conf/`，相对路径基于 `compose/` 目录）：

| 文件 | 覆盖内容 | 官方默认 |
| --- | --- | --- |
| `smtpserver.xml` | 25 端口 `authorizedAddresses` 扩展为 `127.0.0.0/8,172.16.0.0/12,192.168.0.0/16`（私网免认证中继）；465/587 保持官方默认 | 仅 `127.0.0.0/8`，应用容器无法经 25 端口外发 |
| `webadmin.properties` | WebAdmin 监听 `0.0.0.0` + 固定密码 `bytedesk-james-admin` | 监听 `localhost` + 每次启动随机密码（写入日志） |

其余配置（imapserver.xml、pop3server.xml、mailetcontainer.xml、domainlist.xml 等）使用镜像内置默认，如需深度定制可将文件放入 `james-conf` 数据卷（`docker cp` 或直接编辑卷内文件）后重启容器。

## 数据持久化与重置

- 数据卷：`james-conf`（配置 + keystore）、`james-var`（邮件与 Derby 数据库）、`james-logs`（日志）
- 修改 `.env` 中的 `JAMES_PASSWORD` 后：已有账号密码**不会**自动更新，需执行 `docker exec james-bytedesk james-cli SetPassword <账号> '<新密码>'`
- 彻底重置：`./stop.sh down james all` 后删除数据卷（`docker volume rm bytedesk_james-conf bytedesk_james-var`，注意 down 需带启动时的完整关键字组合）

## 生产环境检查清单

- [ ] `JAMES_DOMAIN` 改为自有域名，并在 DNS 配置 MX/SPF/DKIM/DMARC 记录（否则外发邮件易被收件方拒收或进入垃圾箱）
- [ ] 替换自签名 keystore 为正式证书（Let's Encrypt 等），同步更新 `smtpserver.xml` 各端点的 `keystore`/`secret`
- [ ] 收紧 `smtpserver.xml` 中 25 端口的 `authorizedAddresses`（按实际网络网段），防止内网外被滥用为中继
- [ ] 修改 `webadmin.properties` 中的 WebAdmin 密码，且仅在内网/防火墙保护下暴露 18000 端口
- [ ] 修改 `.env` 中 `JAMES_PASSWORD` 等默认口令

## English

Bytedesk ships a dedicated mail server compose file `compose/compose-james.yaml` (one image per file) containing [Apache James](https://james.apache.org/server/index.html) JPA single-node (SMTP/IMAP/POP3/WebAdmin, embedded Derby storage). Start it with the `james` keyword; it joins `bytedesk-network` automatically. Image variant choice: **`jpa`** (zero external dependencies, volume-persisted, right-sized for internal employee mail) over `distributed` (needs Cassandra+RabbitMQ+S3+OpenSearch, multi-node scale), `cassandra` (deprecated), `demo` (fixed pre-provisioned credentials, trial only) and `memory` (no persistence, testing only). Pin `JAMES_IMAGE=apache/james:jpa-3.9.0` in production to avoid major-version drift.

**Positioning**: James is NOT a replacement for third-party SMTP when sending lightweight transactional mails (system notifications, verification codes, ticket notices) — those are easier and more reliable via third-party providers (Aliyun/QQ enterprise mail etc.) configured in the admin console. James exists to power **enterprise internal collaboration**: employee-to-employee email within the bytedesk platform (per-employee mailboxes like `name@company.tld`, internal sending/receiving, IMAP/POP3 client access, fully private mail data). In fully air-gapped deployments you may also point platform mails at James (optional, see below).

Note: the image is `linux/amd64` only; on Apple Silicon it runs via Rosetta emulation (works, slower startup). Use an amd64 host for production.

```bash
./start.sh all james          # full stack + mail server
./start.sh middleware james   # middleware + James (local source development)
./stop.sh down all james
```

On startup `start.sh` auto-provisions (idempotent) the mail domain (`JAMES_DOMAIN`, default `bytedesk.local`) and the default account (`JAMES_USERNAME` → `support@bytedesk.local`, `JAMES_PASSWORD`).

Ports (host → container): SMTP `10025→25` (private-network relay, **no auth** — for the bytedesk app), SMTPS `10465→465`, submission `10587→587` (both TLS + auth, for mail clients), IMAP `10143→143` / `10993→993`, POP3 `10110→110` / `10995→995`, WebAdmin `18000→8000` (password in `compose/james/webadmin.properties`, default `bytedesk-james-admin`).

To optionally point the bytedesk app at James (third-party SMTP remains the default recommendation), configure admin console → System Settings → Platform Email Settings: host `bytedesk-james` (docker) or `127.0.0.1` (source-local), port `25`/`10025`, SSL off, account `support@bytedesk.local` + `JAMES_PASSWORD`. Port 25 relay is authorized for docker/private networks via the overridden `compose/james/smtpserver.xml` (`authorizedAddresses` `127.0.0.0/8,172.16.0.0/12,192.168.0.0/16`), so no TLS/self-signed-cert issues; use the "Test SMTP connection" button to verify.

TLS uses an auto-generated self-signed keystore (`--generate-keystore`, persisted in the `james-conf` volume); replace it with a real certificate for production. Email receiving (`bytedesk.mail.receiver.*`) is a planned bytedesk feature; the James IMAP endpoints are ready for it.
