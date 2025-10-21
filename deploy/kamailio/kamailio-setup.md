# Kamailio 部署与配置指南（反代 FreeSWITCH 的 5060/UDP）

本文档指导你将 Nginx 的 5060/UDP 转发切换为由 Kamailio 负责的专业 SIP 代理，以解决 UDP/NAT 回包路径问题。Nginx 继续用于 HTTP/WS/WSS（例如 WSS 到 FreeSWITCH）。

适用环境
- OS: Ubuntu 24.04（命令对 Debian/Ubuntu 通用）
- 公网 IP（Kamailio）：`124.220.58.234`
- FreeSWITCH 公网 IP：`14.103.165.199`
- SIP 域名：`sip.weiyuai.cn`
- 协议/端口：SIP/UDP 5060

> 若你的实际 IP/域名不同，请将文档中的示例值替换为你的真实值。

---

## 1. 释放 Nginx 的 5060/UDP（如果之前由 Nginx 代理）

1) 编辑 `/etc/nginx/nginx.conf`，删除 `stream {}` 内监听 5060/udp 的 server 段。
2) 验证并重载：

```bash
sudo nginx -t && sudo systemctl reload nginx
sudo ss -lunp | grep -E '(:5060\s)' || echo "no udp:5060 listener"
```

---

## 2. 安装 Kamailio 与必要模块

```bash
# 若存在无效的第三方源导致 apt update 失败，先禁用（示例）
# sudo mv /etc/apt/sources.list.d/mysql.list /etc/apt/sources.list.d/mysql.list.disabled || true

sudo apt-get update
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y kamailio kamailio-extra-modules
```

安装完成后，系统会创建 systemd 服务 `kamailio.service`。

---

## 3. 配置 Kamailio（最小可用代理到 FreeSWITCH）

三个关键点：
- Kamailio 监听 0.0.0.0:5060，并 advertise 公网 IP，确保 Via/Record-Route 对外可用
- 转发所有 SIP 请求到 FreeSWITCH：`14.103.165.199:5060`
- 基础 NAT 处理：`force_rport` 与 `fix_nated_contact`

### 3.1 上游列表 `/etc/kamailio/dispatcher.list`

```bash
sudo tee /etc/kamailio/dispatcher.list >/dev/null <<'EOF'
1 sip:14.103.165.199:5060
EOF
```

### 3.2 主配置 `/etc/kamailio/kamailio.cfg`

如下为最小、可直接使用的配置。若该文件已有内容，建议先备份再覆盖。

```bash
sudo tee /etc/kamailio/kamailio.cfg >/dev/null <<'EOF'
#!KAMAILIO

debug=2
log_stderror=no
children=4
enable_sctp=no

listen=udp:0.0.0.0:5060 advertise 124.220.58.234:5060
alias="sip.weiyuai.cn"

mpath="/usr/lib/x86_64-linux-gnu/kamailio/modules/"

# Modules
loadmodule "sl.so"
loadmodule "tm.so"
loadmodule "rr.so"
loadmodule "sanity.so"
loadmodule "textops.so"
loadmodule "siputils.so"
loadmodule "pv.so"
loadmodule "xlog.so"
loadmodule "dispatcher.so"
loadmodule "nathelper.so"

# Module params
modparam("rr", "enable_full_lr", 1)
modparam("rr", "append_fromtag", 0)

modparam("tm", "fr_timer", 30000)
modparam("tm", "fr_inv_timer", 120000)

modparam("sanity", "autodrop", 0)

modparam("dispatcher", "list_file", "/etc/kamailio/dispatcher.list")
modparam("dispatcher", "flags", 2)
modparam("dispatcher", "ds_probing_mode", 1)

modparam("nathelper", "received_avp", "$avp(rcv)")

route[DISPATCH] {
    if(!ds_select_dst(1, 0)) {
        send_reply(500, "No Upstream");
        exit;
    }
    t_on_reply("NAT_REPLY");
    if(!t_relay()) {
        sl_reply_error();
    }
    exit;
}

onreply_route[NAT_REPLY] {
    if(isflagset(5)) {
        fix_nated_contact();
    }
}

request_route {
    if (!sanity_check("1511", "7")) {
        send_reply(400, "Bad Request");
        exit;
    }

    # Basic NAT handling
    force_rport();
    if (nat_uac_test(19)) {
        setflag(5);
        fix_nated_contact();
    }

    # Stay in the signaling path
    record_route();

    route(DISPATCH);
}
EOF
```

