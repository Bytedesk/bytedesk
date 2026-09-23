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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备推送结果（供应商维度）
 *
 * <p>承载阿里云 MessageId/RequestId 或 APNs 侧的受理结果，
 * 与验证码体系的 PushSendResult（短信/邮件验证码）职责分离，避免语义混淆。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushDeviceResult {

    /** 是否被供应商受理（受理≠到达，到达率以供应商控制台为准） */
    private boolean success;

    /** 供应商标识：aliyun / apns-direct */
    private String provider;

    /** Android 投递记录 uid（PushAndroidRecordService 落库后回填，便于调用方关联持久化记录；落库失败时为空） */
    private String recordUid;

    /** 供应商侧消息 id（阿里云 MessageId / Pushy record uid） */
    private String messageId;

    /** 供应商侧请求 id（阿里云 RequestId，排障用） */
    private String requestId;

    /** 失败原因（供应商错误码或内部异常摘要） */
    private String error;

    public static PushDeviceResult success(String provider, String messageId, String requestId) {
        return PushDeviceResult.builder()
                .success(true)
                .provider(provider)
                .messageId(messageId)
                .requestId(requestId)
                .build();
    }

    public static PushDeviceResult failure(String provider, String error) {
        return PushDeviceResult.builder()
                .success(false)
                .provider(provider)
                .error(error)
                .build();
    }
}
