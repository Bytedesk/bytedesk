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
