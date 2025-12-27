/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:45:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_ratedown;

public class RatedownSettingsPermissions {

    public static final String TAG_PREFIX = "TAG_";
    //
    public static final String TAG_CREATE = "hasAuthority('" + TAG_PREFIX + "CREATE')";
    public static final String TAG_READ = "hasAuthority('" + TAG_PREFIX + "READ')";
    public static final String TAG_UPDATE = "hasAuthority('" + TAG_PREFIX + "UPDATE')";
    public static final String TAG_DELETE = "hasAuthority('" + TAG_PREFIX + "DELETE')";
    public static final String TAG_EXPORT = "hasAuthority('" + TAG_PREFIX + "EXPORT')";
    // 
    public static final String TAG_ANY = "hasAnyAuthority('" + TAG_PREFIX + "CREATE', '" + TAG_PREFIX + "READ', '" + TAG_PREFIX + "UPDATE', '" + TAG_PREFIX + "EXPORT', '" + TAG_PREFIX + "DELETE')";
}
