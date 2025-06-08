# FreeSwitch ESL 连接问题修复指南

## 问题描述
Java应用程序连接FreeSwitch ESL时收到 "text/rude-rejection" 和 "Access Denied, go away" 错误。

## 根本原因
FreeSwitch的Event Socket Library (ESL)配置中的访问控制列表(ACL)限制了外部连接。

## 修复方案

### 方案1: 修改ESL配置允许特定IP (推荐)

1. **备份原配置文件**
```bash
cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml.backup
cp /usr/local/freeswitch/conf/autoload_configs/acl.conf.xml /usr/local/freeswitch/conf/autoload_configs/acl.conf.xml.backup
```

2. **修改event_socket.conf.xml**
```xml
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- 使用自定义ACL -->
    <param name="apply-inbound-acl" value="bytedesk_allowed"/>
  </settings>
</configuration>
```

3. **修改acl.conf.xml，添加自定义ACL**
```xml
<list name="bytedesk_allowed" default="deny">
  <!-- 允许本地连接 -->
  <node type="allow" cidr="127.0.0.0/8"/>
  <node type="allow" cidr="::1/128"/>
  
  <!-- 允许局域网连接 -->
  <node type="allow" cidr="192.168.0.0/16"/>
  <node type="allow" cidr="10.0.0.0/8"/>
  <node type="allow" cidr="172.16.0.0/12"/>
  
  <!-- 根据实际需要添加特定IP -->
  <!-- <node type="allow" cidr="your.client.ip.address/32"/> -->
</list>
```

### 方案2: 临时解决方案 - 移除ACL限制 (不推荐用于生产环境)

修改event_socket.conf.xml:
```xml
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- 移除或注释掉ACL限制 -->
    <!-- <param name="apply-inbound-acl" value="loopback.auto"/> -->
    <!-- <param name="apply-inbound-acl" value="lan"/> -->
  </settings>
</configuration>
```

### 方案3: 使用SSH隧道 (最安全)

1. **建立SSH隧道**
```bash
ssh -L 8021:localhost:8021 user@14.103.165.199
```

2. **修改Java应用配置连接到本地**
```properties
bytedesk.freeswitch.server=127.0.0.1
bytedesk.freeswitch.esl-port=8021
```

## 重启FreeSwitch服务

修改配置后需要重启FreeSwitch：

```bash
# 方法1: 重启系统服务
sudo systemctl restart freeswitch

# 方法2: 重新加载配置 (如果FreeSwitch正在运行)
fs_cli -x "reloadxml"

# 方法3: 只重载ESL模块
fs_cli -x "reload mod_event_socket"
```

## 验证修复

1. **测试端口连接**
```bash
telnet 14.103.165.199 8021
```

2. **检查FreeSwitch日志**
```bash
tail -f /usr/local/freeswitch/log/freeswitch.log
```

3. **使用fs_cli测试**
```bash
fs_cli -H 14.103.165.199 -P 8021 -p bytedesk123
```

## 安全建议

1. **使用强密码**: 确保ESL密码足够复杂
2. **限制IP访问**: 只允许需要的IP地址访问
3. **使用防火墙**: 在服务器级别限制8021端口访问
4. **监控日志**: 定期检查连接日志发现异常访问
5. **使用SSL**: 考虑配置ESL over SSL (需要FreeSwitch支持)

## 当前配置状态

- 服务器: 14.103.165.199
- ESL端口: 8021  
- ESL密码: bytedesk123
- 问题: ACL拒绝外部连接

## 相关文件

- ESL配置: `/usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml`
- ACL配置: `/usr/local/freeswitch/conf/autoload_configs/acl.conf.xml`
- 日志文件: `/usr/local/freeswitch/log/freeswitch.log`
- 应用配置: `application-local.properties`
