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

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.push.PushStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Android 设备推送投递记录（对称 ApnsPushEntity）
 *
 * <p>记录一次「供应商投递请求/受理结果」（账号维度，可能覆盖同一账号多台设备），
 * 不是手机厂商最终到达回执：status=SUCCESS 仅表示供应商受理，到达率以供应商控制台为准。
 *
 * <p>阿里云推送为同步 HTTP 调用，受理结果立即可得，记录直接写终态
 * （SUCCESS/ERROR），正常不出现 PENDING 中间态。
 *
 * <p>见 docs/plans/2026-09-23-android-push-record-entity-plan.md
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_push_android", indexes = {
        @Index(name = "idx_push_android_uid", columnList = "uuid"),
        @Index(name = "idx_push_android_receiver", columnList = "receiver"),
        @Index(name = "idx_push_android_provider", columnList = "provider"),
        @Index(name = "idx_push_android_status", columnList = "push_status"),
        @Index(name = "idx_push_android_message_id", columnList = "message_id")
})
public class PushAndroidEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** Push title shown in notification center. */
    private String name;

    /** Sender display name / uid. */
    private String sender;

    /** Receiver user uid (= 供应商推送账号，账号维度可能覆盖多台设备). */
    private String receiver;

    /** 供应商 deviceId（账号推送不指定设备，排障用，可空）. */
    @Column(name = "device_id", length = 128)
    private String deviceId;

    /** 本次供应商尝试的内部关联 UID，每条记录唯一. */
    @Column(name = "attempt_uid", length = 64)
    private String attemptUid;

    /** 调用方可选幂等/链路 UID；为空时只保证 attemptUid 唯一. */
    @Column(name = "request_uid", length = 128)
    private String requestUid;

    /** 实际执行的供应商通道：aliyun 等. */
    @Column(name = "provider", length = 32)
    private String provider;

    /** 路由开始时请求的供应商（区分配置供应商与实际回退供应商）. */
    @Column(name = "requested_provider", length = 32)
    private String requestedProvider;

    /** 是否发生了供应商回退. */
    @Builder.Default
    @Column(name = "is_fallback")
    private Boolean fallback = Boolean.FALSE;

    /** 回退原因或路由诊断摘要. */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String fallbackReason;

    /** 供应商侧消息 id（阿里云 MessageId，到达率排障主键）. */
    @Column(name = "message_id", length = 128)
    private String messageId;

    /** 供应商侧请求 id（阿里云 RequestId，排障用）. */
    @Column(name = "request_id", length = 128)
    private String requestId;

    /** 触发推送的消息 uid（工单通知时复用存 ticketUid，与 iOS 惯例一致）. */
    @Column(name = "message_uid")
    private String messageUid;

    /** Thread uid for the message conversation. */
    @Column(name = "thread_uid")
    private String threadUid;

    /** Serialized message content used in push payload. */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /** Record type / business source. */
    @Builder.Default
    @Column(name = "push_android_type")
    private String type = PushAndroidTypeEnum.MESSAGE.name();

    /** 受理终态（SUCCESS/ERROR），复用 PushStatusEnum. */
    @Builder.Default
    @Column(name = "push_status")
    private String status = PushStatusEnum.SUCCESS.name();

    @Builder.Default
    private String channel = ChannelEnum.ANDROID.name();

    /** 是否被供应商受理（受理≠到达）. */
    @Column(name = "send_success")
    private Boolean sendSuccess;

    /** 失败原因 / 受理摘要. */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String sendMessage;
}
