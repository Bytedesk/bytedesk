# Coturn 可用性测试 - 完整解决方案

## 问题原因

您遇到的问题是因为：

1. **Coturn 不是 HTTP 服务器**
   - Coturn 是 STUN/TURN 服务器，使用 **UDP/TCP** 协议
   - 端口 3478 不提供 HTTP/HTTPS 服务
   - 无法通过浏览器直接访问 `http://14.103.165.199:3478`

2. **Nginx 代理配置错误**
   - 您的 Nginx 配置试图将 HTTPS 请求代理到 UDP/TCP 端口
   - 这在协议层面是不兼容的
   - `/stats`、`/admin` 等路径在默认配置下不存在

## 🎯 正确的测试方法

### 方法 1：使用 WebRTC 测试工具（最简单）

#### A. 在线测试工具

访问：https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/

填写以下信息：

**测试 STUN：**
```
STUN or TURN URI: stun:14.103.165.199:3478
```

**测试 TURN：**
```
TURN URI: turn:14.103.165.199:3478
Username: username1
Credential: password1
```

点击 **"Add Server"**，然后点击 **"Gather candidates"**

**成功标志：**
- STUN 成功：看到 `srflx` 类型的候选
- TURN 成功：看到 `relay` 类型的候选

#### B. 本地测试工具

1. 在浏览器中打开：`test_coturn.html`
2. 填写服务器信息
3. 点击 "开始测试"
4. 查看测试结果

### 方法 2：启用 Prometheus 监控（推荐）

这是唯一可以通过浏览器访问的方式。

#### 自动配置（推荐）

```bash
# 在服务器上运行
sudo bash enable_prometheus.sh
```

#### 手动配置

1. **编辑配置文件**

```bash
sudo nano /etc/turnserver.conf
```

在文件末尾添加：
```bash
# Enable Prometheus exporter
prometheus
```

2. **重启服务**

```bash
sudo systemctl restart coturn
```

3. **开放防火墙端口**

```bash
sudo ufw allow 9641/tcp
```

4. **测试访问**

```bash
# 本地测试
curl http://localhost:9641/metrics

# 外网访问
curl http://14.103.165.199:9641/metrics
```

5. **浏览器访问**

打开浏览器访问：`http://14.103.165.199:9641/metrics`

您会看到类似这样的监控数据：
```
# HELP turn_traffic_rcvp Total received RTP/RTCP packets
# TYPE turn_traffic_rcvp counter
turn_traffic_rcvp 0
# HELP turn_traffic_sentp Total sent RTP/RTCP packets
# TYPE turn_traffic_sentp counter
turn_traffic_sentp 0
...
```

### 方法 3：配置 Nginx 代理（HTTPS 访问）

1. **使用新的 Nginx 配置**

将 `weiyuai_cn_coturn_443_fixed.conf` 复制到服务器：

```bash
# 备份旧配置
sudo cp /etc/nginx/sites-available/weiyuai_cn_coturn_443.conf \
        /etc/nginx/sites-available/weiyuai_cn_coturn_443.conf.backup

# 使用新配置
sudo cp weiyuai_cn_coturn_443_fixed.conf \
        /etc/nginx/sites-available/weiyuai_cn_coturn_443.conf

# 测试配置
sudo nginx -t

# 重新加载
sudo systemctl reload nginx
```

2. **复制测试页面到 Web 目录**

```bash
# 创建目录
sudo mkdir -p /var/www/html/coturn

# 复制测试页面
sudo cp test_coturn.html /var/www/html/coturn/

# 设置权限
sudo chown -R www-data:www-data /var/www/html/coturn
sudo chmod 755 /var/www/html/coturn
```

3. **通过 HTTPS 访问**

- 主页（使用说明）：https://coturn.weiyuai.cn/
- 测试工具：https://coturn.weiyuai.cn/test
- Prometheus 监控：https://coturn.weiyuai.cn/metrics
- 健康检查：https://coturn.weiyuai.cn/health

### 方法 4：命令行测试

