/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:42:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:48:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class PushRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String sender;

    private String receiver;

    private String ip;

    // according to ip address
    private String ipLocation;

    // 用于扫码登录
    private Boolean forceRefresh; // 强制刷新
    private String deviceUid; // 设备唯一标识

    // @Builder.Default
    // private String status = PushStatusEnum.PENDING.name();

    // @Builder.Default
    // private String platform = PlatformEnum.BYTEDESK.name();

    // 修改邮箱、手机号需要记录用户uid和组织uid
    private String name;
}
