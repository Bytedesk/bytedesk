#!/bin/bash

# FreeSwitch ESL 连接状态监控 (本地执行)
# 持续监控连接状态变化

SERVER="14.103.165.199"
PORT="8021"

echo "========================================="
echo "FreeSwitch ESL 连接状态监控"
echo "========================================="
echo "监控服务器: $SERVER:$PORT"
echo "开始时间: $(date)"
echo "按 Ctrl+C 停止监控"
echo ""

# 初始状态检查
echo "🔍 初始状态检查:"
echo "-----------------------------------------"

# 1. 网络连通性
if ping -c 1 -W 2 $SERVER > /dev/null 2>&1; then
    echo "✅ 网络连通性: 正常"
else
    echo "❌ 网络连通性: 失败"
    exit 1
fi

# 2. 端口连通性
if nc -z -w 2 $SERVER $PORT 2>/dev/null; then
    echo "✅ 端口连通性: 正常"
else
    echo "❌ 端口连通性: 失败"
    exit 1
fi

# 3. ESL协议测试
echo "🔍 ESL协议测试:"
RESPONSE=$(timeout 2 bash -c "echo '' | nc $SERVER $PORT" 2>/dev/null)

if [[ -n "$RESPONSE" ]]; then
    if echo "$RESPONSE" | grep -q "rude-rejection"; then
        echo "❌ ESL状态: 连接被拒绝 (ACL限制)"
        echo "   响应: $(echo "$RESPONSE" | head -1)"
    elif echo "$RESPONSE" | grep -q "auth/request"; then
        echo "✅ ESL状态: 等待认证 (连接正常)"
        echo "   响应: $(echo "$RESPONSE" | head -1)"
    else
        echo "⚠️ ESL状态: 未知响应"
        echo "   响应: $(echo "$RESPONSE" | head -1)"
    fi
else
    echo "❌ ESL状态: 无响应"
fi

echo ""
echo "=== 持续监控模式 (每30秒检查一次) ==="
echo ""

# 持续监控循环
while true; do
    TIMESTAMP=$(date '+%H:%M:%S')
    
    # 测试ESL连接
    RESPONSE=$(timeout 2 bash -c "echo '' | nc $SERVER $PORT" 2>/dev/null)
    
    if [[ -n "$RESPONSE" ]]; then
        if echo "$RESPONSE" | grep -q "rude-rejection"; then
            echo "[$TIMESTAMP] ❌ 仍然被拒绝 - ACL限制未解除"
        elif echo "$RESPONSE" | grep -q "auth/request"; then
            echo "[$TIMESTAMP] ✅ 连接成功! - 收到认证请求"
            echo ""
            echo "🎉 FreeSwitch ESL 修复成功!"
            echo "Java应用现在可以连接到FreeSwitch了"
            break
        else
            echo "[$TIMESTAMP] ⚠️ 未知响应: $(echo "$RESPONSE" | head -1 | tr -d '\r\n')"
        fi
    else
        echo "[$TIMESTAMP] ❌ 无响应或连接失败"
    fi
    
    sleep 30
done

echo ""
echo "=== 最终验证 ==="

# Java应用健康检查测试
if command -v curl > /dev/null 2>&1; then
    echo "🔍 测试Java应用健康状态..."
    
    # 假设Java应用在本地9003端口运行
    if curl -s -f http://localhost:9003/actuator/health/freeSwitch > /dev/null 2>&1; then
        echo "✅ Java应用FreeSwitch健康检查通过"
        
        # 获取详细健康状态
        echo ""
        echo "📊 详细健康状态:"
        curl -s http://localhost:9003/actuator/health/freeSwitch | python -m json.tool 2>/dev/null || echo "健康检查响应获取成功"
        
    else
        echo "⚠️ Java应用未运行或健康检查失败"
        echo "   请启动Java应用并确保FreeSwitch模块已启用"
    fi
else
    echo "💡 建议: 安装curl后可测试Java应用健康状态"
fi

echo ""
echo "=== 监控完成 ==="
echo "结束时间: $(date)"
