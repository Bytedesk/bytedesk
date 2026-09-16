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
package com.bytedesk.core.system_config.sms;

/**
 * 平台短信配置快照（已解密，仅在后端内存中使用，不回显前端）
 * 用于平台验证码/系统短信发送链路，替代 properties 中的 aliyun.sms.* 静态配置
 */
public record PlatformSmsConfig(
        boolean enabled,
        /** 对齐 SmsProviderTypeEnum: ALIYUN/TENCENT/HUAWEI/AWS/OTHER */
        String providerType,
        String region,
        String accessKeyId,
        /** 已解密 */
        String accessKeySecret,
        /** 厂商 API endpoint/domain，空则由发送侧回退 properties */
        String endpoint,
        /** 验证码短信签名，空则由发送侧回退 properties */
        String signName,
        /** 验证码短信模板编码，空则由发送侧回退 properties */
        String templateCode) {
}
