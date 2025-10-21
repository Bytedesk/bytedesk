# Kamailio

专业的 SIP 代理，处理 SIP 的 Via/Record-Route、rport、NAT 以及会话路由
使用 Nginx 继续处理 HTTP/WS/WSS 反向代理

## 架构说明

- **Kamailio**: 专业 SIP 代理服务器，负责 SIP 信令路由、NAT 穿透、会话管理
- **Nginx**: 反向代理，负责 HTTP/WebSocket(WS/WSS) 的代理和 TLS 终结
- **FreeSWITCH**: 后端媒体服务器，处理实际的语音/视频通话

## 安装

### Ubuntu/Debian 系统安装步骤

#### 1. 添加 Kamailio 官方仓库

```bash
# 更新系统包列表
sudo apt update

# 安装必要的工具
sudo apt install -y gnupg2 wget

# 添加 Kamailio 官方 GPG 密钥
wget -O- https://deb.kamailio.org/kamailiodebkey.gpg | sudo apt-key add -

# 添加 Kamailio 仓库（以 Ubuntu 22.04 为例）
# 注意：根据你的系统版本选择对应的代号（jammy/focal/bullseye等）
echo "deb http://deb.kamailio.org/kamailio57 jammy main" | sudo tee /etc/apt/sources.list.d/kamailio.list

# 更新包列表
sudo apt update
```

**常见系统代号：**
- Ubuntu 22.04 LTS: `jammy`
- Ubuntu 20.04 LTS: `focal`
- Debian 11: `bullseye`
- Debian 10: `buster`

#### 2. 安装 Kamailio 及必要模块

```bash
# 安装 Kamailio 核心包
sudo apt install -y kamailio

# 安装 WebSocket 支持模块
sudo apt install -y kamailio-websocket-modules

# 安装 TLS/SSL 支持模块
sudo apt install -y kamailio-tls-modules

# 安装 JSON 和 HTTP 模块（可选，用于 API 集成）
sudo apt install -y kamailio-json-modules kamailio-http-modules

# 安装 MySQL/PostgreSQL 模块（如需数据库支持）
sudo apt install -y kamailio-mysql-modules
# 或
# sudo apt install -y kamailio-postgres-modules

# 安装额外的实用模块
sudo apt install -y kamailio-extra-modules
```

#### 3. 验证安装

```bash
# 查看 Kamailio 版本
kamailio -v

# 检查已安装的模块
ls /usr/lib/x86_64-linux-gnu/kamailio/modules/

# 检查服务状态
sudo systemctl status kamailio
```

#### 4. 启用开机自启

```bash
# 启用 Kamailio 开机自启
sudo systemctl enable kamailio

# 启动 Kamailio
sudo systemctl start kamailio

# 重启 Kamailio
sudo systemctl restart kamailio

# 停止 Kamailio
sudo systemctl stop kamailio
```

## 配置

### 配置文件位置

- **主配置文件**: `/etc/kamailio/kamailio.cfg`
- **本地配置**: `/etc/kamailio/kamailio-local.cfg` (可选，用于本地化配置)
- **TLS 配置**: `/etc/kamailio/tls.cfg`
- **默认配置**: `/etc/default/kamailio`

### 1. 基础配置 (`/etc/default/kamailio`)

```bash
# 编辑默认配置
sudo nano /etc/default/kamailio
```

```bash
# 启用 Kamailio
RUN_KAMAILIO=yes

# 配置文件路径
CFGFILE=/etc/kamailio/kamailio.cfg

# 运行用户
USER=kamailio
GROUP=kamailio

# SIP UDP 端口
SIP_UDP_PORT=5060

# SIP TCP 端口
SIP_TCP_PORT=5060

# SIP TLS 端口
SIP_TLS_PORT=5061

# 监听地址（留空表示监听所有接口）
SIP_LISTEN_ADDR=

# 日志级别 (0=ALERT, 1=CRIT, 2=ERR, 3=WARN, 4=NOTICE, 5=INFO, 6=DEBUG)
LOG_LEVEL=3

# 内存大小
MEMORY=64
```

### 2. 主配置文件 (`/etc/kamailio/kamailio.cfg`)

创建或编辑主配置文件：

```bash
sudo cp /etc/kamailio/kamailio.cfg /etc/kamailio/kamailio.cfg.bak
sudo nano /etc/kamailio/kamailio.cfg
```

