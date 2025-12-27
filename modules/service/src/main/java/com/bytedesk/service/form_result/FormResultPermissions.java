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
package com.bytedesk.service.form_result;

import com.bytedesk.core.base.BasePermissions;

public class FormResultPermissions extends BasePermissions {

    // 模块前缀
    public static final String FORM_RESULT_PREFIX = "FORM_RESULT_";

    // 统一权限（不区分层级）
    public static final String FORM_RESULT_READ = "FORM_RESULT_READ";
    public static final String FORM_RESULT_CREATE = "FORM_RESULT_CREATE";
    public static final String FORM_RESULT_UPDATE = "FORM_RESULT_UPDATE";
    public static final String FORM_RESULT_DELETE = "FORM_RESULT_DELETE";
    public static final String FORM_RESULT_EXPORT = "FORM_RESULT_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_FORM_RESULT_READ = "hasAuthority('FORM_RESULT_READ')";
    public static final String HAS_FORM_RESULT_CREATE = "hasAuthority('FORM_RESULT_CREATE')";
    public static final String HAS_FORM_RESULT_UPDATE = "hasAuthority('FORM_RESULT_UPDATE')";
    public static final String HAS_FORM_RESULT_DELETE = "hasAuthority('FORM_RESULT_DELETE')";
    public static final String HAS_FORM_RESULT_EXPORT = "hasAuthority('FORM_RESULT_EXPORT')";

}
