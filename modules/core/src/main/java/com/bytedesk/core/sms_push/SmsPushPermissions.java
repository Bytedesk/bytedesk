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
package com.bytedesk.core.sms_push;

import com.bytedesk.core.base.BasePermissions;

/**
 * 短信权限控制 - 五级权限体系
 */
public class SmsPushPermissions extends BasePermissions {

    public static final String SMS_PUSH_PREFIX = "SMS_PUSH_";

    public static final String MODULE_NAME = "SMS_PUSH";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String SMS_PUSH_READ = "SMS_PUSH_READ";
    public static final String SMS_PUSH_CREATE = "SMS_PUSH_CREATE";
    public static final String SMS_PUSH_UPDATE = "SMS_PUSH_UPDATE";
    public static final String SMS_PUSH_DELETE = "SMS_PUSH_DELETE";
    public static final String SMS_PUSH_EXPORT = "SMS_PUSH_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_SMS_PUSH_READ = "hasAuthority('" + SMS_PUSH_READ + "')";
    public static final String HAS_SMS_PUSH_CREATE = "hasAuthority('" + SMS_PUSH_CREATE + "')";
    public static final String HAS_SMS_PUSH_UPDATE = "hasAuthority('" + SMS_PUSH_UPDATE + "')";
    public static final String HAS_SMS_PUSH_DELETE = "hasAuthority('" + SMS_PUSH_DELETE + "')";
    public static final String HAS_SMS_PUSH_EXPORT = "hasAuthority('" + SMS_PUSH_EXPORT + "')";
}
