/**
 * Ollama AI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于 Ollama API 的本地 AI 能力集成，主要包括聊天补全、文本生成、FAQ生成等功能。
 * 
 * <p>主要组件：</p&gt;
 * <ul&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaService} - Ollama AI 服务实现，提供对话生成、FAQ生成等核心功能</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaChatConfig} - Ollama AI 聊天服务配置类，包含聊天API接入点和模型设置</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaEmbeddingConfig} - Ollama AI 嵌入服务配置类，包含嵌入API接入点和模型设置</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.ollama.SpringAIOllamaChatController} - Ollama AI 服务的REST API控制器</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.ollama.OllamaApi} - 与Ollama API交互的底层接口</li&gt;
 * </ul&gt;
 * 
 * <p>使用方法：</p&gt;
 * <p>通过在应用配置中设置 spring.ai.ollama.chat.enabled=true 启用Ollama AI 服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。</p&gt;
 * 
 * <p>支持的功能：</p&gt;
 * <ul&gt;
 *   <li>同步和异步文本生成</li&gt;
 *   <li>流式响应处理（SSE）</li&gt;
 *   <li>自动FAQ对生成</li&gt;
 *   <li>服务健康检查</li&gt;
 *   <li>本地模型部署与调用</li&gt;
 * </ul&gt;
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.service.BaseSpringAIService
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.ollama;

import org.springframework.lang.NonNullApi;
