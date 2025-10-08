#!/bin/bash

echo "==================================="
echo "etcd 容器状态检测"
echo "==================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

CONTAINER_NAME="etcd-bytedesk"

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
elif [ -z "$HEALTH_STATUS" ]; then
    echo -e "${YELLOW}⚠ 容器未配置健康检查${NC}"
else
    echo -e "${RED}✗ 容器健康状态: ${HEALTH_STATUS}${NC}"
fi

# 4. 检查端口映射
echo -e "\n[4] 检查端口映射..."
docker port ${CONTAINER_NAME} | while read line; do
    echo -e "${GREEN}  ✓ ${line}${NC}"
done

# 5. 测试 etcd 连接
echo -e "\n[5] 测试 etcd 连接..."
ETCD_VERSION=$(docker exec ${CONTAINER_NAME} etcdctl version 2>&1 | head -n 1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ etcdctl 可用${NC}"
    echo "  $ETCD_VERSION"
else
    echo -e "${RED}✗ etcdctl 不可用${NC}"
fi

# 6. 测试 etcd 健康检查
echo -e "\n[6] 测试 etcd 健康检查..."
HEALTH_CHECK=$(docker exec ${CONTAINER_NAME} etcdctl endpoint health 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ etcd 健康检查通过${NC}"
    echo "$HEALTH_CHECK"
else
    echo -e "${RED}✗ etcd 健康检查失败${NC}"
    echo "$HEALTH_CHECK"
fi

# 7. 测试基本操作
echo -e "\n[7] 测试基本操作（增删查）..."

# 写入测试
PUT_TEST=$(docker exec ${CONTAINER_NAME} etcdctl put /test/check "test-value" 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 写入测试成功${NC}"
else
    echo -e "${RED}✗ 写入测试失败${NC}"
    echo "$PUT_TEST"
fi

# 读取测试
GET_TEST=$(docker exec ${CONTAINER_NAME} etcdctl get /test/check 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 读取测试成功${NC}"
    echo "  值: $(echo "$GET_TEST" | tail -n 1)"
else
    echo -e "${RED}✗ 读取测试失败${NC}"
    echo "$GET_TEST"
fi

# 删除测试
DEL_TEST=$(docker exec ${CONTAINER_NAME} etcdctl del /test/check 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 删除测试成功${NC}"
else
    echo -e "${RED}✗ 删除测试失败${NC}"
    echo "$DEL_TEST"
fi

# 8. 检查 etcd 成员状态
echo -e "\n[8] 检查 etcd 成员状态..."
MEMBER_LIST=$(docker exec ${CONTAINER_NAME} etcdctl member list 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 成员列表获取成功${NC}"
    echo "$MEMBER_LIST"
else
    echo -e "${RED}✗ 成员列表获取失败${NC}"
    echo "$MEMBER_LIST"
fi

# 9. 检查数据库大小
echo -e "\n[9] 检查数据库大小..."
ENDPOINT_STATUS=$(docker exec ${CONTAINER_NAME} etcdctl endpoint status --write-out=table 2>&1)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 状态信息获取成功${NC}"
    echo "$ENDPOINT_STATUS"
else
    echo -e "${RED}✗ 状态信息获取失败${NC}"
    echo "$ENDPOINT_STATUS"
fi

# 10. 检查最近的日志
echo -e "\n[10] 最近的日志 (最后 10 行)..."
docker logs --tail 10 ${CONTAINER_NAME} 2>&1

echo -e "\n==================================="
echo -e "${GREEN}检测完成！${NC}"
echo "==================================="

# 显示连接信息
echo -e "\n${YELLOW}连接信息：${NC}"
echo "  Client API: http://localhost:12379"
echo "  Peer API:   http://localhost:12380"
echo ""
echo -e "${YELLOW}测试命令：${NC}"
echo "  docker exec -it ${CONTAINER_NAME} etcdctl get \"\" --prefix"
echo "  docker exec -it ${CONTAINER_NAME} etcdctl put /test/key value"
echo "  docker exec -it ${CONTAINER_NAME} etcdctl get /test/key"
