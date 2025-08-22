#!/usr/bin/env python3
"""
Bytedesk OpenAI 兼容接口示例
使用标准的 OpenAI Python 客户端库调用 Bytedesk AI 服务

安装依赖:
pip install openai

使用方法:
python openai_client_example.py
"""

import openai
import asyncio
from typing import List, Dict

# 配置 Bytedesk 服务
BYTEDESK_BASE_URL = "http://localhost:9003/api/ai/chat/v1"
BYTEDESK_API_KEY = "your-api-key"  # 可以是任意值，Bytedesk 暂不验证


class BytedeskOpenAIClient:
    """Bytedesk OpenAI 兼容客户端"""
    
    def __init__(self, base_url: str = BYTEDESK_BASE_URL, api_key: str = BYTEDESK_API_KEY):
        self.client = openai.OpenAI(
            api_key=api_key,
            base_url=base_url
        )
    
    def chat_completion(self, messages: List[Dict[str, str]], **kwargs):
        """发送聊天请求"""
        try:
            response = self.client.chat.completions.create(
                model="bytedesk-ai",
                messages=messages,
                **kwargs
            )
            return response
        except Exception as e:
            print(f"❌ 请求失败: {e}")
            return None
    
    def stream_chat_completion(self, messages: List[Dict[str, str]], **kwargs):
        """流式聊天请求"""
        try:
            stream = self.client.chat.completions.create(
                model="bytedesk-ai",
                messages=messages,
                stream=True,
                **kwargs
            )
            return stream
        except Exception as e:
            print(f"❌ 流式请求失败: {e}")
            return None


def test_basic_chat():
    """测试基本聊天功能"""
    print("🔄 测试基本聊天功能...")
    
    client = BytedeskOpenAIClient()
    
    messages = [
        {"role": "system", "content": "你是一个有帮助的AI助手。"},
        {"role": "user", "content": "你好，请介绍一下自己。"}
    ]
    
    response = client.chat_completion(messages, temperature=0.7, max_tokens=200)
    
    if response:
        print("✅ 请求成功!")
        print(f"📝 回复: {response.choices[0].message.content}")
        print(f"📊 Token 使用: {response.usage}")
        print(f"🆔 请求 ID: {response.id}")
    else:
        print("❌ 请求失败")


def test_multi_turn_conversation():
    """测试多轮对话"""
    print("\n🔄 测试多轮对话...")
    
    client = BytedeskOpenAIClient()
    
    messages = [
        {"role": "system", "content": "你是一个记忆力很好的助手。"},
        {"role": "user", "content": "我的名字是张三。"},
        {"role": "assistant", "content": "你好张三！很高兴认识你。"},
        {"role": "user", "content": "我的名字是什么？"}
    ]
    
    response = client.chat_completion(messages)
    
    if response:
        print("✅ 多轮对话测试成功!")
        print(f"📝 回复: {response.choices[0].message.content}")
    else:
        print("❌ 多轮对话测试失败")


def test_streaming_chat():
    """测试流式聊天"""
    print("\n🔄 测试流式聊天...")
    
    client = BytedeskOpenAIClient()
    
    messages = [
        {"role": "user", "content": "请给我讲一个短故事。"}
    ]
    
    stream = client.stream_chat_completion(messages, temperature=0.8)
    
    if stream:
        print("✅ 流式请求启动成功!")
        print("📝 流式回复: ", end="", flush=True)
        
        try:
            for chunk in stream:
                if chunk.choices[0].delta.content is not None:
                    print(chunk.choices[0].delta.content, end="", flush=True)
            print("\n🏁 流式回复完成")
        except Exception as e:
            print(f"\n❌ 流式处理错误: {e}")
    else:
        print("❌ 流式请求失败")


def test_different_parameters():
    """测试不同的参数配置"""
    print("\n🔄 测试不同参数配置...")
    
    client = BytedeskOpenAIClient()
    
    # 测试高创造性参数
    messages = [
        {"role": "user", "content": "用一个词描述春天。"}
    ]
    
    print("🌡️  高创造性 (temperature=1.5):")
    response = client.chat_completion(messages, temperature=1.5, max_tokens=50)
    if response:
        print(f"   回复: {response.choices[0].message.content}")
    
    print("🧊 低创造性 (temperature=0.1):")
    response = client.chat_completion(messages, temperature=0.1, max_tokens=50)
    if response:
        print(f"   回复: {response.choices[0].message.content}")


def test_error_handling():
    """测试错误处理"""
    print("\n🔄 测试错误处理...")
    
    # 测试空消息
    client = BytedeskOpenAIClient()
    response = client.chat_completion([])
    
    if response is None:
        print("✅ 空消息错误处理正确")
    else:
        print("⚠️  空消息未正确处理")


async def test_async_chat():
    """测试异步聊天 (如果需要)"""
    print("\n🔄 测试异步功能...")
    print("ℹ️  当前使用同步客户端，异步功能需要 AsyncOpenAI")


def main():
    """主测试函数"""
    print("🚀 Bytedesk OpenAI 兼容接口测试")
    print("=" * 50)
    
    # 检查服务连通性
    print("🔍 检查服务连通性...")
    try:
        import requests
        response = requests.get("http://localhost:9003/actuator/health", timeout=5)
        if response.status_code == 200:
            print("✅ Bytedesk 服务连接正常")
        else:
            print("⚠️  Bytedesk 服务状态异常")
    except Exception as e:
        print(f"❌ 无法连接到 Bytedesk 服务: {e}")
        print("请确保服务运行在 localhost:9003")
        return
    
    # 运行测试
    test_basic_chat()
    test_multi_turn_conversation()
    test_streaming_chat()
    test_different_parameters()
    test_error_handling()
    
    print("\n" + "=" * 50)
    print("🎉 所有测试完成!")
    print("\n💡 集成提示:")
    print("1. 只需要修改 base_url 就可以使用现有的 OpenAI 代码")
    print("2. API Key 可以是任意值，Bytedesk 当前不做验证")
    print("3. 支持所有标准的 OpenAI Chat Completions 参数")
    print("4. 支持流式和非流式响应")


if __name__ == "__main__":
    main()
