<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 11:18:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 11:58:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 安全

## 1. 任务与指令

描述： 攻击者试图通过提示或操控输入来更改 AI 代理的指令或目标。

缓解措施： 执行验证检查和输入过滤，以检测潜在危险的提示，在它们被 AI 代理处理之前拦截。由于这类攻击通常需要频繁与代理交互，限制对话轮数也是防止此类攻击的有效方法。

## 2. 对关键系统的访问

描述： 如果 AI 代理可以访问存储敏感数据的系统和服务，攻击者可能会破坏代理与这些服务之间的通信。这可能是直接攻击，也可能是通过代理间接获取这些系统的信息。

缓解措施： AI 代理应仅在需要时访问相关系统，以防止此类攻击。代理与系统之间的通信应确保安全。实施身份验证和访问控制也是保护信息的有效手段。

## 3. 资源和服务过载

描述： AI 代理可以访问不同的工具和服务来完成任务。攻击者可能利用这一能力，通过代理发送大量请求来攻击这些服务，可能导致系统故障或高昂成本。

缓解措施： 实施策略以限制 AI 代理对服务的请求数量。限制对话轮数和请求次数也是防止此类攻击的另一种方法。

## 4. 知识库污染

描述： 这种攻击并不直接针对 AI 代理，而是针对代理将使用的知识库和其他服务。这可能包括篡改代理完成任务所需的数据或信息，导致代理向用户提供偏颇或意外的响应。

缓解措施： 定期验证 AI 代理在其工作流程中使用的数据。确保对这些数据的访问是安全的，并且只有受信任的人员才能更改，以避免此类攻击。

## 5. 连锁错误

描述： AI 代理依赖多种工具和服务来完成任务。攻击者引发的错误可能导致其他系统的故障，使攻击范围扩大且更难排查。

缓解措施： 一种避免方法是让 AI 代理在受限环境中运行，例如在 Docker 容器中执行任务，以防止直接系统攻击。创建回退机制和重试逻辑，以应对某些系统返回错误的情况，也是防止更大范围系统故障的有效手段。

# 成本管理

以下是一些在生产环境中部署AI代理的成本管理策略：

## 缓存响应

- 识别常见请求和任务，并在它们通过您的代理系统之前提供响应，这是减少类似请求量的好方法。您甚至可以通过更基础的AI模型实现一个流程，来判断某个请求与缓存请求的相似度。

## 使用小型模型

- 小型语言模型（SLM）在某些代理使用场景中表现良好，并能显著降低成本。如前所述，建立一个评估系统来确定并比较其与大型模型的性能是了解SLM在您的使用场景中表现的最佳方式。

## 使用路由模型

- 类似的策略是使用不同规模的模型组合。您可以使用LLM/SLM或无服务器函数，根据请求的复杂性将其路由到最合适的模型。这不仅能降低成本，还能确保复杂任务的性能表现。

## 限制对话轮数

## 参考

- [building-trustworthy-agents](https://github.com/pengjinning/ai-agents-for-beginners/tree/main/translations/zh/06-building-trustworthy-agents)
- [ai-agents](https://github.com/pengjinning/ai-agents-for-beginners/tree/main/translations/zh/10-ai-agents-production)
