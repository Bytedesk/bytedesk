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

# Janus WebRTC 服务器状态检查脚本
JANUS_BIN="/opt/janus/bin/janus"
PID_FILE="/opt/janus/janus.pid"
LOG_FILE="/opt/janus/logs/janus.log"

export LANG="en_US.UTF-8"

echo "=== Janus WebRTC 服务器状态检查 ==="

# 检查可执行文件
if [ -f "$JANUS_BIN" ]; then
    echo "✓ Janus 可执行文件存在: $JANUS_BIN"
else
    echo "✗ Janus 可执行文件不存在: $JANUS_BIN"
fi

# 检查 PID 文件
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    echo "✓ PID 文件存在: $PID_FILE (PID: $PID)"
    
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "✓ 进程正在运行 (PID: $PID)"
        
        # 显示进程详细信息
        echo "进程详细信息:"
        ps -p "$PID" -o pid,ppid,user,comm,etime,pcpu,pmem,args
        
        # 检查端口占用情况（Janus 默认使用 8088 和 8089 端口）
        echo "端口占用情况:"
        netstat -tlnp 2>/dev/null | grep "$PID" || echo "  未找到端口占用信息"
        
    else
        echo "✗ PID 文件存在但进程不存在"
    fi
else
    echo "✗ PID 文件不存在: $PID_FILE"
fi

# 通过进程名查找
PID=$(ps -ef | grep "$JANUS_BIN" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "✓ 通过进程名找到 Janus 进程 (PID: $PID)"
else
    echo "✗ 未找到 Janus 进程"
fi

# 检查日志文件
if [ -f "$LOG_FILE" ]; then
    echo "✓ 日志文件存在: $LOG_FILE"
    echo "日志文件大小: $(ls -lh "$LOG_FILE" | awk '{print $5}')"
    echo "最后 10 行日志:"
    tail -10 "$LOG_FILE" 2>/dev/null || echo "  无法读取日志文件"
else
    echo "✗ 日志文件不存在: $LOG_FILE"
fi

echo "=== 状态检查完成 ===" 