> 如 FreeSWITCH 实际对外监听为 `5080/udp`，请将 dispatcher.list 中的端口改为 5080。

---

## 4. 启动与验证

```bash
# 语法检查
sudo kamailio -c -f /etc/kamailio/kamailio.cfg

# 启动服务
sudo systemctl enable --now kamailio
sudo systemctl status --no-pager -l kamailio

# 确认监听 5060/udp
sudo ss -lunp | grep -E '(:5060\s)' || true
```

### 4.1 本机快速测试（OPTIONS）

```bash
printf 'OPTIONS sip:weiyuai.cn SIP/2.0\r\nVia: SIP/2.0/UDP 124.220.58.234:5060;branch=z9hG4bK-9999;rport\r\nMax-Forwards: 70\r\nFrom: <sip:ping@sip.weiyuai.cn>;tag=9999\r\nTo: <sip:ping@sip.weiyuai.cn>\r\nCall-ID: 9999@sip.weiyuai.cn\r\nCSeq: 1 OPTIONS\r\nContact: <sip:ping@124.220.58.234:5060>\r\nContent-Length: 0\r\n\r\n' | nc -u -w2 124.220.58.234 5060 | sed -n '1,80p'
```

如果看到 `SIP/2.0 200 OK` 且 `Record-Route` 中包含 Kamailio（公网/内网地址），说明路径正常。

---

## 5. 需要同时检查的外部条件

- 云安全组/防火墙：
  - Kamailio 机（124.220.58.234）入站放行 UDP/5060
  - FreeSWITCH 机（14.103.165.199）入站允许来自 124.220.58.234 的 UDP/5060
- FreeSWITCH：
  - external_sip_ip 与 external_rtp_ip 设置为 14.103.165.199（公网）
  - 外部 profile 监听端口确认与上游一致（5060 或 5080）
  - ACL 允许 124.220.58.234
- 客户端：
  - 注册到 `sip:用户名@sip.weiyuai.cn:5060`，失败时抓包对照 Kamailio/FreeSWITCH 日志

---

## 6. 常见问题排查

- `config file ok` 但服务起不来：
  - `journalctl -xeu kamailio` 查看错误详细
- 返回 4xx/5xx 或无回包：
  - Kamailio：`journalctl -u kamailio -f`
  - FreeSWITCH 日志：检查是否收到请求、是否 ACL 拒绝
  - 核对安全组/端口是否放通
- Via/Contact 显示内网地址：
  - 确保 Kamailio 配置了 `advertise 公网IP`
  - FreeSWITCH external_sip_ip/external_rtp_ip 设置是否正确

---

## 7. 回滚方案（改回直连 FreeSWITCH）

- 将 DNS A 记录 `sip.weiyuai.cn` 指向 `14.103.165.199`
- 在 14.103.165.199 放行 UDP/5060（或 5080）
- 关闭 Kamailio：`sudo systemctl disable --now kamailio`

---

## 8. 进阶建议（可选）

- 负载均衡/高可用：在 `/etc/kamailio/dispatcher.list` 中加入多台 FreeSWITCH，设置失败转移与探测
- 抗扫描/限速：启用 `pike` 与 `htable`，对异常源做封禁
- 媒体穿透：如遇复杂 NAT，考虑配合 TURN/ICE，或引入 `rtpengine`（或 `rtpproxy`）
- 安全：限制来源网段、TLS/SIPS、对注册/呼叫做鉴权策略

---

完成后，请用外网软电话进行注册与呼叫测试；若需要，我可根据你的现网参数进一步完善配置（例如：5080/udp、多上游、SIP 头策略、限速等）。
