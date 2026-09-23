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
import com.bytedesk.core.push_android.PushAndroidRecordService;

import lombok.extern.slf4j.Slf4j;

/**
 * 设备推送供应商路由器
 *
 * <p>按 bytedesk.push.provider 选择供应商；所选供应商不可用（凭据缺失）时
 * 回退到 apns-direct（社区版兜底），保证功能开关默认关闭/凭据未配置时不影响存量部署。
 *
 * <p>业务触达点（会话邀请、工单状态变更等）统一依赖本类，不直接注入具体供应商实现。
 */
@Slf4j
@Service
public class PushDeviceRouter {

    private final PushProviderProperties pushProviderProperties;

    private final List<PushDeviceService> providers;

    private final PushAndroidRecordService pushAndroidRecordService;

    public PushDeviceRouter(PushProviderProperties pushProviderProperties,
            List<PushDeviceService> providers,
                       PushAndroidRecordService pushAndroidRecordService) {
        this.pushProviderProperties = pushProviderProperties;
        this.providers = providers;
        this.pushAndroidRecordService = pushAndroidRecordService;
    }

    /** 解析当前生效供应商（不可用时回退 apns-direct） */
    public PushDeviceService resolve() {
        String preferred = pushProviderProperties.getProvider();
        if (StringUtils.hasText(preferred)) {
            for (PushDeviceService provider : providers) {
                if (preferred.equalsIgnoreCase(provider.getProviderName()) && provider.isAvailable()) {
                    return provider;
                }
            }
            if (!PushProviderProperties.PROVIDER_APNS_DIRECT.equalsIgnoreCase(preferred)) {
                log.warn("Push provider '{}' unavailable (credentials missing?), fallback to apns-direct", preferred);
            }
        }
        for (PushDeviceService provider : providers) {
            if (PushProviderProperties.PROVIDER_APNS_DIRECT.equalsIgnoreCase(provider.getProviderName())) {
                return provider;
            }
        }
        // 理论不可达：ApnsDirectDevicePushService 为无条件注册 Bean
        throw new IllegalStateException("No agent device push provider available");
    }

    /**
     * 按当前供应商推送业务通知到指定用户。
     * 失败返回 failure 结果（调用方据此触发改派/降级），不抛异常。
     */
    public PushDeviceResult pushToUser(String userUid, String title, String body, Map<String, String> extras) {
        String preferred = pushProviderProperties.getProvider();
        PushDeviceService provider = resolve();
        // 配置供应商不可用被 resolve() 回退时记录路由诊断信息
        boolean fallback = StringUtils.hasText(preferred)
                && !preferred.equalsIgnoreCase(provider.getProviderName());
        String fallbackReason = fallback
                ? "provider '" + preferred + "' unavailable (credentials missing?), fallback to " + provider.getProviderName()
                : null;
        return pushAndRecord(preferred, provider, userUid, title, body, extras, fallback, fallbackReason);
    }

    /**
     * 按指定供应商推送（管理后台测试推送等场景：测试哪条绑定记录就走该记录声明的通道）。
     *
     * 与 pushToUser 的区别：不做「不可用自动回退 apns-direct」——直接调用指定供应商的
     * pushToUser，由其内部凭据校验返回带具体缺失项指引的 failure（如缺 AppKey 去后台
     * 「推送配置」、缺 AccessKey 去 push.properties），避免误导性错误
     * （例如 Android 绑定回退到 APNs 后报 no apns token bound for user）。
     * 未找到同名供应商实现时才回退当前生效供应商。
     */
    public PushDeviceResult pushToUserViaProvider(String providerName, String userUid, String title, String body, Map<String, String> extras) {
        if (StringUtils.hasText(providerName)) {
            for (PushDeviceService provider : providers) {
                if (providerName.equalsIgnoreCase(provider.getProviderName())) {
                    return pushAndRecord(providerName, provider, userUid, title, body, extras, false, null);
                }
            }
            log.warn("Push provider '{}' not found, fallback to current provider", providerName);
            PushDeviceService resolved = resolve();
            return pushAndRecord(providerName, resolved, userUid, title, body, extras, true,
                    "provider '" + providerName + "' not found, fallback to " + resolved.getProviderName());
        }
        return pushToUser(userUid, title, body, extras);
    }

    /**
     * 调用供应商并统一落 Android 投递记录（记录收口，对称 iOS PushApnsEntity 链路）。
     *
     * 设计要点（docs/plans/2026-09-23-android-push-record-entity-plan.md §3.3）：
     * - 记录实际执行的供应商与回退信息（requestedProvider/provider/fallback/fallbackReason）；
     * - 实际供应商为 apns-direct 时跳过记录（其内部已落 PushApnsEntity，避免双记）；
     * - 记录经 PushAndroidRecordService 以 REQUIRES_NEW 短事务保存，落库失败不阻断推送主流程；
     * - pushToUser 与 pushToUserViaProvider 都经过本方法，测试接口与业务推送审计行为一致。
     */
    private PushDeviceResult pushAndRecord(String requestedProvider, PushDeviceService provider, String userUid,
            String title, String body, Map<String, String> extras, boolean fallback, String fallbackReason) {
        PushDeviceResult result;
        try {
            result = provider.pushToUser(userUid, title, body, extras);
        } catch (Exception e) {
            log.error("Push via provider {} error, receiver={}", provider.getProviderName(), userUid, e);
            result = PushDeviceResult.failure(provider.getProviderName(), e.getMessage());
        }
        // apns-direct 内部已落 PushApnsEntity，跳过 Android 记录避免双记
        if (PushProviderProperties.PROVIDER_APNS_DIRECT.equalsIgnoreCase(provider.getProviderName())) {
            return result;
        }
        return pushAndroidRecordService.record(userUid, title, body, extras, requestedProvider, result, fallback,
                fallbackReason);
    }

    /** 当前配置的供应商标识（观测/展示用） */
    public String getCurrentProviderName() {
        return resolve().getProviderName();
    }
}

