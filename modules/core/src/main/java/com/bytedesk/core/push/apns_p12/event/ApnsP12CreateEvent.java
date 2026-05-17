/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 10:00:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.apns_p12.event;

import com.bytedesk.core.push.apns_p12.ApnsP12Entity;

/**
 * Event published when a new apns_p12 is created.
 */
public class ApnsP12CreateEvent extends AbstractApnsP12Event {

    private static final long serialVersionUID = 1L;

    public ApnsP12CreateEvent(ApnsP12Entity apns_p12) {
        super(apns_p12, apns_p12);
    }
}
