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
 * 平台邮件配置快照（已解密，仅在后端内存中使用，不回显前端）
 * 用于平台验证码/系统邮件发送链路，替代 properties 中的 spring.mail.* / aliyun.* 静态配置
 */
public record PlatformEmailConfig(
        boolean enabled,
        /** 发件邮箱地址（同时作为 SMTP 登录用户名） */
        String emailAddress,
        /** SMTP 登录密码（已解密） */
        String password,
        String smtpHost,
        Integer smtpPort,
        Boolean smtpSslEnabled,
        /** 发件人显示名称 */
        String displayName,
        /** 平台绑定的验证码邮件模板UID；为空时发送侧回退默认 EMAIL_VERIFY_CODE 模板 */
        String verifyCodeTemplateUid) {
}
