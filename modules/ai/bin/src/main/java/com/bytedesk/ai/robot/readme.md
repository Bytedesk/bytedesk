<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-27 22:19:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-10 14:25:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# prompt

## 提示最佳实践模板

```md
在每个prompt前面添加下面这句话，能够显著提高模型输出质量。
“Take a deep breath and work on this step by step.”
“深吸一口气，一步一步地完成这个任务。”

## 1. 任务描述
- **描述**：清晰地描述您希望模型完成的任务。
- **示例**：`请生成一个关于可持续发展的简短介绍。`

## 2. 输入格式
- **格式**：指定输入的格式，例如文本、问题、指令等。
- **示例**：`输入应为一段文本或一个问题。`

## 3. 输出期望
- **期望**：说明您期望模型返回的结果类型，例如简短回答、详细解释、列表等。
- **示例**：`期望输出为一个简短的段落，包含关键点。`

## 4. 关键要素
- **要素**：列出在输出中应包含的关键要素或信息。
- **示例**：
  - 主题
  - 主要观点
  - 相关数据或事实

## 5. 示例
- **输入示例**：提供一个或多个示例，展示输入和期望的输出。
  - **输入**：`请解释什么是机器学习。`
  - **输出**：`机器学习是人工智能的一个子领域，涉及使用算法和统计模型使计算机系统能够执行特定任务，而无需使用明确的指令。`

## 6. 反馈机制
- **说明**：提供反馈机制，让用户能够根据模型的输出调整提示。
- **示例**：`如果输出不符合预期，请尝试更具体的描述或提供更多上下文信息。`

## 7. 多样性
- **建议**：鼓励用户尝试不同的提示，以探索模型的能力和局限性。
- **示例**：`尝试不同的提问方式，例如“请告诉我...”或“你能解释一下...吗？”`
```

## RAG智能客服提示最佳实践实例

```md
## 1. 任务描述
- **描述**：根据用户的查询，从知识库中检索相关信息，并生成详细的回答。
- **示例**：`请根据用户的查询，从知识库中检索相关信息，并生成一个详细的回答，确保信息准确且易于理解。`

## 2. 输入格式
- **格式**：文本
- **示例**：`输入应为用户的查询文本，例如“如何重置我的密码？”`

## 3. 输出期望
- **期望**：期望输出为一段清晰的文本，包含解决方案和相关步骤。
- **示例**：`输出应包括重置密码的步骤、可能遇到的问题及解决方案，以及联系支持的方式。`

## 4. 关键要素
- **要素**：
  - 重置密码的步骤
  - 可能遇到的常见问题（如邮箱未收到重置链接）
  - 联系客服的方式（如电话、邮件等）

## 5. 示例
- **输入示例**：
  - `我忘记了我的密码，如何重置？`
- **输出示例**：
  - `要重置您的密码，请按照以下步骤操作：1. 访问登录页面，点击“忘记密码？”链接。2. 输入您的注册邮箱地址并提交。3. 检查您的邮箱，您将收到一封重置密码的邮件。4. 点击邮件中的链接，设置新密码。如果您没有收到邮件，请检查垃圾邮件文件夹或联系支持团队，电话：400-123-4567。`

## 6. 反馈机制
- **说明**：如果输出不符合预期，请尝试更具体的描述或提供更多上下文信息。
- **示例**：`如果用户的查询不够明确，可以引导用户提供更多信息，例如他们的用户名或注册邮箱。`

## 7. 多样性
- **建议**：鼓励用户尝试不同的提问方式，以探索RAG智能客服的能力。
- **示例**：`可以尝试提问：“我如何更改我的账户信息？”或“如何查看我的订单历史？”`
```

## 代码实现

```ts
const generateResponse = (userQuery: string, chatHistory: string[], searchResults: string[]) => {
  // RAG智能客服提示模板
  const promptTemplate = `
    任务描述：根据用户的查询，从知识库中检索相关信息，并结合历史聊天记录生成详细的回答。
    
    用户查询: ${userQuery}
    
    历史聊天记录:
    ${chatHistory.join('\n')}
    
    搜索结果:
    ${searchResults.join('\n')}
    
    请根据以上信息生成一个详细的回答，确保信息准确且易于理解。
  `;

  // 调用模型生成响应
  const response = callModel(promptTemplate);
  return response;
};

// 示例调用
const userQuery = "如何重置我的密码？";
const chatHistory = [
  "用户：我忘记了我的密码。",
  "客服：请提供您的注册邮箱。"
];
const searchResults = [
  "重置密码步骤：1. 访问登录页面，2. 点击“忘记密码？”链接，3. 输入您的注册邮箱并提交，4. 检查您的邮箱以获取重置链接。"
];

const response = generateResponse(userQuery, chatHistory, searchResults);
console.log(response);
```
