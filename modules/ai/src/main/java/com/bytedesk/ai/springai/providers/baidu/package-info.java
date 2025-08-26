/**
 * 百 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduChatConfig} - 百度 AI 服务配置类，包含API密钥和模型设置</li> AI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于百度文心大模型 API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ生成等功能。
 * 
 * <p>主要组件：</p&gt;
 * <ul&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduService} - 百度 AI 服务实现，提供对话生成、FAQ生成等核心功能</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduConfig} - 百度 AI 服务配置类，包含API密钥和模型设置</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduChatController} - 百度 AI 服务的REST API控制器</li&gt;
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.BaiduApi} - 与百度 AI API交互的底层接口</li&gt;
 * </ul&gt;
 * 
 * <p>使用方法：</p&gt;
 * <p>通过在应用配置中设置 spring.ai.baidu.chat.enabled=true 启用百度 AI 服务。
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
package com.bytedesk.ai.springai.providers.baidu;

import org.springframework.lang.NonNullApi;
