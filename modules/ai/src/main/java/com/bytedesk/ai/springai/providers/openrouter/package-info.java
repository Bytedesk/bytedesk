/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 15:43:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 22:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
/**
 * OpenRouter 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * OpenRouter 是一个统一的 AI 模型接口平台，提供对多种 AI 模型的访问，包括 OpenAI、Anthropic、Google 等提供商的模型。
 * 本包提供了基于 OpenRouter API 的 AI 能力集成，主要包括聊天补全、模型管理、配置验证等功能。
 * 
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterService} - OpenRouter 服务实现，提供对话生成等核心功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterChatService} - OpenRouter 聊天服务实现</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterRestService} - OpenRouter REST 服务，提供模型管理和配置功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterRestController} - OpenRouter REST API 控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterChatController} - OpenRouter 聊天 API 控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenrouterChatConfig} - OpenRouter 服务配置类</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.OpenrouterRequest} - OpenRouter 请求参数封装类</li>
 * </ul>
 * 
 * <p>使用方法：</p>
 * <p>通过在应用配置中设置相应的 OpenRouter API 密钥和 URL 来启用服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值、最大tokens等，可以根据不同机器人配置动态调整模型行为。</p>
 * 
 * <p>支持的功能：</p>
 * <ul>
 *   <li>同步和异步文本生成</li>
 *   <li>流式响应处理（SSE）</li>
 *   <li>WebSocket 实时对话</li>
 *   <li>多模型支持（OpenAI、Anthropic、Google、Meta 等）</li>
 *   <li>模型列表获取和信息查询</li>
 *   <li>配置验证和连接测试</li>
 *   <li>服务健康检查</li>
 *   <li>Token 使用统计</li>
 * </ul>
 * 
 * <p>REST API 端点：</p>
 * <ul>
 *   <li>GET /api/v1/openrouter/ping - 测试连接</li>
 *   <li>GET /api/v1/openrouter/models - 获取模型列表</li>
 *   <li>GET /api/v1/openrouter/model/info - 获取模型信息</li>
 *   <li>POST /api/v1/openrouter/chat - 发送聊天请求</li>
 *   <li>GET /api/v1/openrouter/config - 获取配置信息</li>
 *   <li>POST /api/v1/openrouter/validate - 验证配置</li>
 *   <li>POST /api/v1/openrouter/test - 快速功能测试</li>
 * </ul>
 * 
 * <p>支持的模型类型：</p>
 * <ul>
 *   <li>OpenAI: gpt-4, gpt-3.5-turbo 等</li>
 *   <li>Anthropic: claude-3-sonnet, claude-3-haiku 等</li>
 *   <li>Google: gemini-pro, palm-2-chat-bison 等</li>
 *   <li>Meta: llama-2-70b-chat, llama-2-13b-chat 等</li>
 * </ul>
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.springai.service.BaseSpringAIService
 * @see org.springframework.ai.openai.OpenAiChatModel
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.openrouter;

import org.springframework.lang.NonNullApi;
