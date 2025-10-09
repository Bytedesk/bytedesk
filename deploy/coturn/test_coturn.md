# Coturn 测试指南

## 问题说明

Coturn 是 STUN/TURN 服务器，使用 UDP/TCP 协议，**不是 HTTP 服务器**。因此：
- ❌ 不能直接通过浏览器访问 `http://14.103.165.199:3478/stats`
- ❌ Nginx 无法直接代理 STUN/TURN 协议到 HTTPS

## 正确的测试方法

### 方法 1：使用 WebRTC 在线测试工具（推荐）

访问以下任一测试工具：

1. **WebRTC Trickle ICE 测试**（最常用）
   - 地址：https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/
   - 操作步骤：
     ```
     STUN or TURN URI: stun:14.103.165.199:3478
     或
     TURN URI: turn:14.103.165.199:3478
     Username: username1
     Password: password1
     ```
   - 点击 "Gather candidates" 按钮
   - 如果成功，会显示 `srflx` 或 `relay` 类型的候选地址

2. **IceTest 测试**
   - 地址：https://icetest.info/
   - 输入服务器信息进行测试

### 方法 2：启用 Coturn Web Admin 界面

修改 `/etc/turnserver.conf` 配置文件，添加以下配置：

```bash
# 启用 Web Admin（HTTPS）
web-admin

# Web Admin 监听地址（0.0.0.0 表示所有接口）
web-admin-ip=0.0.0.0

# Web Admin 端口
web-admin-port=8080

# Web Admin 监听在工作线程上
web-admin-listen-on-workers
```

然后重启 Coturn：
```bash
sudo systemctl restart coturn
```

防火墙开放 8080 端口：
```bash
sudo ufw allow 8080
```

访问：`https://14.103.165.199:8080`（注意是 HTTPS，不是 HTTP）

**注意**：需要配置 SSL 证书才能访问 Web Admin。

### 方法 3：启用 Prometheus 监控（推荐用于监控）

修改 `/etc/turnserver.conf`：

```bash
# 启用 Prometheus 导出器
prometheus
```

重启 Coturn：
```bash
sudo systemctl restart coturn
```

防火墙开放 9641 端口：
```bash
sudo ufw allow 9641
```

访问：`http://14.103.165.199:9641/metrics`

这会返回 Prometheus 格式的监控数据。

### 方法 4：使用命令行测试工具

#### 使用 turnutils_uclient 测试
```bash
# 测试 STUN
turnutils_stunclient 14.103.165.199

# 测试 TURN（需要提供用户名和密码）
turnutils_uclient -v -u username1 -w password1 14.103.165.199
```

#### 使用 telnet 测试端口连通性
```bash
# 测试 TCP 端口是否开放
telnet 14.103.165.199 3478

# 或使用 nc
nc -zv 14.103.165.199 3478
```

### 方法 5：查看 Coturn 运行状态

```bash
# 查看服务状态
sudo systemctl status coturn

# 查看实时日志
sudo journalctl -u coturn -f

# 查看端口监听情况
sudo netstat -tulnp | grep turnserver
# 或
sudo ss -tulnp | grep turnserver

# 检查进程
ps aux | grep turnserver
```

## 创建本地测试页面

可以创建一个简单的 HTML 测试页面来测试 TURN 服务器。

## Nginx 配置说明

**重要**：Nginx 无法直接代理 STUN/TURN 协议。您的 Nginx 配置 `weiyuai_cn_coturn_443.conf` 中的 `/stats`、`/admin` 等路径都无法工作。

如果需要通过域名访问：
- **Web Admin**：可以通过 Nginx 代理到 8080 端口（需要先启用 Web Admin）
- **Prometheus 监控**：可以通过 Nginx 代理到 9641 端口

修改后的 Nginx 配置建议：

```nginx
# 代理到 Prometheus 监控
location /metrics {
    proxy_pass http://14.103.165.199:9641;
    # ... 其他配置
}

# 代理到 Web Admin（如果启用）
location /admin {
    proxy_pass https://14.103.165.199:8080;
    # ... 其他配置
}
```

## 推荐配置

为了便于监控和测试，建议启用 Prometheus：

1. 编辑 `/etc/turnserver.conf`
2. 取消注释 `prometheus` 行
3. 重启服务：`sudo systemctl restart coturn`
4. 访问：`http://14.103.165.199:9641/metrics`
5. 可以配置 Nginx 代理使其通过 `https://coturn.weiyuai.cn/metrics` 访问

## 常见问题排查

### 1. 连接被拒绝
```bash
# 检查 Coturn 是否运行
sudo systemctl status coturn

# 检查端口是否监听
sudo netstat -tulnp | grep 3478
```

### 2. 防火墙问题
```bash
# 检查防火墙状态
sudo ufw status

# 开放必要端口
sudo ufw allow 3478/tcp
sudo ufw allow 3478/udp
sudo ufw allow 5349/tcp
sudo ufw allow 5349/udp
sudo ufw allow 49152:65535/udp  # TURN relay 端口范围
```

### 3. 查看详细日志
```bash
# 实时查看日志
sudo journalctl -u coturn -f

# 或者配置文件中启用详细日志
# 在 /etc/turnserver.conf 中添加：
verbose
log-file=/var/log/turnserver.log
```

## 总结

- ✅ 使用 WebRTC 测试工具（https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/）
- ✅ 启用 Prometheus 监控并通过 Nginx 代理
- ✅ 使用命令行工具测试
- ❌ 不要尝试直接通过浏览器访问 STUN/TURN 端口
