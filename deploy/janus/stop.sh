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

# Janus WebRTC 服务器停止脚本
JANUS_BIN="/opt/janus/bin/janus"
PID_FILE="/opt/janus/janus.pid"

export LANG="en_US.UTF-8"

echo "正在停止 Janus WebRTC 服务器..."

# 方法1: 通过 PID 文件停止
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "通过 PID 文件停止进程 (PID: $PID)"
        kill "$PID"
        
        # 等待进程结束
        for i in {1..10}; do
            if ! ps -p "$PID" > /dev/null 2>&1; then
                echo "Janus WebRTC 服务器已成功停止"
                rm -f "$PID_FILE"
                exit 0
            fi
            sleep 1
        done
        
        # 如果进程仍在运行，强制杀死
        echo "进程未响应，强制停止..."
        kill -9 "$PID"
        rm -f "$PID_FILE"
        echo "Janus WebRTC 服务器已强制停止"
    else
        echo "PID 文件存在但进程不存在，清理 PID 文件"
        rm -f "$PID_FILE"
    fi
else
    echo "PID 文件不存在"
fi

# 方法2: 通过进程名查找并停止
PID=$(ps -ef | grep "$JANUS_BIN" | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "发现 Janus 进程 (PID: $PID)，正在停止..."
    kill "$PID"
    
    # 等待进程结束
    for i in {1..10}; do
        if ! ps -p "$PID" > /dev/null 2>&1; then
            echo "Janus WebRTC 服务器已成功停止"
            exit 0
        fi
        sleep 1
    done
    
    # 如果进程仍在运行，强制杀死
    echo "进程未响应，强制停止..."
    kill -9 "$PID"
    echo "Janus WebRTC 服务器已强制停止"
else
    echo "Janus WebRTC 服务器未在运行"
fi 