/**
 * ByteDesk 点踩设置管理包
 * 
 * 本包提供客服系统中点踩功能的相关设置与管理，包括：
 * 
 * 1. 点踩选项管理: 提供预设标签的CRUD操作
 * 2. 点踩功能控制: 包括启用/禁用、标签选择限制等配置
 * 3. 用户反馈收集: 支持结构化标签和自由文本反馈
 * 4. 后续处理机制: 包括客服跟进、质检集成等
 * 
 * 核心功能包括:
 * - 自定义点踩标签集合，支持针对不同场景设置不同标签
 * - 控制每次点踩可选择的最大标签数量
 * - 支持用户输入自定义文本反馈
 * - 点踩后的自动感谢消息
 * - 点踩会话的质检标记
 * - 支持点踩后自动转人工客服
 * - 点踩反馈的后续跟进管理
 * 
 * 此包的设计遵循客服行业最佳实践，旨在有效收集和处理负面反馈，
 * 促进服务质量持续改进。
 */
@NonNullApi
package com.bytedesk.kbase.settings_ratedown;

import org.springframework.lang.NonNullApi;