```bash
# 1. 测试端口连通性
telnet 14.103.165.199 3478
# 或
nc -zv 14.103.165.199 3478

# 2. 使用 Coturn 自带工具测试 STUN
turnutils_stunclient 14.103.165.199

# 3. 测试 TURN
turnutils_uclient -v -u username1 -w password1 14.103.165.199

# 4. 查看服务状态
sudo systemctl status coturn

# 5. 查看实时日志
sudo journalctl -u coturn -f

# 6. 查看端口监听
sudo netstat -tulnp | grep turnserver
```

## 🔍 故障排查

### 1. 服务未运行

```bash
# 检查状态
sudo systemctl status coturn

# 启动服务
sudo systemctl start coturn

# 查看错误日志
sudo journalctl -u coturn -n 100
```

### 2. 防火墙问题

```bash
# 检查防火墙状态
sudo ufw status

# 开放必要端口
sudo ufw allow 3478/tcp
sudo ufw allow 3478/udp
sudo ufw allow 5349/tcp    # TURNS
sudo ufw allow 5349/udp    # DTLS
sudo ufw allow 49152:65535/udp  # TURN relay 端口范围
sudo ufw allow 9641/tcp    # Prometheus
```

### 3. 端口未监听

```bash
# 检查端口监听
sudo netstat -tulnp | grep 3478
sudo netstat -tulnp | grep 9641

# 如果没有监听，检查配置
sudo nano /etc/turnserver.conf

# 确保以下配置未被注释
# listening-port=3478
# external-ip=14.103.165.199
```

### 4. Prometheus 无法访问

```bash
# 检查配置文件
grep prometheus /etc/turnserver.conf

# 应该看到
# prometheus

# 如果被注释（前面有 #），去掉注释
sudo nano /etc/turnserver.conf

# 重启服务
sudo systemctl restart coturn

# 等待几秒后测试
curl http://localhost:9641/metrics
```

### 5. Nginx 代理不工作

```bash
# 检查 Nginx 配置语法
sudo nginx -t

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/error.log

# 检查后端服务是否可访问
curl http://14.103.165.199:9641/metrics

# 重新加载 Nginx
sudo systemctl reload nginx
```

## 📊 监控数据说明

访问 Prometheus 端点后，您会看到以下指标：

```bash
# 活跃分配数
turn_total_allocations

# 流量统计
turn_traffic_rcvp      # 接收的包数
turn_traffic_sentp     # 发送的包数
turn_traffic_rcvb      # 接收的字节数
turn_traffic_sentb     # 发送的字节数

# 连接统计
turn_total_traffic_peer_connections  # peer 连接数
```

## 🎉 成功标准

您的 Coturn 服务器配置成功的标志：

### WebRTC 测试：
- ✅ 可以获取到 ICE 候选
- ✅ 看到 `srflx`（STUN）或 `relay`（TURN）类型
- ✅ 测试状态显示为 "Done"

### Prometheus 监控：
- ✅ 可以访问 `http://14.103.165.199:9641/metrics`
- ✅ 看到指标数据（即使值为 0 也正常）
- ✅ 数据格式为 `# TYPE` 和 `# HELP` 开头的 Prometheus 格式

### 命令行测试：
- ✅ `telnet 14.103.165.199 3478` 能连接
- ✅ `turnutils_stunclient` 返回公网 IP
- ✅ `systemctl status coturn` 显示 active (running)

## 📚 参考资源

- [Coturn 官方文档](https://github.com/coturn/coturn)
- [WebRTC 示例](https://webrtc.github.io/samples/)
- [Prometheus 官方文档](https://prometheus.io/docs/)
- [RFC 5766 - TURN](https://tools.ietf.org/html/rfc5766)
- [RFC 5389 - STUN](https://tools.ietf.org/html/rfc5389)

## 🆘 需要帮助？

如果仍然无法正常工作，请提供以下信息：

```bash
# 1. 服务状态
sudo systemctl status coturn

# 2. 最近的日志
sudo journalctl -u coturn -n 50

# 3. 端口监听
sudo netstat -tulnp | grep turnserver

# 4. 防火墙状态
sudo ufw status

# 5. 配置文件关键部分
grep -E "listening-port|external-ip|user=|realm=" /etc/turnserver.conf | grep -v "^#"
```
