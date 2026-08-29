# watchdog.sh 使用说明 / Watchdog

`watchdog.sh` 用于监控 bytedesk 应用容器，当容器意外退出时自动拉起，减少生产环境停机时间。

## 快速开始

```bash
cd deploy/docker

# 启动看门狗（后台守护进程；若容器未运行会先自动拉起）
./watchdog.sh start

# 查看状态（看门狗 + 容器 + 重启统计 + 配置）
./watchdog.sh status

# 重启看门狗
./watchdog.sh restart

# 停止看门狗（注意：不会停止 bytedesk 容器本身）
./watchdog.sh stop
```

## 工作机制

1. 每 `WATCHDOG_CHECK_INTERVAL`（默认 10s）检查一次容器 `bytedesk` 状态；
2. 容器退出且退出码非 0（异常退出）时自动重启：
   - 优先 `docker start <容器>` —— 完整保留容器原有环境变量（含 start.sh 注入的数据源/MQ 配置）；
   - 容器已不存在时，回退 `docker compose --env-file .env [--env-file .env.app] -f compose/compose-bytedesk.yaml up -d --no-deps bytedesk` 重建；
3. 容器正常退出（退出码 0，如手工 `docker stop`）不自动拉起，看门狗自身退出；
4. 突发保护：`WATCHDOG_BURST_WINDOW`（默认 300s）内最多重启 `WATCHDOG_MAX_RESTARTS`（默认 5）次，超限停止看门狗并提示人工排查（避免崩溃循环）。

## 配置项（环境变量覆盖）

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| WATCHDOG_CHECK_INTERVAL | 10 | 健康检查间隔（秒） |
| WATCHDOG_STARTUP_WAIT | 90 | 重启后等待应用启动的时间（秒） |
| WATCHDOG_MAX_RESTARTS | 5 | 突发窗口内最大重启次数 |
| WATCHDOG_BURST_WINDOW | 300 | 突发窗口时长（秒） |
| WATCHDOG_LOG_FILE | ./watchdog.log | 日志文件 |
| WATCHDOG_CONTAINER_NAME | bytedesk | 监控的容器名 |
| PROJECT_NAME | bytedesk | compose 项目名（与 start.sh 一致） |

示例：

```bash
WATCHDOG_CHECK_INTERVAL=30 WATCHDOG_MAX_RESTARTS=3 ./watchdog.sh start
```

## 日志与文件

- `watchdog.log`：运行日志（含每次重启记录，用于突发统计）
- `watchdog.pid` / `watchdog.lock`：进程与锁文件（自动管理，勿手工编辑；已加入 .gitignore）

## 常见问题

**Q: 看门狗重启后应用连不上数据库？**

优先 `docker start` 保留原环境，不会出现此问题；仅当容器被删除走 compose 重建分支时，依赖 `.env.app`（由 start.sh 生成，含数据源/MQ 注入）。若你从未通过 start.sh 启动过应用，请先运行一次 `./start.sh all`。

**Q: 如何彻底停掉应用和看门狗？**

```bash
./watchdog.sh stop     # 先停止看门狗
./stop.sh stop all     # 再停止应用（正常退出，看门狗不会误拉起）
```

**Q: 与 docker restart 策略的关系？**

compose-bytedesk.yaml 已配置 `restart: unless-stopped`，Docker 自身会拉起崩溃容器；watchdog 额外提供突发限次保护与状态可视化，适合需要更精细控制的场景，按需启用。
