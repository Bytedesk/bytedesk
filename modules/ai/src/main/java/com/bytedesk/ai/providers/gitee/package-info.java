/**
 * Gitee AI 服务提供者集成包，包含与 Spring AI 框架集成的所有类和配置。
 * 
 * 本包提供了基于 Gitee API 的 AI 能力集成，主要包括聊天补全、文本生成、FAQ 生成等功能。
 * 
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.providers.gitee.SpringAIGiteeService} - Gitee AI 服务实现，提供对话生成、FAQ 生成等核心功能</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.gitee.SpringAIGiteeConfig} - Gitee AI 服务配置类，包含 API 密钥和模型设置</li>
 *   <li>{@link com.bytedesk.ai.providers.gitee.SpringAIGiteeChatController} - Gitee AI 服务的REST API 控制器</li>
 *   <li>{@link com.bytedesk.ai.springai.providers.gitee.GiteeApi} - 与 Gitee AI API 交互的底层接口</li>
 * </ul>
 * 
 * <p>使用方法：</p>
 * <p>通过在应用配置中设置 spring.ai.gitee.chat.enabled=true 启用 Gitee AI 服务。
 * 服务支持动态配置模型参数，包括温度(temperature)、top-p值等，可以根据不同机器人配置动态调整模型行为。</p>
 * 
 * <p>支持的功能：</p>
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
@NullMarked
package com.bytedesk.ai.providers.gitee;

import org.jspecify.annotations.NullMarked;
