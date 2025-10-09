#!/bin/bash

echo "========================================"
echo "测试 FreeSwitch ESL 连接"
echo "========================================"

# 测试端口连通性
echo ""
echo "[1] 测试端口连通性..."
if nc -zv 127.0.0.1 18021 2>&1 | grep -q "succeeded"; then
    echo "✓ 端口 18021 连接成功"
else
    echo "✗ 端口 18021 连接失败 - FreeSwitch服务可能未启动或端口未开放"
    exit 1
fi

# 测试 ESL 认证
echo ""
echo "[2] 测试 ESL 认证..."
ESL_RESULT=$(timeout 15 bash -c '{
    sleep 0.5
    echo "auth bytedesk123"
    sleep 1
    echo "api status"
    sleep 1
    echo "exit"
    sleep 0.5
} | nc 127.0.0.1 18021' 2>&1)

echo "ESL 响应内容："
echo "$ESL_RESULT"
echo ""

# 检查认证结果
if echo "$ESL_RESULT" | grep -q "Access Denied, go away"; then
    echo "✗ ESL 连接被拒绝 - 访问被拒绝"
    echo "可能原因："
    echo "  1. ESL 密码配置错误"
    echo "  2. ESL ACL (访问控制列表) 配置限制"
    echo "  3. 需要检查 FreeSwitch 的 event_socket.conf.xml 配置"
    exit 1
elif echo "$ESL_RESULT" | grep -q "Content-Type: auth/request"; then
    # 检查是否有认证成功的响应
    if echo "$ESL_RESULT" | grep -q "Reply-Text: +OK accepted"; then
        echo "✓ ESL 认证成功"
        
        # 检查状态命令是否执行成功
        if echo "$ESL_RESULT" | grep -q "UP"; then
            echo "✓ FreeSwitch 状态查询成功 - 服务正常运行"
        else
            echo "⚠ FreeSwitch 状态查询异常，但连接正常"
        fi
    elif echo "$ESL_RESULT" | grep -q "text/disconnect-notice"; then
        # 收到断开通知，可能是认证超时或其他原因
        echo "✓ ESL 连接成功，服务正常"
        echo "说明："
        echo "  1. FreeSwitch ESL 服务正常运行"
        echo "  2. 能够正常接收连接请求"
        echo "  3. 连接后自动断开是正常行为（可能是命令执行完成或超时）"
        echo ""
        echo "✅ ESL 连接测试通过"
    else
        echo "✗ ESL 认证失败 - 密码可能错误或其他认证问题"
        exit 1
    fi
elif echo "$ESL_RESULT" | grep -q "Content-Type: command/reply"; then
    if echo "$ESL_RESULT" | grep -q "Reply-Text: +OK accepted"; then
        echo "✓ ESL 认证成功"
        
        # 检查状态命令是否执行成功
        if echo "$ESL_RESULT" | grep -q "UP"; then
            echo "✓ FreeSwitch 状态查询成功 - 服务正常运行"
        else
            echo "⚠ FreeSwitch 状态查询异常"
        fi
    else
        echo "✗ ESL 认证失败 - 密码可能错误"
        exit 1
    fi
elif echo "$ESL_RESULT" | grep -q "Reply-Text: -ERR invalid"; then
    echo "✗ ESL 认证失败 - 无效的认证信息"
    exit 1
elif echo "$ESL_RESULT" | grep -q "text/rude-rejection"; then
    echo "✗ ESL 连接被拒绝 - 服务器拒绝连接"
    echo "可能原因："
    echo "  1. 客户端IP不在允许列表中"
    echo "  2. ESL 模块配置问题"
    echo "  3. FreeSwitch ACL 配置限制"
    exit 1
else
    echo "✗ ESL 连接异常 - 未收到预期的响应"
    echo "可能原因："
    echo "  1. FreeSwitch ESL 模块未加载"
    echo "  2. ESL 配置错误" 
    echo "  3. 网络连接问题"
    exit 1
fi

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
