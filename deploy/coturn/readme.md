---
sidebar_label: Coturn
sidebar_position: 15
---

# Coturn

## STUN/TURN 服务安装

```bash
# https://github.com/coturn/coturn
apt install coturn
# 查看日志
turnserver --log-file stdout
# 启动运行
sudo systemctl start coturn
# 或
turnserver
# turnserver -c /etc/turnserver.conf
# 查看运行状态
sudo systemctl status coturn
# 停止运行
sudo systemctl stop coturn
# 重启运行
sudo systemctl restart coturn
# 
netstat -anp|grep turnserver
```

## 防火墙开放端口号

```bash
ufw allow 3478
# ufw status
# ufw disable
# 3478 TCP
```

## 修改 /etc/turnserver.conf 配置文件

```bash
# 备份配置文件
cp /etc/turnserver.conf /etc/turnserver.conf.original
# 下面的四行原本都是注释掉的，去掉前面的 #， 添加参数：
# 这个端口号是默认的
listening-port=3478    
# 此ip为服务器公网ip，替换为自己的  
external-ip=47.**.**.81
# 这个是用户名:密码，替换为自己的用户名和密码
user=username1:password1
user=username2:password2
# 这里填写域名，替换为自己的
realm=coturn.weiyuai.cn
```

## 修改 /etc/default/coturn 配置文件

```bash
# 去掉前面的注释 #
TURNSERVER_ENABLED=1
```

## 重启 turnserver

```bash
sudo systemctl restart coturn
```

## 测试

### 重要说明

⚠️ **Coturn 是 STUN/TURN 服务器，使用 UDP/TCP 协议，不是 HTTP 服务器！**

- ❌ 不能通过浏览器直接访问 `http://14.103.165.199:3478`
- ❌ Nginx 无法直接代理 STUN/TURN 协议到 HTTPS
- ✅ 需要使用专门的 WebRTC 测试工具

### 测试方法

#### 方法 1: 使用在线 WebRTC 测试工具（推荐）

访问：[WebRTC Trickle ICE 测试](https://webrtc.github.io/samples/src/content/peerconnection/trickle-ice/)

填写信息：
```
STUN URI: stun:14.103.165.199:3478
或
TURN URI: turn:14.103.165.199:3478
Username: username1
Password: password1
```

点击 "Gather candidates" 按钮，成功会显示 `srflx` 或 `relay` 类型的候选地址。

![WebRTC 测试](/img/deploy/webrtc/coturn_turn_stun_test.png)

#### 方法 2: 使用本地测试页面

在浏览器中打开：`file:///path/to/test_coturn.html`

或者将 `test_coturn.html` 部署到 Web 服务器，通过 HTTPS 访问。

#### 方法 3: 启用 Prometheus 监控

编辑 `/etc/turnserver.conf`，添加：
```bash
prometheus
```

重启服务：
```bash
sudo systemctl restart coturn
```

开放端口：
```bash
sudo ufw allow 9641
```

访问监控数据：`http://14.103.165.199:9641/metrics`

#### 方法 4: 使用命令行工具

```bash
# 测试 STUN
turnutils_stunclient 14.103.165.199

# 测试 TURN
turnutils_uclient -v -u username1 -w password1 14.103.165.199

# 测试端口连通性
telnet 14.103.165.199 3478
```

### 查看运行状态

```bash
# 查看服务状态
sudo systemctl status coturn

# 实时日志
sudo journalctl -u coturn -f

# 查看端口监听
sudo netstat -tulnp | grep turnserver
```

### 详细测试指南

参见 [test_coturn.md](./test_coturn.md) 获取完整测试说明。

## 参考资料

- [Samples](https://webrtc.github.io/samples/)
- [Github Samples](https://github.com/webrtc/samples)
- [WebRTC API](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)
- [Janus Gateway](https://github.com/meetecho/janus-gateway)
