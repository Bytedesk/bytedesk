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

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移动端推送凭据鉴权下发接口
 *
 * <p>移动端（Android 阿里云推送）登录后拉取 EMAS 应用级 appKey/appSecret 用于 SDK 初始化，
 * 替代编译期 --dart-define 硬编码，支持多服务器部署各自凭据。</p>
 *
 * <p>安全边界：本接口必须鉴权（isAuthenticated），凭据不得经匿名的
 * /config/bytedesk/properties 下发；仅下发 EMAS 应用凭据，不下发服务端
 * OpenAPI 的 accessKey（避免客户端拿到可调云 API 的 RAM 凭据）。</p>
 */
@RestController
@RequestMapping("/api/v1/push")
@AllArgsConstructor
@Tag(name = "Push Client Config", description = "Authenticated push client config delivery APIs")
@Description("Push Client Config Controller - deliver EMAS app credentials to authenticated mobile clients")
public class PushConfigRestController {

    private final PushConfigService pushConfigService;

    @ActionAnnotation(title = "push client config", action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query push client config")
    @Operation(summary = "Query Push Client Config", description = "Return the platform-configured EMAS app credentials (appKey/appSecret) for the authenticated client. Android/iOS platform is enabled only when both appKey and appSecret are configured.")
    @ApiRateLimiter(qps = 1, timeout = 2)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/config")
    public ResponseEntity<?> config() {
        PushClientConfigResponse response = pushConfigService.getConfig();
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 下发 DTO：仅含 EMAS 应用级凭据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushClientConfigResponse {

        /** 推送供应商：当前固定 aliyun */
        private String provider;

        /** 总开关（push.aliyun.enabled，默认 false） */
        private boolean enabled;

        /** Android 平台凭据 */
        private PlatformCredentials android;

        /** iOS 平台凭据（预留） */
        private PlatformCredentials ios;
    }

    /**
     * 平台级凭据：appKey/appSecret 均已配置时 enabled=true
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformCredentials {

        private String appKey;

        private String appSecret;

        /** 该平台凭据是否完整可用 */
        private boolean enabled;
    }
}
