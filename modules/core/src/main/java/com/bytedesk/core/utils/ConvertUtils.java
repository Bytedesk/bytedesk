/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 11:55:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.modelmapper.ModelMapper;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.config.properties.BytedeskPropertiesResponse;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadEntity;
import com.bytedesk.core.message_unread.MessageUnreadResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadConvertUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadResponse;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static BytedeskPropertiesResponse convertToBytedeskPropertiesResponse(
            BytedeskProperties bytedeskProperties) {
        // return modelMapper.map(bytedeskProperties, BytedeskPropertiesResponse.class);
        BytedeskPropertiesResponse response = getModelMapper().map(bytedeskProperties, BytedeskPropertiesResponse.class);
        
        // 注意：licenseKey 不复制到响应，避免泄露给前端（控制器仅下发验签后的明文摘要）
        
        // 明确设置Custom所有字段的值，确保从配置中获取
        if (bytedeskProperties.getCustom() != null) {
            response.getCustom().setUploadApiUrl(bytedeskProperties.getCustom().getUploadApiUrl());
            response.getCustom().setMqttWebsocketUrl(bytedeskProperties.getCustom().getMqttWebsocketUrl());
            response.getCustom().setShowRightCornerChat(bytedeskProperties.getCustom().getShowRightCornerChat());
            response.getCustom().setLoginUsernameEnable(bytedeskProperties.getCustom().getLoginUsernameEnable());
            response.getCustom().setLoginMaxRetryCount(bytedeskProperties.getCustom().getLoginMaxRetryCount());
            response.getCustom().setLoginMaxRetryLockTime(bytedeskProperties.getCustom().getLoginMaxRetryLockTime());
            response.getCustom().setLoginMobileEnable(bytedeskProperties.getCustom().getLoginMobileEnable());
            // response.getCustom().setLoginEmailEnable(bytedeskProperties.getCustom().getLoginEmailEnable());
            response.getCustom().setLoginScanEnable(bytedeskProperties.getCustom().getLoginScanEnable());
            response.getCustom().setDocUrlShow(bytedeskProperties.getCustom().getDocUrlShow());
            response.getCustom().setDocUrl(bytedeskProperties.getCustom().getDocUrl());
            response.getCustom().setHelpDocButtonEnabled(bytedeskProperties.getCustom().getHelpDocButtonEnabled());
            response.getCustom().setEnabled(bytedeskProperties.getCustom().getEnabled());
            response.getCustom().setName(bytedeskProperties.getCustom().getName());
            response.getCustom().setLogo(bytedeskProperties.getCustom().getLogo());
            response.getCustom().setFavicon(bytedeskProperties.getCustom().getFavicon());
            response.getCustom().setDescription(bytedeskProperties.getCustom().getDescription());
            response.getCustom().setPrivacyPolicyUrl(bytedeskProperties.getCustom().getPrivacyPolicyUrl());
            response.getCustom().setTermsOfServiceUrl(bytedeskProperties.getCustom().getTermsOfServiceUrl());
            // 
            response.getCustom().setShowRegisterButton(bytedeskProperties.getCustom().getShowRegisterButton());
            response.getCustom().setAutoRegisterOnLogin(bytedeskProperties.getCustom().getAutoRegisterOnLogin());
            response.getCustom().setForceValidateMobile(bytedeskProperties.getCustom().getForceValidateMobile());
            response.getCustom().setForceValidateEmail(bytedeskProperties.getCustom().getForceValidateEmail());
                response.getCustom().setForceVisitorAuth(bytedeskProperties.getCustom().getForceVisitorAuth());
                response.getCustom().setWechatMpSubscribePromptEnabled(
                    bytedeskProperties.getCustom().getWechatMpSubscribePromptEnabled());
                response.getCustom().setWechatMpSubscribePromptAppId(
                    bytedeskProperties.getCustom().getWechatMpSubscribePromptAppId());
                response.getCustom().setDefaultLlmPrompt(
                    bytedeskProperties.getCustom().getDefaultLlmPrompt());

            // 合并系统全局配置 DB 覆盖值（/super/system-config 后台保存的运行时配置）：
            // DB 值非空才覆盖静态默认值；service 缺失/异常时静默回退静态值，不影响下发链路。
            applySystemConfigOverrides(response);
        }

        // 明确设置Organization部分字段，确保从配置中获取
        if (bytedeskProperties.getOrganization() != null) {
            if (response.getOrganization() == null) {
                response.setOrganization(new BytedeskPropertiesResponse.Organization());
            }
            response.getOrganization().setName(bytedeskProperties.getOrganization().getName());
            response.getOrganization().setCode(bytedeskProperties.getOrganization().getCode());
            response.getOrganization().setAllowCreateOrg(bytedeskProperties.getOrganization().getAllowCreateOrg());
            response.getOrganization().setAllowJoinOrg(bytedeskProperties.getOrganization().getAllowJoinOrg());
            response.getOrganization().setDefaultVipLevel(bytedeskProperties.getOrganization().getDefaultVipLevel());
            response.getOrganization().setDefaultVipDays(bytedeskProperties.getOrganization().getDefaultVipDays());
            response.getOrganization().setDefaultMaxMembers(bytedeskProperties.getOrganization().getDefaultMaxMembers());
            response.getOrganization().setDefaultMaxAgents(bytedeskProperties.getOrganization().getDefaultMaxAgents());
            response.getOrganization().setDefaultMaxWorkgroups(bytedeskProperties.getOrganization().getDefaultMaxWorkgroups());
        }

        if (bytedeskProperties.getCall() != null && bytedeskProperties.getCall().getFreeswitch() != null) {
            if (response.getCall() == null) {
                response.setCall(new BytedeskPropertiesResponse.Call());
            }
            if (response.getCall().getFreeswitch() == null) {
                response.getCall().setFreeswitch(new BytedeskPropertiesResponse.Freeswitch());
            }
            response.getCall().getFreeswitch()
                    .setRecordingsBaseUrl(bytedeskProperties.getCall().getFreeswitch().getRecordingsBaseUrl());
        }

        return response;
    }

    /**
     * 合并系统全局配置（SystemConfig）DB 覆盖值到 properties 响应。
     *
     * 优先级：DB 覆盖值（非空）> properties/compose 静态默认值。
     * 本方法在静态工具类中被调用（ApplicationContextHolder 延迟取 bean），
     * 任何异常（bean 缺失/缓存不可用/DB 异常）都静默降级为静态值，
     * 保证 /config/bytedesk/properties（匿名高频接口）不受影响。
     */
    private static void applySystemConfigOverrides(BytedeskPropertiesResponse response) {
        try {
            if (!ApplicationContextHolder.isInitialized() || response.getCustom() == null) {
                return;
            }
            com.bytedesk.core.system_config.SystemConfigRestService systemConfigRestService =
                    ApplicationContextHolder.getBean(com.bytedesk.core.system_config.SystemConfigRestService.class);
            Map<String, String> overrides = systemConfigRestService
                    .getOverrideValues(com.bytedesk.core.system_config.SystemConfigConsts.PLATFORM_CONFIG_ORG_UID);
            if (overrides == null || overrides.isEmpty()) {
                return;
            }
            BytedeskPropertiesResponse.Custom custom = response.getCustom();
            // custom.enabled（BOOLEAN）
            String enabled = overrides.get(com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_ENABLED);
            if (StringUtils.hasText(enabled)) {
                custom.setEnabled(Boolean.parseBoolean(enabled));
            }
            // 字符串类品牌字段
            String name = overrides.get(com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_NAME);
            if (StringUtils.hasText(name)) {
                custom.setName(name);
            }
            String logo = overrides.get(com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_LOGO);
            if (StringUtils.hasText(logo)) {
                custom.setLogo(logo);
            }
            String favicon = overrides.get(com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_FAVICON);
            if (StringUtils.hasText(favicon)) {
                custom.setFavicon(favicon);
            }
            String description = overrides.get(com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_DESCRIPTION);
            if (StringUtils.hasText(description)) {
                custom.setDescription(description);
            }
            // 协议链接
            String privacyPolicyUrl = overrides.get(
                    com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_PRIVACY_POLICY_URL);
            if (StringUtils.hasText(privacyPolicyUrl)) {
                custom.setPrivacyPolicyUrl(privacyPolicyUrl);
            }
            String termsOfServiceUrl = overrides.get(
                    com.bytedesk.core.system_config.SystemConfigConsts.KEY_CUSTOM_TERMS_OF_SERVICE_URL);
            if (StringUtils.hasText(termsOfServiceUrl)) {
                custom.setTermsOfServiceUrl(termsOfServiceUrl);
            }
        } catch (Exception e) {
            // 静默降级：使用静态默认值，不打断下发链路
            log.warn("Apply system config overrides failed, fallback to static defaults: {}", e.getMessage());
        }
    }

    public static UploadResponse convertToUploadResponse(UploadEntity entity) {
        UploadResponse uploadResponse = getModelMapper().map(entity, UploadResponse.class);
        // 上一行没有自动初始化isLlm字段，所以这里需要手动设置
        // uploadResponse.setIsLlm(entity.isLlm());
        return uploadResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageEntity message) {

        MessageResponse messageResponse = getModelMapper().map(message, MessageResponse.class);
        //
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                messageResponse.setUser(user);
            }
        }

        // thread
        if (message.getThread() != null) {
            ThreadResponse thread = ThreadConvertUtils.convertToThreadResponse(message.getThread());
            messageResponse.setThread(thread);
        }

        return messageResponse;
    }
    
    public static MessageUnreadResponse convertToMessageUnreadResponse(MessageUnreadEntity message) {

        MessageUnreadResponse messageResponse = getModelMapper().map(message, MessageUnreadResponse.class);
        //
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                messageResponse.setUser(user);
            }
        }

        // thread
        if (message.getThread() != null) {
            ThreadResponse thread = ThreadConvertUtils.convertToThreadResponse(message.getThread());
            messageResponse.setThread(thread);
        }

        return messageResponse;
    }
    
}
