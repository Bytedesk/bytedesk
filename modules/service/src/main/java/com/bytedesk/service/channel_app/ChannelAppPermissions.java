/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.channel_app;

import com.bytedesk.core.base.BasePermissions;

public class ChannelAppPermissions extends BasePermissions {

    // 模块前缀
    public static final String CHANNEL_APP_PREFIX = "CHANNEL_APP_";

    // 统一权限（不区分层级）
    public static final String CHANNEL_APP_READ = "CHANNEL_APP_READ";
    public static final String CHANNEL_APP_CREATE = "CHANNEL_APP_CREATE";
    public static final String CHANNEL_APP_UPDATE = "CHANNEL_APP_UPDATE";
    public static final String CHANNEL_APP_DELETE = "CHANNEL_APP_DELETE";
    public static final String CHANNEL_APP_EXPORT = "CHANNEL_APP_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_CHANNEL_APP_READ = "hasAuthority('CHANNEL_APP_READ')";
    public static final String HAS_CHANNEL_APP_CREATE = "hasAuthority('CHANNEL_APP_CREATE')";
    public static final String HAS_CHANNEL_APP_UPDATE = "hasAuthority('CHANNEL_APP_UPDATE')";
    public static final String HAS_CHANNEL_APP_DELETE = "hasAuthority('CHANNEL_APP_DELETE')";
    public static final String HAS_CHANNEL_APP_EXPORT = "hasAuthority('CHANNEL_APP_EXPORT')";

}
