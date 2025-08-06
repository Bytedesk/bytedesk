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

# Janus WebRTC 服务器重启脚本
JANUS_BIN="/opt/janus/bin/janus"
JANUS_CONFIG="/opt/janus/etc/janus/janus.jcfg"
LOG_FILE="/opt/janus/logs/janus.log"
PID_FILE="/opt/janus/janus.pid"

export LANG="en_US.UTF-8"

echo "=== Janus WebRTC 服务器重启脚本 ==="

# 检查 Janus 可执行文件是否存在
if [ ! -f "$JANUS_BIN" ]; then
    echo "错误: Janus 可执行文件不存在: $JANUS_BIN"
    echo "请确保 Janus 已正确安装到 /opt/janus 目录"
    exit 1
fi

# 检查配置文件是否存在
if [ ! -f "$JANUS_CONFIG" ]; then
    echo "错误: Janus 配置文件不存在: $JANUS_CONFIG"
    exit 1
fi

# 创建日志目录
mkdir -p "$(dirname "$LOG_FILE")"

echo "正在停止 Janus WebRTC 服务器..."

# 方法1: 通过 PID 文件停止
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "通过 PID 文件停止进程 (PID: $PID)..."
        kill "$PID"
        sleep 3
        
        # 检查进程是否已停止
        if ps -p "$PID" > /dev/null 2>&1; then
            echo "进程仍在运行，强制终止..."
            kill -9 "$PID"
            sleep 2
        fi
        
        rm -f "$PID_FILE"
        echo "进程已停止"
    else
        echo "PID 文件中的进程已不存在，清理 PID 文件..."
        rm -f "$PID_FILE"
    fi
fi

# 方法2: 通过进程名查找并停止
PID=$(ps -ef | grep "$JANUS_BIN" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "发现 Janus 进程 (PID: $PID)，正在停止..."
    kill "$PID"
    sleep 3
    
    # 检查进程是否已停止
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "进程仍在运行，强制终止..."
        kill -9 "$PID"
        sleep 2
    fi
    
    echo "进程已停止"
else
    echo "未发现运行中的 Janus 进程"
fi

# 确保 PID 文件被清理
rm -f "$PID_FILE"

echo "正在启动 Janus WebRTC 服务器..."

# 后台启动 Janus
nohup "$JANUS_BIN" > "$LOG_FILE" 2>&1 &
JANUS_PID=$!

# 保存 PID
echo "$JANUS_PID" > "$PID_FILE"

# 等待一下确保进程启动
sleep 3

# 检查进程是否成功启动
if ps -p "$JANUS_PID" > /dev/null 2>&1; then
    echo "=== 重启成功 ==="
    echo "Janus WebRTC 服务器已重启 (PID: $JANUS_PID)"
    echo "日志文件: $LOG_FILE"
    echo "PID 文件: $PID_FILE"
    echo "可以使用以下命令查看日志:"
    echo "  tail -f $LOG_FILE"
else
    echo "=== 重启失败 ==="
    echo "Janus WebRTC 服务器启动失败"
    rm -f "$PID_FILE"
    exit 1
fi 