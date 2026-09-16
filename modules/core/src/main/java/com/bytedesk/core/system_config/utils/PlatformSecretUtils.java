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
package com.bytedesk.core.system_config.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 供应商敏感信息（邮件密码 / 短信 AccessKeySecret 等）加密工具
 *
 * 与 channels/wechat 中 aibot secret 的存储约定一致：
 * - 入库：明文 → ENC(密文)；已是 ENC(...) 原样保留
 * - 读取：ENC(...) → 解密；明文直接返回（兼容历史数据）
 *
 * StringEncryptor 实例由 starter 的 jasypt-spring-boot-starter 提供，
 * 通过 {@link PlatformSecretConfig} 在启动时注入静态持有器；
 * 未初始化（如单模块测试环境）时明文透传，不阻断主流程。
 */
@Slf4j
public final class PlatformSecretUtils {

    private static final String ENC_PREFIX = "ENC(";
    private static final String ENC_SUFFIX = ")";

    /** 敏感信息回显掩码，前端回传该值时后端应保留原值不覆盖 */
    public static final String SECRET_MASK = "***";

    private static volatile StringEncryptor encryptor;

    private PlatformSecretUtils() {
    }

    public static void init(StringEncryptor stringEncryptor) {
        encryptor = stringEncryptor;
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENC_PREFIX) && value.endsWith(ENC_SUFFIX) && value.length() > ENC_PREFIX.length();
    }

    /**
     * 明文 → ENC(密文)；空值/已是密文原样返回；加密器不可用时明文透传并告警
     */
    public static String encrypt(String plain) {
        if (!StringUtils.hasText(plain) || isEncrypted(plain)) {
            return plain;
        }
        StringEncryptor current = encryptor;
        if (current == null) {
            log.warn("StringEncryptor 未初始化，敏感信息将以明文存储");
            return plain;
        }
        try {
            return ENC_PREFIX + current.encrypt(plain) + ENC_SUFFIX;
        } catch (Exception e) {
            log.error("敏感信息加密失败，将以明文存储: {}", e.getMessage());
            return plain;
        }
    }

    /**
     * ENC(...) → 明文；明文直接返回；解密失败返回原值（由调用方以认证失败形式暴露）
     */
    public static String decrypt(String stored) {
        if (!isEncrypted(stored)) {
            return stored;
        }
        StringEncryptor current = encryptor;
        if (current == null) {
            log.error("StringEncryptor 未初始化，无法解密 ENC(...) 敏感信息");
            return stored;
        }
        try {
            return current.decrypt(stored.substring(ENC_PREFIX.length(), stored.length() - ENC_SUFFIX.length()));
        } catch (Exception e) {
            log.error("敏感信息解密失败，返回原值: {}", e.getMessage());
            return stored;
        }
    }
}
