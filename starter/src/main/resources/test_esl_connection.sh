#!/bin/bash

echo "========================================"
echo "测试 FreeSwitch ESL 连接"
echo "========================================"

# 测试端口连通性
echo ""
echo "[1] 测试端口连通性..."
if nc -zv 127.0.0.1 18021 2>&1 | grep -q "succeeded"; then
    echo "✓ 端口 18021 可以连接"
else
    echo "✗ 端口 18021 无法连接"
    exit 1
fi

# 测试 ESL 认证
echo ""
echo "[2] 测试 ESL 认证..."
{
    sleep 2
    echo "auth bytedesk123"
    sleep 2
    echo "api status"
    sleep 2
    echo "exit"
    sleep 1
} | nc 127.0.0.1 18021

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
