#!/bin/bash

# FreeSwitch 配置检查和修复脚本
# 针对 rude-rejection 问题的完整解决方案

SERVER="14.103.165.199"
USERNAME="root"

echo "=== FreeSwitch ESL 配置修复脚本 ==="
echo "目标服务器: $SERVER"
echo "时间: $(date)"
echo ""

echo "步骤1: 查找 FreeSwitch 配置文件..."
echo "常见配置文件位置:"
echo "  - /etc/freeswitch/autoload_configs/event_socket.conf.xml"
echo "  - /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml"
echo "  - /opt/freeswitch/conf/autoload_configs/event_socket.conf.xml"
echo ""

echo "步骤2: 生成修复用的配置文件..."

# 生成正确的 event_socket.conf.xml
cat > event_socket_fixed.conf.xml << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- 完全移除ACL限制以允许所有连接 -->
    <!-- <param name="apply-inbound-acl" value="loopback.auto"/> -->
    <!-- <param name="apply-inbound-acl" value="lan"/> -->
    
    <!--<param name="stop-on-bind-error" value="true"/>-->
  </settings>
</configuration>
EOF

echo "✅ 已生成修复配置文件: event_socket_fixed.conf.xml"
echo ""

# 生成更新的 ACL 配置
cat > acl_fixed.conf.xml << 'EOF'
<configuration name="acl.conf" description="Network Lists">
  <network-lists>
    <!-- 允许所有IP连接的ACL (用于测试) -->
    <list name="bytedesk_allowed" default="allow">
      <!-- 允许所有连接 -->
      <node type="allow" cidr="0.0.0.0/0"/>
      <node type="allow" cidr="::/0"/>
    </list>

    <!-- 保留原有的LAN配置 -->
    <list name="lan" default="allow">
      <node type="deny" cidr="192.168.42.0/24"/>
      <node type="allow" cidr="192.168.42.42/32"/>
    </list>

    <list name="domains" default="deny">
      <node type="allow" domain="$${domain}"/>
    </list>
  </network-lists>
</configuration>
EOF

echo "✅ 已生成修复ACL配置文件: acl_fixed.conf.xml"
echo ""

echo "步骤3: 手动修复指令 (需要在服务器上执行)..."
echo ""
echo "请在服务器 $SERVER 上执行以下命令:"
echo ""
echo "# 1. 备份原配置"
echo "sudo cp /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml.backup.$(date +%Y%m%d)"
echo ""
echo "# 2. 查找实际配置文件位置"
echo "sudo find /etc /usr/local /opt -name 'event_socket.conf.xml' -type f 2>/dev/null"
echo ""
echo "# 3. 临时移除ACL限制的配置内容:"
echo "cat > /tmp/event_socket_temp.xml << 'INNER_EOF'"
echo "<configuration name=\"event_socket.conf\" description=\"Socket Client\">"
echo "  <settings>"
echo "    <param name=\"nat-map\" value=\"false\"/>"
echo "    <param name=\"listen-ip\" value=\"0.0.0.0\"/>"
echo "    <param name=\"listen-port\" value=\"8021\"/>"
echo "    <param name=\"password\" value=\"bytedesk123\"/>"
echo "  </settings>"
echo "</configuration>"
echo "INNER_EOF"
echo ""
echo "# 4. 覆盖配置文件 (请根据实际路径调整)"
echo "sudo cp /tmp/event_socket_temp.xml /usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml"
echo ""
echo "# 5. 重新加载配置"
echo "sudo fs_cli -x 'reload mod_event_socket'"
echo ""
echo "# 6. 如果重新加载失败，重启FreeSwitch"
echo "sudo systemctl restart freeswitch"
echo "# 或者"
echo "sudo /etc/init.d/freeswitch restart"
echo ""
echo "# 7. 验证配置"
echo "sudo fs_cli -x 'show modules' | grep event_socket"
echo ""
echo "# 8. 测试连接"
echo "telnet localhost 8021"
echo ""

echo "步骤4: 验证修复..."
echo "请在执行上述命令后，运行以下测试:"
echo ""
echo "# 从服务器本地测试"
echo "telnet localhost 8021"
echo ""
echo "# 从客户端测试"
echo "telnet $SERVER 8021"
echo ""

echo "步骤5: 如果问题仍然存在..."
echo "1. 检查FreeSwitch日志:"
echo "   tail -f /usr/local/freeswitch/log/freeswitch.log"
echo ""
echo "2. 检查是否有多个FreeSwitch实例:"
echo "   ps aux | grep freeswitch"
echo ""
echo "3. 检查端口监听状态:"
echo "   netstat -tlnp | grep 8021"
echo ""
echo "4. 检查防火墙规则:"
echo "   iptables -L -n | grep 8021"
echo ""

echo "=== 脚本完成 ==="
echo "注意: 此配置移除了所有ACL限制，仅用于测试。"
echo "生产环境请重新添加适当的IP限制。"
