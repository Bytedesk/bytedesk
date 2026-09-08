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
package com.bytedesk.ai.robot_tool;

/**
 * 机器人工具意图上下文解析 SPI（社区版接口 / 企业版实现）。
 *
 * <p>工具注册表（ToolEntity/ToolRepository）已迁移至 enterprise/ai 模块
 * （企业版/平台版功能）。本接口保留在 modules/ai 中，供
 * {@code ConvertAiUtils#resolveToolIntentContext} 在社区版下编译与运行：
 * 社区版无实现 bean 时返回 {@link RobotToolIntentContext#empty()}，
 * 机器人 LLM 对话不启用工具意图识别，其余链路不受影响。</p>
 *
 * <p>企业版实现：{@code com.bytedesk.enterprise.ai.tool.RobotToolIntentResolverImpl}
 * （enterprise/ai 模块，bytedesk-enterprise-ai）。</p>
 */
public interface RobotToolIntentResolver {

    /**
     * 解析机器人工具设置为工具意图上下文。
     *
     * @param orgUid        组织 uid（用于解析组织级/平台级工具注册表）
     * @param toolsSettings 机器人工具设置
     * @return 工具意图上下文；未启用或无可用工具时返回空上下文
     */
    RobotToolIntentContext resolve(String orgUid, RobotToolsSettingsEntity toolsSettings);
}
