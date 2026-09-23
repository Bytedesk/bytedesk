/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.push_android_device.service.PushDeviceResult;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Android 推送投递记录服务（记录落库唯一收口的执行者）
 *
 * <p>设计要点（对齐 docs/plans/2026-09-23-android-push-record-entity-plan.md §3.3）：
 * <ul>
 * <li>供应商 HTTP 调用不得被数据库事务包住：本服务使用 REQUIRES_NEW 短事务，
 *     在 PushDeviceRouter 调用供应商完成后独立保存记录；</li>
 * <li>落库失败不阻断推送主流程：save 失败仅 WARN，返回入参 result 原样（recordUid 为空）；</li>
 * <li>每次供应商尝试生成唯一 attemptUid；requestUid 由调用方通过 extras 可选传入，
 *     不以 messageId 作为唯一键（失败请求可能没有 messageId）；</li>
 * <li>type 由调用方显式通过 extras 受控键 source=TEST|MESSAGE|TICKET 传入，
 *     不根据其它 key 是否存在隐式推断；缺省 MESSAGE。</li>
 * </ul>
 */
@Slf4j
@Service
@AllArgsConstructor
public class PushAndroidRecordService {

    private final PushAndroidRepository pushAndroidRepository;

    private final UidUtils uidUtils;

    /** extras 受控键：推送来源类型（TEST|MESSAGE|TICKET） */
    public static final String EXTRA_KEY_SOURCE = "source";
    /** extras 受控键：工单 uid（同时写入 messageUid，与 iOS 惯例一致） */
    public static final String EXTRA_KEY_TICKET_UID = "ticketUid";
    /** extras 受控键：会话 uid */
    public static final String EXTRA_KEY_THREAD_UID = "threadUid";
    /** extras 受控键：调用方链路/幂等 UID（可选） */
    public static final String EXTRA_KEY_REQUEST_UID = "requestUid";
    /** extras 受控键：供应商 deviceId（可选，排障用） */
    public static final String EXTRA_KEY_DEVICE_ID = "deviceId";

    /**
     * 保存一次 Android 推送投递记录（终态直写），并把 recordUid 回填到 result。
     *
     * @param requestedProvider 路由开始时请求的供应商
     * @param result            供应商调用结果（success/provider/messageId/requestId/error）
     * @param fallback          是否发生了供应商回退
     * @param fallbackReason    回退原因
     * @return 回填 recordUid 后的 result；落库失败时返回原 result（不抛异常）
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PushDeviceResult record(String userUid, String title, String body, Map<String, String> extras,
            String requestedProvider, PushDeviceResult result, boolean fallback, String fallbackReason) {
        if (result == null) {
            return result;
        }
        try {
            String type = resolveType(extras);
            String ticketUid = getExtra(extras, EXTRA_KEY_TICKET_UID);
            String threadUid = getExtra(extras, EXTRA_KEY_THREAD_UID);

            PushAndroidEntity entity = PushAndroidEntity.builder()
                    .uid(uidUtils.getUid())
                    .name(title)
                    .receiver(userUid)
                    .attemptUid(uidUtils.getUid())
                    .requestUid(getExtra(extras, EXTRA_KEY_REQUEST_UID))
                    .provider(result.getProvider())
                    .requestedProvider(StringUtils.hasText(requestedProvider) ? requestedProvider : result.getProvider())
                    .fallback(fallback)
                    .fallbackReason(fallbackReason)
                    .messageId(result.getMessageId())
                    .requestId(result.getRequestId())
                    // 工单通知时 messageUid 复用存 ticketUid，与 iOS 惯例一致
                    .messageUid(StringUtils.hasText(ticketUid) ? ticketUid : null)
                    .threadUid(threadUid)
                    .content(body)
                    .type(type)
                    .status(result.isSuccess() ? PushStatusEnum.SUCCESS.name() : PushStatusEnum.ERROR.name())
                    .channel(ChannelEnum.ANDROID.name())
                    .sendSuccess(result.isSuccess())
                    .sendMessage(result.isSuccess() ? "accepted by provider" : result.getError())
                    .build();

            PushAndroidEntity saved = pushAndroidRepository.save(entity);
            result.setRecordUid(saved.getUid());
            log.debug("Recorded android push delivery, recordUid={}, receiver={}, provider={}, type={}, messageId={}",
                    saved.getUid(), userUid, result.getProvider(), type, result.getMessageId());
        } catch (Exception e) {
            // 推送本身成功比记录成功重要：落库失败仅 WARN，不阻断主流程
            log.warn("Failed to record android push delivery, receiver={}, provider={}, error={}",
                    userUid, result.getProvider(), e.getMessage());
        }
        return result;
    }

    /** type 必须由调用方显式传入（extras.source），不做 key 存在性推断 */
    private String resolveType(Map<String, String> extras) {
        String source = getExtra(extras, EXTRA_KEY_SOURCE);
        if (PushAndroidTypeEnum.TEST.name().equalsIgnoreCase(source)) {
            return PushAndroidTypeEnum.TEST.name();
        }
        if (PushAndroidTypeEnum.TICKET.name().equalsIgnoreCase(source)) {
            return PushAndroidTypeEnum.TICKET.name();
        }
        if (PushAndroidTypeEnum.MESSAGE.name().equalsIgnoreCase(source)) {
            return PushAndroidTypeEnum.MESSAGE.name();
        }
        return PushAndroidTypeEnum.MESSAGE.name();
    }

    private String getExtra(Map<String, String> extras, String key) {
        if (extras == null) {
            return null;
        }
        String value = extras.get(key);
        return StringUtils.hasText(value) ? value : null;
    }
}
