#!/bin/sh
### 
# 添加权限 chmod +x stop.sh
# 查找监听在9007端口的进程ID (PID)

# 使用netstat命令（可能需要sudo权限）
# PID=$(netstat -tulnp | grep ':9007 ' | awk '{print $7}' | cut -d'/' -f1)

# 或者使用lsof命令（可能需要sudo权限）
PID=$(lsof -t -i:9007)

if [ -z "$PID" ]; then
  echo "No process found listening on port 9007."
else
  # 终止找到的进程
  kill $PID
  echo "Process with PID $PID listening on port 9007 has been terminated."
fi