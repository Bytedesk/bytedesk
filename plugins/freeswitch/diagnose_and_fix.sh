#!/bin/bash

# FreeSwitch 连接诊断和修复助手 (本地执行)
# 提供详细的诊断信息和修复建议

echo "======================================="
echo "  FreeSwitch ESL 连接诊断助手"
echo "======================================="
echo "诊断时间: $(date)"
echo "目标服务器: 14.103.165.199:8021"
echo ""

# 1. 基础网络连通性测试
echo "🔍 步骤1: 基础网络连通性测试"
echo "----------------------------------------"

if ping -c 3 14.103.165.199 > /dev/null 2>&1; then
    echo "✅ 服务器网络可达"
else
    echo "❌ 服务器网络不可达"
    echo "   请检查网络连接和服务器状态"
    exit 1
fi

# 2. 端口连通性测试
echo ""
echo "🔍 步骤2: 端口连通性测试"
echo "----------------------------------------"

if timeout 5 bash -c "</dev/tcp/14.103.165.199/8021" 2>/dev/null; then
    echo "✅ 端口8021可以连接"
else
    echo "❌ 端口8021无法连接"
    echo "   可能原因:"
    echo "   - FreeSwitch服务未运行"
    echo "   - 防火墙阻止了8021端口"
    echo "   - 端口配置错误"
    exit 1
fi

# 3. ESL协议测试
echo ""
echo "🔍 步骤3: ESL协议测试"
echo "----------------------------------------"

RESPONSE=$(timeout 3 bash -c "echo '' | nc 14.103.165.199 8021" 2>/dev/null)

if [[ -n "$RESPONSE" ]]; then
    echo "收到FreeSwitch响应:"
    echo "$RESPONSE"
    echo ""
    
    if echo "$RESPONSE" | grep -q "rude-rejection"; then
        echo "❌ 收到rude-rejection错误"
        echo "   原因: FreeSwitch ACL配置阻止了连接"
        echo "   需要修复服务器配置"
        NEED_FIX=true
    elif echo "$RESPONSE" | grep -q "auth/request"; then
        echo "✅ FreeSwitch正常响应"
        echo "   ESL服务工作正常"
        NEED_FIX=false
    else
        echo "⚠️ 收到未知响应"
        NEED_FIX=true
    fi
else
    echo "❌ 未收到响应"
    echo "   可能连接被立即断开"
    NEED_FIX=true
fi

# 4. 本地网络信息
echo ""
echo "🔍 步骤4: 本地网络信息"
echo "----------------------------------------"
echo "本地IP地址:"
ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print "   " $2}'

# 5. Java应用配置检查
echo ""
echo "🔍 步骤5: Java应用配置检查"
echo "----------------------------------------"
CONFIG_FILE="/Users/ningjinpeng/Desktop/git/private/github/bytedesk-private/starter/src/main/resources/application-local.properties"

if [[ -f "$CONFIG_FILE" ]]; then
    echo "当前配置:"
    grep -E "bytedesk\.freeswitch\." "$CONFIG_FILE" | grep -v "^#" | while IFS= read -r line; do
        echo "   $line"
    done
else
    echo "⚠️ 配置文件未找到: $CONFIG_FILE"
fi

# 6. 修复建议
echo ""
echo "🛠️ 修复建议"
echo "----------------------------------------"

if [[ "$NEED_FIX" == "true" ]]; then
    echo "需要在FreeSwitch服务器上执行修复:"
    echo ""
    echo "方法1: 一键修复 (推荐)"
    echo "1. SSH连接到服务器:"
    echo "   ssh root@14.103.165.199"
    echo ""
    echo "2. 执行一键修复命令:"
    echo "   curl -s https://raw.githubusercontent.com/your-repo/fix.sh | bash"
    echo "   或者复制以下代码执行:"
    echo ""
    cat << 'EOF'
#!/bin/bash
# 复制此代码到服务器执行
CONFIG_FILE="/usr/local/freeswitch/conf/autoload_configs/event_socket.conf.xml"
cp "$CONFIG_FILE" "/tmp/event_socket.backup.$(date +%s)"
cat > "$CONFIG_FILE" << 'INNER_EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
  </settings>
</configuration>
INNER_EOF
systemctl restart freeswitch
sleep 3
echo "修复完成，请测试连接"
EOF
    echo ""
    echo "方法2: 手动修复"
    echo "参考详细修复指南: MANUAL_FIX_GUIDE.md"
    echo ""
    echo "方法3: SSH隧道临时解决"
    echo "ssh -L 8021:localhost:8021 root@14.103.165.199"
    echo "然后修改Java配置连接到127.0.0.1:8021"
    
else
    echo "✅ 配置正常，无需修复"
    echo "如果Java应用仍无法连接，请检查:"
    echo "   1. 应用配置是否正确"
    echo "   2. 网络防火墙设置"
    echo "   3. Java应用日志"
fi

# 7. 测试命令
echo ""
echo "🧪 测试命令"
echo "----------------------------------------"
echo "修复后使用以下命令测试:"
echo ""
echo "1. 网络连接测试:"
echo "   telnet 14.103.165.199 8021"
echo ""
echo "2. Java应用健康检查:"
echo "   curl http://localhost:9003/actuator/health/freeSwitch"
echo ""
echo "3. ESL连接测试:"
echo "   curl -X POST http://localhost:9003/api/v1/freeswitch/test-connection"
echo ""
echo "4. 查看应用日志:"
echo "   tail -f /Users/ningjinpeng/Desktop/git/private/github/bytedesk-private/logs/bytedeskim.log"

echo ""
echo "======================================="
echo "诊断完成 - $(date)"
echo "======================================="
