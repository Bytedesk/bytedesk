/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-22 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

import com.bytedesk.core.base.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PushDeviceResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /** 推送供应商：aliyun */
    private String provider;

    /** 供应商侧设备标识（阿里云 deviceId） */
    private String deviceId;

    /** 绑定账号 = userUid */
    private String account;

    /** 绑定类型：ANDROID_ALIYUN */
    private String type;

    /** 设备平台：ANDROID / IOS */
    private String device;

    /** 客户端渠道：FLUTTER 等 */
    private String channel;
}
