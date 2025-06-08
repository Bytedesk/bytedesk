# FreeSwitch ESL "rude-rejection" 问题诊断报告

## 问题摘要

**问题现象**: 即使修改了FreeSwitch配置文件后，Java应用连接ESL时仍然收到 `text/rude-rejection` 错误。

**关键发现**: 
- ✅ 网络连接正常 (端口8021可达)
- ❌ FreeSwitch仍返回 "Access Denied, go away" 消息
- 🔍 配置修改可能未生效

## 根本原因分析

### 1. 配置文件位置问题
FreeSwitch可能从不同的路径读取配置文件：
- `/etc/freeswitch/autoload_configs/`
- `/usr/local/freeswitch/conf/autoload_configs/`
- `/opt/freeswitch/conf/autoload_configs/`

### 2. 配置重新加载问题
修改配置后需要：
- 重新加载 `mod_event_socket` 模块，或
- 完全重启FreeSwitch服务

### 3. 可能的配置覆盖
- 其他配置文件可能覆盖了ACL设置
- 系统级防火墙可能在应用层面阻止连接

## 立即修复方案

### 方案A: 使用自动修复脚本 (推荐)

1. **将修复脚本上传到服务器**:
   ```bash
   scp freeswitch_acl_fix.sh root@14.103.165.199:/tmp/
   ```

2. **在服务器上执行修复**:
   ```bash
   ssh root@14.103.165.199
   chmod +x /tmp/freeswitch_acl_fix.sh
   sudo /tmp/freeswitch_acl_fix.sh
   ```

### 方案B: 手动修复步骤

在FreeSwitch服务器 (14.103.165.199) 上执行：

```bash
# 1. 查找实际配置文件位置
sudo find /etc /usr/local /opt -name "event_socket.conf.xml" -type f 2>/dev/null

# 2. 备份原配置
sudo cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /tmp/

# 3. 创建无ACL限制的配置
sudo tee /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml > /dev/null << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
  </settings>
</configuration>
EOF

# 4. 重启FreeSwitch服务
sudo systemctl restart freeswitch

# 5. 等待服务启动
sleep 5

# 6. 验证修复
telnet localhost 8021
```

### 方案C: 应急SSH隧道方案

如果服务器配置无法修改，可以使用SSH隧道：

```bash
# 在本地执行 (Mac终端)
ssh -L 8021:localhost:8021 root@14.103.165.199

# 修改Java应用配置连接到本地
bytedesk.freeswitch.server=127.0.0.1
bytedesk.freeswitch.esl-port=8021
```

## 验证修复

修复后使用以下方法验证：

### 1. 网络层测试
```bash
telnet 14.103.165.199 8021
```
应该收到 `auth/request` 而不是 `rude-rejection`

### 2. Java应用测试
```bash
# 检查健康状态
curl http://localhost:9003/actuator/health/freeSwitch

# 测试连接
curl -X POST http://localhost:9003/api/v1/freeswitch/test-connection
```

### 3. FreeSwitch日志
```bash
# 在服务器上检查日志
tail -f /usr/local/freeswitch/log/freeswitch.log
```

## 安全注意事项

⚠️ **临时解决方案警告**: 
- 当前修复移除了所有ACL限制
- 生产环境应重新添加IP白名单
- 建议使用防火墙进行额外保护

### 生产环境ACL配置示例
```xml
<configuration name="acl.conf" description="Network Lists">
  <network-lists>
    <list name="bytedesk_allowed" default="deny">
      <!-- 只允许应用服务器IP -->
      <node type="allow" cidr="YOUR_APP_SERVER_IP/32"/>
      <!-- 允许本地连接 -->
      <node type="allow" cidr="127.0.0.0/8"/>
    </list>
  </network-lists>
</configuration>
```

## 后续监控

1. **设置健康检查**: 定期检查ESL连接状态
2. **日志监控**: 监控FreeSwitch连接日志
3. **告警配置**: 连接失败时发送告警

---

**生成时间**: 2025-06-08 14:42  
**问题状态**: 🔴 待修复  
**优先级**: 高  
**预计修复时间**: 5-10分钟
