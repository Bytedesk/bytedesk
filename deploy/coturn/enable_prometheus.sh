#!/bin/bash

# Coturn 启用 Prometheus 监控的快速配置脚本
# 使用方法: sudo bash enable_prometheus.sh

set -e

echo "=================================="
echo "Coturn Prometheus 监控配置脚本"
echo "=================================="

# 检查是否以 root 运行
if [ "$EUID" -ne 0 ]; then 
    echo "❌ 请使用 sudo 运行此脚本"
    exit 1
fi

# 备份配置文件
CONF_FILE="/etc/turnserver.conf"
if [ ! -f "$CONF_FILE" ]; then
    echo "❌ 找不到配置文件: $CONF_FILE"
    exit 1
fi

BACKUP_FILE="$CONF_FILE.backup.$(date +%Y%m%d_%H%M%S)"
echo "📦 备份配置文件到: $BACKUP_FILE"
cp "$CONF_FILE" "$BACKUP_FILE"

# 检查是否已启用 prometheus
if grep -q "^prometheus" "$CONF_FILE"; then
    echo "✅ Prometheus 已经启用"
else
    echo "📝 启用 Prometheus 监控..."
    # 在文件末尾添加 prometheus 配置
    echo "" >> "$CONF_FILE"
    echo "# Enable Prometheus exporter (added by script)" >> "$CONF_FILE"
    echo "prometheus" >> "$CONF_FILE"
    echo "✅ Prometheus 配置已添加"
fi

# 开放防火墙端口 9641
echo "🔥 配置防火墙..."
if command -v ufw &> /dev/null; then
    ufw allow 9641/tcp comment 'Coturn Prometheus'
    echo "✅ UFW 防火墙规则已添加"
elif command -v firewall-cmd &> /dev/null; then
    firewall-cmd --permanent --add-port=9641/tcp
    firewall-cmd --reload
    echo "✅ FirewallD 规则已添加"
else
    echo "⚠️ 未检测到防火墙工具，请手动开放 9641 端口"
fi

# 重启 Coturn 服务
echo "🔄 重启 Coturn 服务..."
systemctl restart coturn

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 3

# 检查服务状态
if systemctl is-active --quiet coturn; then
    echo "✅ Coturn 服务运行正常"
else
    echo "❌ Coturn 服务启动失败"
    echo "请查看日志: journalctl -u coturn -n 50"
    exit 1
fi

# 检查端口监听
if netstat -tuln | grep -q ":9641"; then
    echo "✅ Prometheus 端口 9641 已监听"
else
    echo "⚠️ 警告: 端口 9641 未监听，可能需要等待或检查配置"
fi

# 测试访问
echo ""
echo "🧪 测试 Prometheus 端点..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:9641/metrics | grep -q "200"; then
    echo "✅ Prometheus 端点测试成功"
    echo ""
    echo "=================================="
    echo "✅ 配置完成！"
    echo "=================================="
    echo ""
    echo "📊 访问监控数据："
    echo "   本地: http://localhost:9641/metrics"
    echo "   外网: http://YOUR_SERVER_IP:9641/metrics"
    echo ""
    echo "🌐 如果配置了 Nginx 代理："
    echo "   HTTPS: https://coturn.weiyuai.cn/metrics"
    echo ""
    echo "💡 提示："
    echo "   - 查看日志: journalctl -u coturn -f"
    echo "   - 查看状态: systemctl status coturn"
    echo "   - 查看端口: netstat -tuln | grep turnserver"
    echo ""
else
    echo "❌ Prometheus 端点测试失败"
    echo "请检查配置和日志: journalctl -u coturn -n 50"
    exit 1
fi
