#!/bin/sh
###
 # @Author: jackning 270580156@qq.com
 # @Date: 2025-08-05 07:53:04
 # @LastEditors: jackning 270580156@qq.com
 # @LastEditTime: 2025-08-05 08:02:14
 # @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 #   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 #  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 #  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 #  contact: 270580156@qq.com 
 # 
 # Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
###

# Janus WebRTC 服务器安装和配置脚本
JANUS_DIR="/opt/janus"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

export LANG="en_US.UTF-8"

echo "=== Janus WebRTC 服务器安装和配置脚本 ==="

# 检查是否为 root 用户
if [ "$EUID" -ne 0 ]; then
    echo "请使用 root 用户运行此脚本"
    echo "使用: sudo $0"
    exit 1
fi

# 检查 Janus 是否已安装
if [ ! -f "$JANUS_DIR/bin/janus" ]; then
    echo "错误: Janus 未安装在 $JANUS_DIR"
    echo "请先安装 Janus WebRTC 服务器"
    exit 1
fi

echo "✓ 检测到 Janus 安装: $JANUS_DIR"

# 创建必要的目录
echo "创建必要的目录..."
mkdir -p "$JANUS_DIR/logs"
mkdir -p "$JANUS_DIR/run"

# 设置权限
echo "设置目录权限..."
chown -R root:root "$JANUS_DIR"
chmod -R 755 "$JANUS_DIR"
chmod 755 "$JANUS_DIR/bin/janus"

# 复制脚本到 Janus 目录
echo "复制管理脚本..."
cp "$SCRIPT_DIR/start.sh" "$JANUS_DIR/"
cp "$SCRIPT_DIR/stop.sh" "$JANUS_DIR/"
cp "$SCRIPT_DIR/status.sh" "$JANUS_DIR/"
chmod +x "$JANUS_DIR"/*.sh

# 安装 systemd 服务
echo "安装 systemd 服务..."
cp "$SCRIPT_DIR/janus.service" /etc/systemd/system/
systemctl daemon-reload

# 启用服务
echo "启用 systemd 服务..."
systemctl enable janus

echo ""
echo "=== 安装完成 ==="
echo ""
echo "使用方法:"
echo "1. 启动服务: systemctl start janus"
echo "2. 停止服务: systemctl stop janus"
echo "3. 重启服务: systemctl restart janus"
echo "4. 查看状态: systemctl status janus"
echo "5. 查看日志: journalctl -u janus -f"
echo ""
echo "或者使用脚本:"
echo "1. 启动服务: $JANUS_DIR/start.sh"
echo "2. 停止服务: $JANUS_DIR/stop.sh"
echo "3. 查看状态: $JANUS_DIR/status.sh"
echo ""
echo "配置文件位置: $JANUS_DIR/etc/janus/janus.jcfg"
echo "日志文件位置: $JANUS_DIR/logs/janus.log" 