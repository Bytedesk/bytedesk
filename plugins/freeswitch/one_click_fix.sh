#!/bin/bash
# FreeSwitch ESL 一键修复脚本 - 请在FreeSwitch服务器上执行

echo "=== FreeSwitch ESL 一键修复脚本 ==="
echo "开始时间: $(date)"

# 1. 查找配置文件位置
echo "步骤1: 查找配置文件..."
CONFIG_PATHS=$(find /etc /usr/local /opt /var -name "event_socket.conf.xml" -type f 2>/dev/null)
echo "找到的配置文件位置:"
echo "$CONFIG_PATHS"

# 2. 尝试常见路径
COMMON_PATHS=(
    "/usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml"
    "/etc/freeswitch/autoload_configs/event_socket.conf.xml"
    "/opt/freeswitch/conf/autoload_configs/event_socket.conf.xml"
)

CONFIG_FILE=""
for path in "${COMMON_PATHS[@]}"; do
    if [[ -f "$path" ]]; then
        CONFIG_FILE="$path"
        echo "使用配置文件: $CONFIG_FILE"
        break
    fi
done

if [[ -z "$CONFIG_FILE" && -n "$CONFIG_PATHS" ]]; then
    CONFIG_FILE=$(echo "$CONFIG_PATHS" | head -1)
    echo "使用第一个找到的配置文件: $CONFIG_FILE"
fi

if [[ -z "$CONFIG_FILE" ]]; then
    echo "❌ 错误: 未找到event_socket.conf.xml配置文件"
    echo "请手动指定配置文件路径"
    exit 1
fi

# 3. 备份原配置
echo "步骤2: 备份原配置..."
BACKUP_FILE="/tmp/event_socket.conf.xml.backup.$(date +%Y%m%d_%H%M%S)"
cp "$CONFIG_FILE" "$BACKUP_FILE"
echo "✅ 已备份到: $BACKUP_FILE"

# 4. 创建新配置
echo "步骤3: 创建新配置..."
cat > "$CONFIG_FILE" << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- ACL限制已移除 - 解决连接问题 -->
    <!-- <param name="apply-inbound-acl" value="loopback.auto"/> -->
    <!-- <param name="apply-inbound-acl" value="lan"/> -->
    
  </settings>
</configuration>
EOF
echo "✅ 新配置已创建"

# 5. 重启FreeSwitch
echo "步骤4: 重启FreeSwitch服务..."
if command -v systemctl &> /dev/null; then
    systemctl restart freeswitch && echo "✅ systemctl重启成功" || echo "❌ systemctl重启失败"
elif command -v service &> /dev/null; then
    service freeswitch restart && echo "✅ service重启成功" || echo "❌ service重启失败"
else
    echo "⚠️ 无法自动重启，请手动重启FreeSwitch"
fi

# 6. 等待服务启动
echo "步骤5: 等待服务启动..."
sleep 5

# 7. 验证修复
echo "步骤6: 验证修复..."

# 检查端口监听
if netstat -tlnp 2>/dev/null | grep -q ":8021.*LISTEN" || ss -tlnp 2>/dev/null | grep -q ":8021.*LISTEN"; then
    echo "✅ 端口8021正在监听"
else
    echo "❌ 端口8021未在监听"
fi

# 检查进程
if pgrep freeswitch > /dev/null; then
    echo "✅ FreeSwitch进程运行中"
else
    echo "❌ FreeSwitch进程未运行"
fi

# 测试连接
echo "步骤7: 测试ESL连接..."
RESPONSE=$(timeout 3 bash -c "echo '' | nc localhost 8021" 2>/dev/null || echo "连接失败")

if echo "$RESPONSE" | grep -q "auth/request"; then
    echo "✅ ESL连接测试成功 - 收到认证请求"
elif echo "$RESPONSE" | grep -q "rude-rejection"; then
    echo "❌ 仍然收到rude-rejection错误"
    echo "建议: 检查是否有其他配置文件或重启系统"
else
    echo "⚠️ 连接响应: $RESPONSE"
fi

echo ""
echo "=== 修复完成 ==="
echo "请从客户端测试连接: telnet $(hostname -I | awk '{print $1}') 8021"
echo "配置备份位置: $BACKUP_FILE"
echo "如需恢复: cp $BACKUP_FILE $CONFIG_FILE && systemctl restart freeswitch"
