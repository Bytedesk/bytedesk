/**
 * SiliconFlow AI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于硅元 API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ生成等功能。
 * 
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.springai.providers.siliconflow.SpringAISiliconFlowService} - SiliconFlow AI 服务实现，提供对话生成、FAQ生成等核心功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.siliconflow.SpringAISiliconFlowConfig} - SiliconFlow AI 服务配置类，包含API密钥和模型设置</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.siliconflow.SpringAISiliconFlowController} - SiliconFlow AI 服务的REST API控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.siliconflow.SiliconFlowApi} - 与SiliconFlow AI API交互的底层接口</li>
 * </ul>
 * 
 * <p>使用方法：</p>
 * <p>通过在应用配置中设置 spring.ai.siliconflow.chat.enabled=true 启用SiliconFlow AI 服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。</p>
 * 
 * <p>支持的功能：</p>
 * <ul>
 *   <li>同步和异步文本生成</li>
 *   <li>流式响应处理（SSE）</li>
 *   <li>自动FAQ对生成</li>
 *   <li>服务健康检查</li>
 * </ul>
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.springai.service.BaseSpringAIService
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.siliconflow;

import org.springframework.lang.NonNullApi;
