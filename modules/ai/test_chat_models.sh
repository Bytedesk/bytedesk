#!/bin/bash

# ChatModel 信息查询 API 测试脚本
# 使用方法: ./test_chat_models.sh [base_url]
# 默认 base_url: http://127.0.0.1:9003

BASE_URL=${1:-"http://127.0.0.1:9003"}
API_BASE="/spring/ai/api/v1/chat-models"

echo "=== ChatModel 信息查询 API 测试 ==="
echo "Base URL: $BASE_URL"
echo "API Base: $API_BASE"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试函数
test_api() {
    local endpoint=$1
    local description=$2
    
    echo -e "${BLUE}测试: $description${NC}"
    echo "URL: $BASE_URL$API_BASE$endpoint"
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL$API_BASE$endpoint")
    
    # 分离响应体和状态码
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    response_body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    if [ "$http_status" = "200" ]; then
        echo -e "${GREEN}✓ 成功 (HTTP $http_status)${NC}"
        echo "响应:"
        echo "$response_body" | jq '.' 2>/dev/null || echo "$response_body"
    else
        echo -e "${RED}✗ 失败 (HTTP $http_status)${NC}"
        echo "响应:"
        echo "$response_body"
    fi
    echo ""
}

# 检查 jq 是否安装
if ! command -v jq &> /dev/null; then
    echo -e "${YELLOW}警告: jq 未安装，JSON 响应将不会格式化${NC}"
    echo "安装 jq: brew install jq (macOS) 或 apt-get install jq (Ubuntu)"
    echo ""
fi

# 检查 curl 是否安装
if ! command -v curl &> /dev/null; then
    echo -e "${RED}错误: curl 未安装${NC}"
    exit 1
fi

echo "开始测试..."
echo ""

# 1. 获取所有ChatModel信息
test_api "/info" "获取所有ChatModel信息"

# 2. 获取Primary ChatModel信息
test_api "/primary" "获取Primary ChatModel信息"

# 3. 获取RAG使用的ChatModel信息
test_api "/rag" "获取RAG使用的ChatModel信息"

# 4. 测试指定的ChatModel (zhipuai)
test_api "/test/zhipuai" "测试智谱AI ChatModel"

# 5. 测试指定的ChatModel (ollama)
test_api "/test/ollama" "测试Ollama ChatModel"

# 6. 测试指定的ChatModel (dashscope)
test_api "/test/dashscope" "测试Dashscope ChatModel"

# 7. 测试所有可用的ChatModel
test_api "/test-all" "测试所有可用的ChatModel"

echo "=== 测试完成 ==="
echo ""
echo "注意事项:"
echo "1. 确保应用正在运行且调试模式已启用 (bytedesk.debug=true)"
echo "2. 确保至少有一个ChatModel提供商已配置并启用"
echo "3. 测试API会实际调用ChatModel，可能产生费用"
echo ""
echo "如果测试失败，请检查:"
echo "- 应用是否正在运行"
echo "- 调试模式是否启用"
echo "- ChatModel配置是否正确"
echo "- 网络连接是否正常" 