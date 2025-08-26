/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-14 15:43:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 15:43:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
/**
 * OpenAI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于 OpenAI API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ生成等功能。
 * 
 * <p>主要组件：</p&amp;amp;gt;
 * <ul&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenAIService} - OpenAI 服务实现，提供对话生成、FAQ生成等核心功能</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenAIConfig} - OpenAI 服务配置类，包含API密钥和模型设置</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.SpringAIOpenAIController} - OpenAI 服务的REST API控制器</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.openrouter.OpenAIApi} - 与OpenAI API交互的底层接口</li&gt;
 * </ul&gt;
 * 
 * <p>使用方法：</p&gt;
 * <p>通过在应用配置中设置 spring.ai.openrouter.chat.enabled=true 启用OpenAI服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。</p&gt;
 * 
 * <p>支持的功能：</p&gt;
 * <ul&gt;
 *   <li>同步和异步文本生成</li&gt;
 *   <li>流式响应处理（SSE）</li&gt;
 *   <li>自动FAQ对生成</li&gt;
 *   <li>服务健康检查</li&gt;
 * </ul&gt;
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.springai.service.BaseSpringAIService
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.openrouter;

import org.springframework.lang.NonNullApi;
