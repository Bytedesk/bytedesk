#!/bin/bash

# FreeSwitch ESL 连接诊断脚本
# 创建时间: 2025-06-08

echo "=== FreeSwitch ESL 连接诊断 ==="
echo "时间: $(date)"
echo "目标服务器: 14.103.165.199:8021"
echo ""

echo "1. 测试端口连通性..."
timeout 5 bash -c "</dev/tcp/14.103.165.199/8021" && echo "✅ 端口8021可以连接" || echo "❌ 端口8021无法连接"
echo ""

echo "2. 使用nc测试连接..."
echo "quit" | nc -w 3 14.103.165.199 8021
echo ""

echo "3. 使用telnet测试连接并获取详细响应..."
(echo ""; sleep 2) | telnet 14.103.165.199 8021 2>/dev/null
echo ""

echo "4. 检查本地网络配置..."
echo "本地IP地址:"
ifconfig | grep "inet " | grep -v 127.0.0.1
echo ""

echo "5. 分析可能的原因:"
echo "   - 如果收到 'rude-rejection'，说明FreeSwitch服务运行正常但ACL拒绝连接"
echo "   - 如果连接超时，说明网络不通或端口未开放"
echo "   - 如果连接拒绝，说明FreeSwitch服务未运行"
echo ""

echo "6. 建议的修复步骤:"
echo "   a) 确认FreeSwitch服务状态: systemctl status freeswitch"
echo "   b) 重新加载ESL配置: fs_cli -x 'reload mod_event_socket'"
echo "   c) 重启FreeSwitch服务: systemctl restart freeswitch"
echo "   d) 检查防火墙设置: iptables -L | grep 8021"
echo "   e) 查看FreeSwitch日志: tail -f /usr/local/freeswitch/log/freeswitch.log"
echo ""

echo "=== 诊断完成 ==="
