/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-05-18 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-05-18 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.call_settings;

import com.bytedesk.core.base.BasePermissions;

public class CallSettingsPermissions extends BasePermissions {

    public static final String CALL_SETTINGS_PREFIX = "CALL_SETTINGS_";

    public static final String MODULE_NAME = "CALL_SETTINGS";

    public static final String CALL_SETTINGS_READ = "CALL_SETTINGS_READ";
    public static final String CALL_SETTINGS_CREATE = "CALL_SETTINGS_CREATE";
    public static final String CALL_SETTINGS_UPDATE = "CALL_SETTINGS_UPDATE";
    public static final String CALL_SETTINGS_DELETE = "CALL_SETTINGS_DELETE";
    public static final String CALL_SETTINGS_EXPORT = "CALL_SETTINGS_EXPORT";

    public static final String HAS_CALL_SETTINGS_READ = "hasAuthority('" + CALL_SETTINGS_READ + "')";
    public static final String HAS_CALL_SETTINGS_CREATE = "hasAuthority('" + CALL_SETTINGS_CREATE + "')";
    public static final String HAS_CALL_SETTINGS_UPDATE = "hasAuthority('" + CALL_SETTINGS_UPDATE + "')";
    public static final String HAS_CALL_SETTINGS_DELETE = "hasAuthority('" + CALL_SETTINGS_DELETE + "')";
    public static final String HAS_CALL_SETTINGS_EXPORT = "hasAuthority('" + CALL_SETTINGS_EXPORT + "')";
}