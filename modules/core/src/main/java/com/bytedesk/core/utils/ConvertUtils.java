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

@UtilityClass
public class ConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static BytedeskPropertiesResponse convertToBytedeskPropertiesResponse(
            BytedeskProperties bytedeskProperties) {
        // return modelMapper.map(bytedeskProperties, BytedeskPropertiesResponse.class);
        BytedeskPropertiesResponse response = getModelMapper().map(bytedeskProperties, BytedeskPropertiesResponse.class);
        
        // 先带出配置中的许可证值，控制器会在返回前替换为前端专用加密摘要。
        response.setLicenseKey(bytedeskProperties.getLicenseKey());
        
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
            response.getCustom().setEnabled(bytedeskProperties.getCustom().getEnabled());
            response.getCustom().setName(bytedeskProperties.getCustom().getName());
            response.getCustom().setLogo(bytedeskProperties.getCustom().getLogo());
            response.getCustom().setFavicon(bytedeskProperties.getCustom().getFavicon());
            response.getCustom().setDescription(bytedeskProperties.getCustom().getDescription());
            response.getCustom().setPrivacyPolicyUrl(bytedeskProperties.getCustom().getPrivacyPolicyUrl());
            response.getCustom().setTermsOfServiceUrl(bytedeskProperties.getCustom().getTermsOfServiceUrl());
            // 
            response.getCustom().setAllowRegister(bytedeskProperties.getCustom().getAllowRegister());
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
