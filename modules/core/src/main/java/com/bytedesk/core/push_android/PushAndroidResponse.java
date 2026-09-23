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

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PushAndroidResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String sender;

    private String receiver;

    private String receiverNickname;

    private String deviceId;

    private String attemptUid;

    private String requestUid;

    private String provider;

    private String requestedProvider;

    private Boolean fallback;

    private String fallbackReason;

    private String messageId;

    private String requestId;

    private String messageUid;

    private String threadUid;

    private String content;

    private String type;

    private String status;

    private String channel;

    private Boolean sendSuccess;

    private String sendMessage;

    private String orgName;
}
