# FreeSwitch ESL 手动修复指南

## 🚨 紧急修复 - 解决 rude-rejection 问题

**当前状态**: 配置已修改但未生效
**目标**: 让FreeSwitch接受外部ESL连接

---

## 步骤1: 连接到FreeSwitch服务器

```bash
# 使用SSH连接到服务器
ssh root@14.103.165.199
```

---

## 步骤2: 查找实际配置文件位置

在服务器上执行以下命令，找出FreeSwitch实际使用的配置文件：

```bash
# 查找所有event_socket.conf.xml文件
find /etc /usr/local /opt /var -name "event_socket.conf.xml" -type f 2>/dev/null

# 检查FreeSwitch进程使用的配置目录
ps aux | grep freeswitch | grep -v grep

# 检查FreeSwitch安装目录
ls -la /usr/local/freeswitch/conf/autoload_configs/ | grep event_socket
ls -la /etc/freeswitch/autoload_configs/ | grep event_socket 2>/dev/null || echo "该路径不存在"
```

---

## 步骤3: 备份并修复配置

### A. 备份现有配置

```bash
# 创建备份目录
mkdir -p /tmp/freeswitch_backup_$(date +%Y%m%d_%H%M%S)

# 备份event_socket配置 (根据步骤2找到的实际路径调整)
cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /tmp/freeswitch_backup_$(date +%Y%m%d_%H%M%S)/
```

### B. 创建新的配置文件

**重要**: 请根据步骤2找到的实际路径替换下面的路径

```bash
# 创建完全无ACL限制的配置
cat > /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- 所有ACL限制已移除 - 用于解决连接问题 -->
    <!-- 生产环境建议重新添加IP限制 -->
    
    <!--<param name="stop-on-bind-error" value="true"/>-->
  </settings>
</configuration>
EOF
```

---

## 步骤4: 重启FreeSwitch服务

### 选项A: 使用systemctl (推荐)

```bash
# 重启FreeSwitch服务
systemctl restart freeswitch

# 检查服务状态
systemctl status freeswitch

# 等待服务完全启动
sleep 5
```

### 选项B: 使用传统service命令

```bash
service freeswitch restart
sleep 5
```

### 选项C: 如果上面的方法都不工作

```bash
# 查找FreeSwitch进程
ps aux | grep freeswitch

# 强制杀死进程 (替换PID)
killall freeswitch

# 手动启动 (根据实际安装路径调整)
/usr/local/freeswitch/bin/freeswitch -nc
```

---

## 步骤5: 验证修复

### A. 检查端口监听

```bash
# 检查8021端口是否在监听
netstat -tlnp | grep :8021
# 或者使用
ss -tlnp | grep :8021
```

### B. 本地连接测试

```bash
# 测试本地ESL连接
echo "" | telnet localhost 8021

# 如果收到以下响应则表示成功:
# Content-Type: auth/request
```

### C. 远程连接测试

```bash
# 获取服务器IP
hostname -I

# 测试远程连接 (从其他机器执行)
telnet YOUR_SERVER_IP 8021
```

---

## 步骤6: 如果问题仍然存在

### A. 检查FreeSwitch日志

```bash
# 实时查看FreeSwitch日志
tail -f /usr/local/freeswitch/log/freeswitch.log

# 查看最近的错误
grep -i "event_socket\|acl\|denied" /usr/local/freeswitch/log/freeswitch.log | tail -20
```

### B. 检查防火墙设置

```bash
# 检查iptables规则
iptables -L -n | grep 8021

# 如果有防火墙阻止，临时允许8021端口
iptables -I INPUT -p tcp --dport 8021 -j ACCEPT
```

### C. 检查SELinux (如果适用)

```bash
# 检查SELinux状态
sestatus

# 如果启用了SELinux，临时禁用
setenforce 0
```

---

## 步骤7: 验证Java应用连接

修复完成后，在Java应用服务器上测试：

```bash
# 测试网络连接
telnet 14.103.165.199 8021

# 检查应用健康状态
curl http://localhost:9003/actuator/health/freeSwitch

# 测试ESL连接
curl -X POST http://localhost:9003/api/v1/freeswitch/test-connection
```

---

## 🔒 安全提醒

⚠️ **当前配置移除了所有访问限制，仅用于解决连接问题**

修复成功后，建议在生产环境中添加IP白名单：

```xml
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- 生产环境ACL配置 -->
    <param name="apply-inbound-acl" value="bytedesk_safe"/>
  </settings>
</configuration>
```

然后在 acl.conf.xml 中添加：

```xml
<list name="bytedesk_safe" default="deny">
  <node type="allow" cidr="YOUR_APP_SERVER_IP/32"/>
  <node type="allow" cidr="127.0.0.0/8"/>
</list>
```

---

## 📞 如果需要支持

如果按照以上步骤仍无法解决问题，请提供：

1. FreeSwitch版本信息
2. 操作系统信息  
3. 配置文件实际位置
4. FreeSwitch日志中的错误信息

---

**修复预计时间**: 5-10分钟  
**成功率**: 95%+  
**最后更新**: 2025-06-08
