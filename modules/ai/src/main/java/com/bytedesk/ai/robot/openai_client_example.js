/**
 * Bytedesk OpenAI 兼容接口 Node.js 示例
 * 
 * 安装依赖:
 * npm install openai
 * 
 * 使用方法:
 * node openai_client_example.js
 */

import OpenAI from 'openai';

// 配置 Bytedesk 服务
const BYTEDESK_BASE_URL = 'http://localhost:9003/api/ai/chat/v1';
const BYTEDESK_API_KEY = 'your-api-key'; // 可以是任意值

class BytedeskOpenAIClient {
  constructor(baseURL = BYTEDESK_BASE_URL, apiKey = BYTEDESK_API_KEY) {
    this.openai = new OpenAI({
      apiKey: apiKey,
      baseURL: baseURL
    });
  }

  /**
   * 基本聊天请求
   */
  async chatCompletion(messages, options = {}) {
    try {
      const completion = await this.openai.chat.completions.create({
        model: 'bytedesk-ai',
        messages: messages,
        ...options
      });
      return completion;
    } catch (error) {
      console.error('❌ 聊天请求失败:', error.message);
      return null;
    }
  }

  /**
   * 流式聊天请求
   */
  async streamChatCompletion(messages, options = {}) {
    try {
      const stream = await this.openai.chat.completions.create({
        model: 'bytedesk-ai',
        messages: messages,
        stream: true,
        ...options
      });
      return stream;
    } catch (error) {
      console.error('❌ 流式聊天请求失败:', error.message);
      return null;
    }
  }
}

/**
 * 测试基本聊天功能
 */
async function testBasicChat() {
  console.log('🔄 测试基本聊天功能...');
  
  const client = new BytedeskOpenAIClient();
  
  const messages = [
    { role: 'system', content: '你是一个有帮助的AI助手。' },
    { role: 'user', content: '你好，请介绍一下自己。' }
  ];

  const completion = await client.chatCompletion(messages, {
    temperature: 0.7,
    max_tokens: 200
  });

  if (completion) {
    console.log('✅ 请求成功!');
    console.log(`📝 回复: ${completion.choices[0].message.content}`);
    console.log(`📊 Token 使用:`, completion.usage);
    console.log(`🆔 请求 ID: ${completion.id}`);
  } else {
    console.log('❌ 请求失败');
  }
}

/**
 * 测试多轮对话
 */
async function testMultiTurnConversation() {
  console.log('\n🔄 测试多轮对话...');
  
  const client = new BytedeskOpenAIClient();
  
  const messages = [
    { role: 'system', content: '你是一个记忆力很好的助手。' },
    { role: 'user', content: '我的名字是李四。' },
    { role: 'assistant', content: '你好李四！很高兴认识你。' },
    { role: 'user', content: '我的名字是什么？' }
  ];

  const completion = await client.chatCompletion(messages);

  if (completion) {
    console.log('✅ 多轮对话测试成功!');
    console.log(`📝 回复: ${completion.choices[0].message.content}`);
  } else {
    console.log('❌ 多轮对话测试失败');
  }
}

/**
 * 测试流式聊天
 */
async function testStreamingChat() {
  console.log('\n🔄 测试流式聊天...');
  
  const client = new BytedeskOpenAIClient();
  
  const messages = [
    { role: 'user', content: '请给我讲一个短故事。' }
  ];

  const stream = await client.streamChatCompletion(messages, {
    temperature: 0.8
  });

  if (stream) {
    console.log('✅ 流式请求启动成功!');
    console.log('📝 流式回复: ');
    
    try {
      for await (const chunk of stream) {
        const content = chunk.choices[0]?.delta?.content || '';
        if (content) {
          process.stdout.write(content);
        }
      }
      console.log('\n🏁 流式回复完成');
    } catch (error) {
      console.error(`\n❌ 流式处理错误: ${error.message}`);
    }
  } else {
    console.log('❌ 流式请求失败');
  }
}

/**
 * 测试不同的参数配置
 */
async function testDifferentParameters() {
  console.log('\n🔄 测试不同参数配置...');
  
  const client = new BytedeskOpenAIClient();
  
  const messages = [
    { role: 'user', content: '用一个词描述春天。' }
  ];

  console.log('🌡️  高创造性 (temperature=1.5):');
  const highTemp = await client.chatCompletion(messages, {
    temperature: 1.5,
    max_tokens: 50
  });
  if (highTemp) {
    console.log(`   回复: ${highTemp.choices[0].message.content}`);
  }

  console.log('🧊 低创造性 (temperature=0.1):');
  const lowTemp = await client.chatCompletion(messages, {
    temperature: 0.1,
    max_tokens: 50
  });
  if (lowTemp) {
    console.log(`   回复: ${lowTemp.choices[0].message.content}`);
  }
}

/**
 * 测试错误处理
 */
async function testErrorHandling() {
  console.log('\n🔄 测试错误处理...');
  
  const client = new BytedeskOpenAIClient();
  
  // 测试空消息
  const result = await client.chatCompletion([]);
  
  if (result === null) {
    console.log('✅ 空消息错误处理正确');
  } else {
    console.log('⚠️  空消息未正确处理');
  }
}

/**
 * 检查服务连通性
 */
async function checkServiceConnectivity() {
  console.log('🔍 检查服务连通性...');
  
  try {
    const response = await fetch('http://localhost:9003/actuator/health');
    if (response.ok) {
      console.log('✅ Bytedesk 服务连接正常');
      return true;
    } else {
      console.log('⚠️  Bytedesk 服务状态异常');
      return false;
    }
  } catch (error) {
    console.log(`❌ 无法连接到 Bytedesk 服务: ${error.message}`);
    console.log('请确保服务运行在 localhost:9003');
    return false;
  }
}

/**
 * 主函数
 */
async function main() {
  console.log('🚀 Bytedesk OpenAI 兼容接口测试 (Node.js)');
  console.log('='.repeat(50));

  // 检查服务连通性
  const isConnected = await checkServiceConnectivity();
  if (!isConnected) {
    return;
  }

  // 运行测试
  await testBasicChat();
  await testMultiTurnConversation();
  await testStreamingChat();
  await testDifferentParameters();
  await testErrorHandling();

  console.log('\n' + '='.repeat(50));
  console.log('🎉 所有测试完成!');
  console.log('\n💡 集成提示:');
  console.log('1. 只需要修改 baseURL 就可以使用现有的 OpenAI 代码');
  console.log('2. API Key 可以是任意值，Bytedesk 当前不做验证');
  console.log('3. 支持所有标准的 OpenAI Chat Completions 参数');
  console.log('4. 支持流式和非流式响应');
  console.log('5. 完全兼容 OpenAI JavaScript/TypeScript SDK');
}

// 运行主函数
main().catch(console.error);
