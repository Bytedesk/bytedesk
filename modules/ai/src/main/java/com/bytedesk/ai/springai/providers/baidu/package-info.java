/**
 * Baidu AI provider integration package for Spring AI-based model capabilities.
 * 百度 AI 提供方集成包，负责 Spring AI 相关模型能力接入与配置。
 * 
 * 本包提供了基于百度文心大模型 API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ 生成等功能。
 * 
 * <p>
 * 主要组件：
 * </p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduService} -
 * 百度 AI 服务实现，提供对话生成、FAQ 生成等核心功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduConfig} - 百度
 * AI 服务配置类，包含 API 密钥和模型设置</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.SpringAIBaiduChatController}
 * - 百度 AI 服务的 REST API 控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.baidu.BaiduApi} - 与百度 AI API
 * 交互的底层接口</li>
 * </ul>
 * 
 * <p>
 * 使用方法：
 * </p>
 * <p>
 * 通过在应用配置中设置 spring.ai.baidu.chat.enabled=true 启用百度 AI 服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。
 * </p>
 * 
 * <p>
 * 支持的功能：
 * </p>
 * <ul>
 *   <li>同步和异步文本生成</li>
 *   <li>流式响应处理（SSE）</li>
 *   <li>自动 FAQ 对生成</li>
 *   <li>服务健康检查</li>
 * </ul>
 *
 * @author bytedesk.com
 * @see com.bytedesk.ai.service.BaseSpringAIService
 * @since 1.0.0
 */
@NonNullApi
package com.bytedesk.ai.springai.providers.baidu;

import org.springframework.lang.NonNullApi;
