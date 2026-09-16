/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-15 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.system_config.email;

/**
 * 平台邮件配置 SPI（模块依赖反转）
 *
 * 接口定义在 modules/core，由 enterprise/core 提供实现（读取 SettingsEntity(type=EMAIL) 平台绑定）。
 * 消费方（EmailSendService 等）通过 ObjectProvider#getIfAvailable() 获取实现：
 * - 无实现（社区版未聚合 enterprise 模块）时返回 null，自动回退 properties 配置；
 * - 有实现但平台配置未启用/未绑定邮箱时同样返回 null，回退 properties。
 *
 * 开关语义：emailEnabled = "是否用平台绑定覆盖 properties 默认值"，不是"是否禁用邮件发送"。
 * 关闭或未配置 = 回退 properties，绝不出现"配置关闭导致验证码发不出去"的回归。
 */
public interface PlatformEmailConfigProvider {

    /**
     * 获取平台邮件配置快照（已解密）
     *
     * @return 配置快照；未配置/未启用/配置无效时返回 null（消费方回退 properties）
     */
    PlatformEmailConfig getPlatformEmailConfig();
}
