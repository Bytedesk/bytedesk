/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-21 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.push.service.PushProviderProperties;
import com.bytedesk.core.push_apns.PushApnsService;
import com.bytedesk.core.push_apns_token.ApnsTokenEntity;
import com.bytedesk.core.push_apns_token.ApnsTokenRestService;

import lombok.extern.slf4j.Slf4j;

/**
 * APNs 直连供应商实现（社区版/私有化部署兜底）
 *
 * <p>封装既有 PushApnsService（Pushy 直连 APNs，按 token 推送 iOS 设备）。
 * Android 设备无 APNs 通道，本实现 isAvailable 仅为 iOS 设备可达性兜底判断。
 */
@Slf4j
@Service
public class PushApnsDeviceService implements PushDeviceService {

    private final PushApnsService apnsPushService;

    private final ApnsTokenRestService apnsTokenRestService;

    public PushApnsDeviceService(PushApnsService apnsPushService,
            ApnsTokenRestService apnsTokenRestService) {
        this.apnsPushService = apnsPushService;
        this.apnsTokenRestService = apnsTokenRestService;
    }

    @Override
    public String getProviderName() {
        return PushProviderProperties.PROVIDER_APNS_DIRECT;
    }

    @Override
    public boolean isAvailable() {
        // iOS 兜底通道按「该用户是否有 APNs token」判断（p12 有效性在推送时校验并落库）
        return true;
    }

    @Override
    public PushDeviceResult pushToUser(String userUid, String title, String body, Map<String, String> extras) {
        if (!StringUtils.hasText(userUid) || !StringUtils.hasText(title)) {
            return PushDeviceResult.failure(getProviderName(), "userUid or title is empty");
        }

        try {
            // 无 token 直接短路，避免空推送（与 PushApnsService 内部判定一致，这里提前返回便于调用方感知）
            List<ApnsTokenEntity> tokens = apnsTokenRestService.findByUserUid(userUid);
            if (tokens == null || tokens.isEmpty()) {
                return PushDeviceResult.failure(getProviderName(), "no apns token bound for user");
            }

            String ticketUid = extras != null ? extras.get("ticketUid") : null;
            String threadUid = extras != null ? extras.get("threadUid") : null;
            // 既有 pushNotificationToUser 接受单个追踪 uid：优先 threadUid（会话邀请），其次 ticketUid（工单）
            String trackingUid = StringUtils.hasText(threadUid) ? threadUid : ticketUid;
            apnsPushService.pushNotificationToUser(userUid, title, body, trackingUid);
            return PushDeviceResult.success(getProviderName(), null, null);
        } catch (Exception e) {
            log.error("ApnsDirect push error, receiver={}, error={}", userUid, e.getMessage(), e);
            return PushDeviceResult.failure(getProviderName(), e.getMessage());
        }
    }
}
