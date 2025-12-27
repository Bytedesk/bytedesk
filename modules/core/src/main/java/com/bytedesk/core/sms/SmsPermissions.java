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
package com.bytedesk.core.sms;

import com.bytedesk.core.base.BasePermissions;

/**
 * 短信权限控制 - 五级权限体系
 */
public class SmsPermissions extends BasePermissions {

    public static final String SMS_PREFIX = "SMS_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String SMS_READ = "SMS_READ";
    public static final String SMS_CREATE = "SMS_CREATE";
    public static final String SMS_UPDATE = "SMS_UPDATE";
    public static final String SMS_DELETE = "SMS_DELETE";
    public static final String SMS_EXPORT = "SMS_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_SMS_READ = "hasAuthority('SMS_READ')";
    public static final String HAS_SMS_CREATE = "hasAuthority('SMS_CREATE')";
    public static final String HAS_SMS_UPDATE = "hasAuthority('SMS_UPDATE')";
    public static final String HAS_SMS_DELETE = "hasAuthority('SMS_DELETE')";
    public static final String HAS_SMS_EXPORT = "hasAuthority('SMS_EXPORT')";
}
