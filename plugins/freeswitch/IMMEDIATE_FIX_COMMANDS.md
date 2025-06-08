# 🚨 FreeSwitch ESL 立即修复指令

## 当前状态确认 ✅
- 网络连通性：正常
- 端口8021：可访问
- ESL响应：仍然是 "text/rude-rejection"

## 🔧 需要在服务器上执行的修复命令

**请在FreeSwitch服务器 (14.103.165.199) 上以root权限执行以下命令：**

### 1. 快速修复（一行命令）

```bash
# 一键修复ESL配置
cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml.backup.$(date +%Y%m%d) && cat > /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
  </settings>
</configuration>
EOF
systemctl restart freeswitch
```

### 2. 分步执行（如果需要详细控制）

```bash
# 步骤1: 备份原配置
cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml.backup

# 步骤2: 创建新配置
cat > /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
  </settings>
</configuration>
EOF

# 步骤3: 重启FreeSwitch
systemctl restart freeswitch
```

### 3. 验证修复

```bash
# 检查服务状态
systemctl status freeswitch

# 检查端口监听
netstat -tlnp | grep :8021

# 测试ESL连接
echo "" | nc localhost 8021
```

**预期结果：应该看到 "Content-Type: auth/request" 而不是 "rude-rejection"**

## 🔍 如果还有问题

1. **检查配置文件位置：**
   ```bash
   find /etc /usr/local /opt /var -name "event_socket.conf.xml" -type f 2>/dev/null
   ```

2. **查看FreeSwitch日志：**
   ```bash
   tail -20 /usr/local/freeswitch/log/freeswitch.log
   ```

3. **检查其他可能的配置文件：**
   ```bash
   grep -r "apply-inbound-acl" /usr/local/freeswitch/conf/ 2>/dev/null
   ```

## 📞 联系信息

如果需要技术支持，请提供以下信息：
- 执行命令后的输出结果
- FreeSwitch日志的最后20行
- 配置文件的实际位置

---

**⚠️ 注意：这个配置移除了所有ACL限制，仅用于解决连接问题。生产环境建议后续添加IP白名单。**
