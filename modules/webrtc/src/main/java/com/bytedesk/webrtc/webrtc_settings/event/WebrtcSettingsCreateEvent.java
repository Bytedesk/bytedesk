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
package com.bytedesk.webrtc.webrtc_settings.event;

import com.bytedesk.webrtc.webrtc_settings.WebrtcSettingsEntity;

/**
 * Event published when a new webrtc_settings is created.
 */
public class WebrtcSettingsCreateEvent extends AbstractWebrtcSettingsEvent {

    private static final long serialVersionUID = 1L;

    public WebrtcSettingsCreateEvent(WebrtcSettingsEntity webrtc_settings) {
        super(webrtc_settings, webrtc_settings);
    }
}
