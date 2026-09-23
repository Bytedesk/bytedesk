/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-21 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms of the license and automatically terminates your rights under use of the license.
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_config;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.push_config.PushConfigRestController.PlatformCredentials;
import com.bytedesk.core.push_config.PushConfigRestController.PushClientConfigResponse;
import com.bytedesk.core.system_config.SystemConfigConsts;
import com.bytedesk.core.system_config.SystemConfigRestService;
import com.bytedesk.core.system_config.utils.PlatformSecretUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 移动端推送凭据下发服务
 *
 * <p>读取平台级 SystemConfig 覆盖值（走 Redis 缓存），appSecret 解密后明文下发
 * （客户端 initPush 需要明文）。任何情况下不打敏感值日志。</p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class PushConfigService {

    private static final String PROVIDER_ALIYUN = "aliyun";

    private final SystemConfigRestService systemConfigRestService;

    /**
     * 组装移动端推送凭据。
     *
     * <p>未配置（appKey/appSecret 任一为空）时对应平台 enabled=false，
     * 移动端跳过该平台 initPush；总开关 enabled=false 时移动端跳过整个推送初始化。</p>
     */
    public PushClientConfigResponse getConfig() {
        Map<String, String> overrides = systemConfigRestService
                .getOverrideValues(SystemConfigConsts.PLATFORM_CONFIG_ORG_UID);

        boolean enabled = "true".equalsIgnoreCase(overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_ENABLED));

        PlatformCredentials android = buildCredentials(
                overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_ANDROID_APP_KEY),
                overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_ANDROID_APP_SECRET));
        PlatformCredentials ios = buildCredentials(
                overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_IOS_APP_KEY),
                overrides.get(SystemConfigConsts.KEY_PUSH_ALIYUN_IOS_APP_SECRET));

        return PushClientConfigResponse.builder()
                .provider(PROVIDER_ALIYUN)
                .enabled(enabled)
                .android(android)
                .ios(ios)
                .build();
    }

    /**
     * appKey 明文直用；appSecret ENC(...) 解密为明文（客户端 SDK 初始化需要）。
     * 任一为空 → enabled=false（未配置降级，不报错）。
     */
    private PlatformCredentials buildCredentials(String appKey, String storedSecret) {
        boolean ready = StringUtils.hasText(appKey) && StringUtils.hasText(storedSecret);
        String appSecret = ready ? PlatformSecretUtils.decrypt(storedSecret) : null;
        // 解密失败时 decrypt 返回原密文，视为凭据不可用而非下发错误数据
        boolean usable = ready && !PlatformSecretUtils.isEncrypted(appSecret);
        return PlatformCredentials.builder()
                .appKey(usable ? appKey : null)
                .appSecret(usable ? appSecret : null)
                .enabled(usable)
                .build();
    }
}
