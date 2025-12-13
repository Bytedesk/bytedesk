# ByteDesk Docker Compose 使用指南

## 快速检测脚本

我们提供了快速检测脚本来检查各个服务的状态：

```bash
# 检测 FreeSwitch 容器状态
./check_freeswitch.sh

# 检测 etcd 容器状态
./check_etcd.sh
```

## 检测 etcd 容器状态

### 快速检测

```bash
# 运行 etcd 检测脚本
./check_etcd.sh
```

该脚本会自动检测：
- ✅ 容器是否存在和运行
- ✅ 健康状态检查
- ✅ 端口映射情况
- ✅ etcdctl 工具可用性
- ✅ 基本读写操作测试
- ✅ 成员状态和数据库大小

### 手动检测方法

```bash
# 查看容器状态
docker ps | grep etcd

# 测试健康检查
docker exec -it etcd-bytedesk etcdctl endpoint health

# 查看成员列表
docker exec -it etcd-bytedesk etcdctl member list

# 测试读写操作
docker exec -it etcd-bytedesk etcdctl put /test/key "test-value"
docker exec -it etcd-bytedesk etcdctl get /test/key

# 查看所有键
docker exec -it etcd-bytedesk etcdctl get "" --prefix
```

## 检测 FreeSwitch 容器状态

### 方法一：检查容器运行状态

```bash
# 查看 FreeSwitch 容器状态
docker ps -a --filter "name=freeswitch-bytedesk"

# 或使用 docker-compose
docker-compose ps bytedesk-freeswitch
```

### 方法二：检查健康状态

```bash
# 检查容器健康状态
docker inspect --format='{{.State.Health.Status}}' freeswitch-bytedesk

# 查看详细健康检查日志
docker inspect --format='{{json .State.Health}}' freeswitch-bytedesk | jq
```

### 方法三：查看 FreeSwitch 日志

```bash
# 实时查看日志
docker logs -f freeswitch-bytedesk

# 查看最近 100 行日志
docker logs --tail 100 freeswitch-bytedesk
```

### 方法四：使用 fs_cli 连接测试

```bash
# 进入容器
docker exec -it freeswitch-bytedesk /bin/bash

# 在容器内执行 fs_cli
fs_cli -p bytedesk123 -x "status"

# 或直接从外部执行
docker exec -it freeswitch-bytedesk fs_cli -p bytedesk123 -x "status"
```

### 方法五：一键检测脚本

创建以下脚本 `check_freeswitch.sh`：

```bash
#!/bin/bash

echo "==================================="
echo "FreeSwitch 容器状态检测"
echo "==================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

CONTAINER_NAME="freeswitch-bytedesk"

# 1. 检查容器是否存在
echo -e "\n[1] 检查容器是否存在..."
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${GREEN}✓ 容器存在${NC}"
else
    echo -e "${RED}✗ 容器不存在${NC}"
    exit 1
fi

# 2. 检查容器是否运行
echo -e "\n[2] 检查容器是否运行..."
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo -e "${GREEN}✓ 容器正在运行${NC}"
else
    echo -e "${RED}✗ 容器未运行${NC}"
    exit 1
fi

# 3. 检查容器健康状态
echo -e "\n[3] 检查容器健康状态..."
HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' ${CONTAINER_NAME} 2>/dev/null)
if [ "$HEALTH_STATUS" = "healthy" ]; then
    echo -e "${GREEN}✓ 容器健康状态: healthy${NC}"
elif [ "$HEALTH_STATUS" = "starting" ]; then
    echo -e "${YELLOW}⚠ 容器健康状态: starting (启动中)${NC}"
else
    echo -e "${RED}✗ 容器健康状态: ${HEALTH_STATUS}${NC}"
fi

# 4. 检查端口映射
echo -e "\n[4] 检查端口映射..."
docker port ${CONTAINER_NAME} | while read line; do
    echo -e "${GREEN}  ✓ ${line}${NC}"
done

# 5. 测试 ESL 连接
echo -e "\n[5] 测试 FreeSwitch ESL 连接..."
ESL_TEST=$(docker exec ${CONTAINER_NAME} fs_cli -p bytedesk123 -x "status" 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ ESL 连接成功${NC}"
    echo "$ESL_TEST" | head -n 5
else
    echo -e "${RED}✗ ESL 连接失败${NC}"
    echo "$ESL_TEST"
fi

# 6. 检查最近的日志
echo -e "\n[6] 最近的日志 (最后 10 行)..."
docker logs --tail 10 ${CONTAINER_NAME} 2>&1

echo -e "\n==================================="
echo -e "${GREEN}检测完成！${NC}"
echo "==================================="
```

使用方法：

```bash
# 给脚本添加执行权限
chmod +x check_freeswitch.sh

# 运行脚本
./check_freeswitch.sh
```

### 常见问题排查

#### 1. 容器启动失败
```bash
# 查看启动错误日志
docker logs freeswitch-bytedesk

# 检查配置文件挂载是否正确
docker inspect freeswitch-bytedesk | grep -A 10 "Mounts"
```

#### 2. 健康检查失败
```bash
# 查看健康检查日志
docker inspect --format='{{json .State.Health.Log}}' freeswitch-bytedesk | jq

# 手动执行健康检查命令
docker exec -it freeswitch-bytedesk fs_cli -p bytedesk123 -x "status"
```

#### 3. 端口冲突
```bash
# 检查端口占用
lsof -i :15060
lsof -i :18021

# 或使用 netstat
netstat -an | grep 15060
```

#### 4. 网络连接问题
```bash
# 检查网络配置
docker network inspect bytedesk-network

# 测试容器间网络连通性
docker exec -it freeswitch-bytedesk ping bytedesk-mysql
```

## 启动所有服务

```bash
# 启动所有服务
docker-compose up -d

# 查看所有服务状态
docker-compose ps

# 停止所有服务
docker-compose down

# 停止并删除所有数据
docker-compose down -v
```
