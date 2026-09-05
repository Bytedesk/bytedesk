/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-03 12:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-03 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 * 
 */
package com.bytedesk.ai.tool.utils;

import java.util.List;

import org.springframework.ai.tool.ToolCallback;

/**
 * 机器人工具回调解析 SPI（社区版接口 / 企业版实现）。
 *
 * <p>工具注册表（ToolEntity 治理）、本地 @Tool 回调、MCP 外部工具等能力已迁移至
 * enterprise/ai 模块（企业版/平台版功能）。本接口保留在 modules/ai 中，
 * 供 {@code BaseSpringAIService#applyRobotToolCallbacks} 在社区版下编译与运行：
 * 社区版无实现 bean 时，机器人 LLM 对话不挂载工具回调，其余链路不受影响。</p>
 *
 * <p>企业版实现：{@code com.bytedesk.ai.tool.utils.RobotToolCallbackResolverImpl}
 * （enterprise/ai 模块，bytedesk-enterprise-ai）。</p>
 */
public interface RobotToolCallbackResolver {

	/**
	 * 根据机器人配置的工具名列表解析可用的 ToolCallback。
	 *
	 * @param requestedToolNames 机器人配置的工具名（可能包含未注册/已禁用的工具）
	 * @return 解析出的工具回调列表；无可用工具时返回空列表
	 */
	List<ToolCallback> resolveToolCallbacks(List<String> requestedToolNames);

	/**
	 * 解析工具回调并应用单轮请求工具调用次数上限（规划 G9）。
	 *
	 * <p>企业版实现会在同一请求的全部回调间共享一个计数器，超出上限的调用
	 * 不再执行并返回终止提示；社区版/默认实现忽略上限，行为与
	 * {@link #resolveToolCallbacks(List)} 一致。</p>
	 *
	 * @param requestedToolNames 机器人配置的工具名
	 * @param maxToolInvocations 单轮请求内工具调用总次数上限；null 或 <=0 表示不限制
	 * @return 解析出的工具回调列表；无可用工具时返回空列表
	 */
	default List<ToolCallback> resolveToolCallbacks(List<String> requestedToolNames, Integer maxToolInvocations) {
		return resolveToolCallbacks(requestedToolNames);
	}
}