```cfg
#!KAMAILIO

####### Global Parameters #########

### 日志配置
debug=3
log_stderror=no
memdbg=5
memlog=5
log_facility=LOG_LOCAL0
fork=yes
children=4

### 网络配置
# 替换为你的实际公网 IP
#!define PUBLICIP "14.103.165.199"
#!substdef "!PUBLICIP!14.103.165.199!g"

# 本地 IP（内网 IP，如果没有 NAT 则与 PUBLICIP 相同）
#!define LOCALIP "127.0.0.1"

# FreeSWITCH 地址
#!define FREESWITCH_IP "14.103.165.199"
#!define FREESWITCH_PORT 5080

### 监听配置
# SIP UDP
listen=udp:0.0.0.0:5060

# SIP TCP
listen=tcp:0.0.0.0:5060

# SIP TLS (如需要)
# listen=tls:0.0.0.0:5061

### 协议配置
disable_tcp=no
enable_tls=no
tcp_connection_lifetime=3605
tcp_accept_no_cl=yes
tcp_rd_buf_size=16384

### DNS 配置
dns=no
rev_dns=no
dns_try_ipv6=no

### 别名配置
# 替换为你的实际域名
alias="sip.weiyuai.cn"
alias="weiyuai.cn"

####### 模块加载 #########

loadmodule "jsonrpcs.so"
loadmodule "kex.so"
loadmodule "corex.so"
loadmodule "tm.so"
loadmodule "tmx.so"
loadmodule "sl.so"
loadmodule "rr.so"
loadmodule "pv.so"
loadmodule "maxfwd.so"
loadmodule "textops.so"
loadmodule "siputils.so"
loadmodule "xlog.so"
loadmodule "sanity.so"
loadmodule "ctl.so"
loadmodule "cfg_rpc.so"
loadmodule "nathelper.so"
loadmodule "rtpproxy.so"

####### 模块参数 #########

### jsonrpcs 参数
modparam("jsonrpcs", "pretty_format", 1)

### tm 参数
modparam("tm", "failure_reply_mode", 3)
modparam("tm", "fr_timer", 30000)
modparam("tm", "fr_inv_timer", 120000)

### rr 参数
modparam("rr", "enable_full_lr", 1)
modparam("rr", "append_fromtag", 1)

### nathelper 参数
modparam("nathelper", "natping_interval", 30)
modparam("nathelper", "ping_nated_only", 1)
modparam("nathelper", "sipping_bflag", 7)
modparam("nathelper", "sipping_from", "sip:pinger@weiyuai.cn")

### rtpproxy 参数 (如果使用 RTPProxy)
# modparam("rtpproxy", "rtpproxy_sock", "udp:127.0.0.1:22222")

####### 路由逻辑 #########

# 主请求路由
request_route {
    # 基本的防洪和安全检查
    route(REQINIT);

    # NAT 检测
    route(NATDETECT);

    # 处理 OPTIONS (心跳检测)
    if (is_method("OPTIONS")) {
        sl_send_reply("200", "OK");
        exit;
    }

    # 记录路由处理
    if (is_method("INVITE|SUBSCRIBE")) {
        record_route();
    }

    # 转发到 FreeSWITCH
    route(FREESWITCH);
}

# 初始化检查路由
route[REQINIT] {
    # 检查最大转发次数
    if (!mf_process_maxfwd_header("10")) {
        sl_send_reply("483", "Too Many Hops");
        exit;
    }

    # 基本的 SIP 消息合法性检查
    if (!sanity_check("1511", "7")) {
        xlog("L_WARN", "Malformed SIP message from $si:$sp\n");
        exit;
    }
}

# NAT 检测路由
route[NATDETECT] {
    force_rport();
    
    if (nat_uac_test("19")) {
        if (is_method("REGISTER")) {
            fix_nated_register();
        } else {
            if (is_first_hop()) {
                set_contact_alias();
            }
        }
        setflag(5); # NAT flag
    }
}

# FreeSWITCH 转发路由
route[FREESWITCH] {
    # 设置目标为 FreeSWITCH
    $du = "sip:" + FREESWITCH_IP + ":" + FREESWITCH_PORT;
    
    # 添加日志
    xlog("L_INFO", "Forwarding $rm from $fu to FreeSWITCH: $du\n");
    
    # NAT 处理
    if (isflagset(5)) {
        if (is_method("INVITE")) {
            # 如果使用 RTPProxy，取消注释下面这行
            # rtpproxy_offer();
        }
    }
    
    # 转发请求
    if (!t_relay()) {
        sl_reply_error();
    }
    exit;
}

# 应答路由
onreply_route {
    # NAT 处理
    if (isflagset(5)) {
        if (is_method("INVITE")) {
            # 如果使用 RTPProxy，取消注释下面这行
            # rtpproxy_answer();
        }
        handle_rport_n_received();
    }
    
    xlog("L_INFO", "Reply $rs $rr from $si:$sp\n");
}

# 失败路由
failure_route {
    if (t_is_canceled()) {
        exit;
    }
    
    xlog("L_INFO", "Failure route triggered: $rm from $fu\n");
}
```

