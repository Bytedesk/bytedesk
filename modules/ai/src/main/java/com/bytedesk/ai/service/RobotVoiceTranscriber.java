/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-14 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.service;

import com.bytedesk.ai.robot.RobotProtobuf;

/**
 * 机器人语音转写 SPI（接口下沉到 modules/ai，社区版零依赖）。
 *
 * 实现位于 enterprise/ai（如 DashScopeRobotVoiceTranscriber），RobotService 通过
 * ObjectProvider&lt;RobotVoiceTranscriber&gt; 可选注入：实现存在且机器人开启 asrEnabled
 * 时自动转写；实现不存在（社区版/未授权）时跳过，社区版编译/运行不受影响。
 * 参照 BaseSpringAIService 中 ObjectProvider&lt;AdvisorChainFactory&gt; 的既有模式。
 */
public interface RobotVoiceTranscriber {

    /**
     * 转写音频文件为文本。
     *
     * @param robot  机器人运行时配置（含 llm.asrModel / asrProviderUid / asrApiKeyOverride / asrLanguage）
     * @param orgUid 所属组织（用于组织级 ASR key 解析，见规划 §4.4/§10）
     * @param fileUrl 音频文件 URL（VoiceContent/AudioContent.url）
     * @return 识别文本（非空；由实现方保证 trim）
     * @throws Exception 识别失败（超时/格式不支持/key 缺失等），由调用方决定回退策略
     */
    String transcribe(RobotProtobuf robot, String orgUid, String fileUrl) throws Exception;
}
