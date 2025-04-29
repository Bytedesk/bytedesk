/**
 * 自定义 AI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于自定义大语言模型 API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ生成等功能。
 * 适用于需要自行部署或集成非官方支持的 AI 模型服务。
 * 
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.springai.providers.custom.SpringAICustomService} - 自定义 AI 服务实现，提供对话生成、FAQ生成等核心功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.custom.SpringAICustomConfig} - 自定义 AI 服务配置类，包含API端点、密钥和模型设置</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.custom.SpringAICustomController} - 自定义 AI 服务的REST API控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.custom.CustomApi} - 与自定义 AI API交互的底层接口</li>
 * </ul>
 * 
 * <p>使用方法：</p>
 * <p>通过在应用配置中设置 spring.ai.custom.chat.enabled=true 启用自定义 AI 服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。
 * 同时需配置 spring.ai.custom.base-url 指向自定义模型服务的地址。</p>
 * 
 * <p>支持的功能：</p>
 * <ul>
 *   <li>同步和异步文本生成</li>
 *   <li>流式响应处理（SSE）</li>
 *   <li>自动FAQ对生成</li>
 *   <li>服务健康检查</li>
 *   <li>自定义模型参数配置</li>
 *   <li>多模型切换支持</li>
 * </ul>
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.springai.service.BaseSpringAIService
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.custom;

import org.springframework.lang.NonNullApi;