### 3. TLS 配置（可选，如需支持 SIPS）

编辑 `/etc/kamailio/tls.cfg`:

```bash
sudo nano /etc/kamailio/tls.cfg
```

```cfg
[server:default]
method = TLSv1.2+
verify_certificate = no
require_certificate = no
private_key = /etc/letsencrypt/live/weiyuai.cn/privkey.pem
certificate = /etc/letsencrypt/live/weiyuai.cn/fullchain.pem
```

### 4. 配置检查和启动

```bash
# 检查配置文件语法
sudo kamailio -c -f /etc/kamailio/kamailio.cfg

# 如果没有错误，重启服务
sudo systemctl restart kamailio

# 查看日志
sudo tail -f /var/log/syslog | grep kamailio

# 或者使用 journalctl
sudo journalctl -u kamailio -f
```

### 5. 防火墙配置

```bash
# 开放 SIP 端口
sudo ufw allow 5060/udp comment 'Kamailio SIP UDP'
sudo ufw allow 5060/tcp comment 'Kamailio SIP TCP'
sudo ufw allow 5061/tcp comment 'Kamailio SIP TLS'

# 如果使用 RTPProxy，还需要开放 RTP 端口范围
# sudo ufw allow 10000:20000/udp comment 'RTP Media'

# 重载防火墙
sudo ufw reload

# 查看防火墙状态
sudo ufw status
```

## 与 Nginx 的集成架构

### 请求流程

1. **WebSocket 客户端 (浏览器)** → `wss://sip.weiyuai.cn/ws`
2. **Nginx (443)** → TLS 终结，转发 WebSocket → **FreeSWITCH (7443/5066)**
3. **FreeSWITCH** ← SIP 信令 → **Kamailio (5060)** → NAT 穿透、路由
4. **Kamailio** ← SIP 信令 → **其他 SIP 端点** (外部 SIP 客户端、运营商等)

### 工作原理

- **WebSocket 流量**: Nginx 处理 WS/WSS，直接转发到 FreeSWITCH
- **SIP 信令流量**: Kamailio 处理 UDP/TCP/TLS SIP，负责路由和 NAT
- **媒体流量 (RTP)**: 直接在 FreeSWITCH 和客户端之间传输，或通过 TURN/coturn

## 测试和验证

### 1. 测试 Kamailio SIP 端口

```bash
# 使用 netcat 测试 UDP 端口
nc -u -v 14.103.165.199 5060

# 测试 TCP 端口
nc -v 14.103.165.199 5060

# 使用 sipsak 工具测试（需要安装）
sudo apt install -y sipsak
sipsak -v -s sip:14.103.165.199:5060
```

### 2. 查看 Kamailio 统计信息

```bash
# 使用 kamctl 工具
sudo kamctl stats

# 查看活动连接
sudo kamctl fifo get_statistics all

# 查看内存使用
sudo kamctl fifo get_statistics shmem:
```

### 3. 调试模式

```bash
# 临时提高日志级别到 DEBUG
sudo kamcmd cfg.set_now_int core debug 6

# 恢复默认级别
sudo kamcmd cfg.set_now_int core debug 3
```

## 常见问题

### 1. Kamailio 无法启动

- 检查配置文件语法: `kamailio -c`
- 查看详细错误日志: `journalctl -u kamailio -n 100`
- 确认端口未被占用: `netstat -tuln | grep 5060`

### 2. NAT 穿透问题

- 确保 `nathelper` 模块已加载
- 配置正确的公网 IP (`PUBLICIP`)
- 考虑使用 RTPProxy 或 TURN 服务器处理 RTP

### 3. FreeSWITCH 连接问题

- 检查 FreeSWITCH 的 ACL 配置，允许 Kamailio IP
- 确认 FreeSWITCH 监听正确的端口 (5080)
- 查看 FreeSWITCH 日志: `fs_cli -x "console loglevel debug"`

## 参考资源

- [Kamailio 官方文档](https://www.kamailio.org/docs/)
- [Kamailio Wiki](https://www.kamailio.org/wiki/)
- [FreeSWITCH + Kamailio 集成指南](https://freeswitch.org/confluence/display/FREESWITCH/Kamailio)
- [NAT 穿透最佳实践](https://www.kamailio.org/docs/modules/stable/modules/nathelper.html)
