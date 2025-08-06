<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-05 07:53:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-05 13:52:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# Janus WebRTC Server

- [演示](https://janus.weiyuai.cn/demos/demos/admin.html)

## 配置文件

```bash
# 修改 cd /opt/janus/etc/janus/janus.transport.http.jcfg
general: {
    ip = "0.0.0.0"
}
admin: {
    admin_http = true
    admin_ip = "0.0.0.0"
}
```

## 前台运行

```bash
# 安装路径
cd /opt/janus
# 配置文件
./etc/janus/janus.jcfg
# 查看帮助
./bin/janus --help
# 前台启动
./bin/janus
# 查看端口占用情况
netstat -ntlp | grep janus
```

## 后台运行

### 方法一：使用脚本（推荐）

```bash
# 给脚本添加执行权限
chmod +x deploy/janus/start.sh
chmod +x deploy/janus/stop.sh
chmod +x deploy/janus/status.sh

# 启动服务
./deploy/janus/start.sh

# 停止服务
./deploy/janus/stop.sh

# 查看状态
./deploy/janus/status.sh
```

### 方法二：使用 systemd 服务

```bash
# 复制服务文件到系统目录
sudo cp deploy/janus/janus.service /etc/systemd/system/

# 重新加载 systemd 配置
sudo systemctl daemon-reload

# 启用服务（开机自启）
sudo systemctl enable janus

# 启动服务
sudo systemctl start janus

# 停止服务
sudo systemctl stop janus

# 重启服务
sudo systemctl restart janus

# 查看状态
sudo systemctl status janus

# 查看日志
sudo journalctl -u janus -f
```

### 方法三：使用 nohup 命令

```bash
# 后台启动并重定向输出
nohup /opt/janus/bin/janus > /opt/janus/logs/janus.log 2>&1 &

# 查看进程
ps aux | grep janus

# 停止进程
pkill -f janus
```
