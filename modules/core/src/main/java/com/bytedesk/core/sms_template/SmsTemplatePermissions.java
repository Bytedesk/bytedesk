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
package com.bytedesk.core.sms_template;

import com.bytedesk.core.base.BasePermissions;

/**
 * 短信模板权限控制 - 五级权限体系
 */
public class SmsTemplatePermissions extends BasePermissions {

    public static final String SMS_TEMPLATE_PREFIX = "SMS_TEMPLATE_";

    public static final String SMS_TEMPLATE_CREATE = "SMS_TEMPLATE_CREATE";
    public static final String SMS_TEMPLATE_READ = "SMS_TEMPLATE_READ";
    public static final String SMS_TEMPLATE_UPDATE = "SMS_TEMPLATE_UPDATE";
    public static final String SMS_TEMPLATE_DELETE = "SMS_TEMPLATE_DELETE";
    public static final String SMS_TEMPLATE_EXPORT = "SMS_TEMPLATE_EXPORT";

    public static final String HAS_SMS_TEMPLATE_CREATE = "hasAuthority('" + SMS_TEMPLATE_CREATE + "')";
    public static final String HAS_SMS_TEMPLATE_READ = "hasAuthority('" + SMS_TEMPLATE_READ + "')";
    public static final String HAS_SMS_TEMPLATE_UPDATE = "hasAuthority('" + SMS_TEMPLATE_UPDATE + "')";
    public static final String HAS_SMS_TEMPLATE_DELETE = "hasAuthority('" + SMS_TEMPLATE_DELETE + "')";
    public static final String HAS_SMS_TEMPLATE_EXPORT = "hasAuthority('" + SMS_TEMPLATE_EXPORT + "')";
}
