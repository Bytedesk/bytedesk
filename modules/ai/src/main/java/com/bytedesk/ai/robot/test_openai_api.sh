#!/bin/bash

# OpenAI 兼容接口测试脚本
# 测试 Bytedesk AI Robot Chat Controller 的 chat/completions 接口

BASE_URL="http://localhost:9003"
ENDPOINT="/api/ai/chat/v1/chat/completions"
URL="${BASE_URL}${ENDPOINT}"

echo "🚀 测试 Bytedesk OpenAI 兼容接口"
echo "📍 接口地址: $URL"
echo ""

# 测试 1: 基本非流式请求
echo "📝 测试 1: 基本非流式请求"
echo "----------------------------------------"

curl -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Hello, how are you?"
      }
    ],
    "temperature": 0.7,
    "max_tokens": 150
  }' | jq '.'

echo ""
echo ""

# 测试 2: 流式请求
echo "📝 测试 2: 流式请求"
echo "----------------------------------------"

curl -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "user",
        "content": "Tell me a short joke"
      }
    ],
    "stream": true,
    "temperature": 0.7
  }' --no-buffer

echo ""
echo ""

# 测试 3: 多轮对话
echo "📝 测试 3: 多轮对话"
echo "----------------------------------------"

curl -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant that remembers context."
      },
      {
        "role": "user",
        "content": "My name is Alice"
      },
      {
        "role": "assistant",
        "content": "Hello Alice! Nice to meet you."
      },
      {
        "role": "user",
        "content": "What is my name?"
      }
    ]
  }' | jq '.'

echo ""
echo ""

# 测试 4: 错误处理（无效的角色）
echo "📝 测试 4: 错误处理测试"
echo "----------------------------------------"

curl -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "invalid_role",
        "content": "This should be handled gracefully"
      }
    ]
  }' | jq '.'

echo ""
echo ""

# 测试 5: 空消息
echo "📝 测试 5: 空消息数组"
echo "----------------------------------------"

curl -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": []
  }' | jq '.'

echo ""
echo ""

echo "✅ 测试完成！"
echo ""
echo "💡 使用说明："
echo "1. 确保 Bytedesk 服务器正在运行在 localhost:9003"
echo "2. 确保已配置好 Primary ChatModel"
echo "3. 如果看到错误，请检查服务器日志"
echo ""
echo "🔧 第三方集成示例:"
echo "Python: 修改 openai.base_url = '$BASE_URL/api/ai/chat/v1'"
echo "JavaScript: 修改 baseURL: '$BASE_URL/api/ai/chat/v1'"
echo "Java: service.setBaseUrl('$BASE_URL/api/ai/chat/v1/');"
