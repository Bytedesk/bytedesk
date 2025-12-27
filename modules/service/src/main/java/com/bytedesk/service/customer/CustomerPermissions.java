/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-24 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-24 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import com.bytedesk.core.base.BasePermissions;

public class CustomerPermissions extends BasePermissions {

    // 模块前缀
    public static final String CUSTOMER_PREFIX = "CUSTOMER_";

    // 统一权限（不区分层级）
    public static final String CUSTOMER_READ = "CUSTOMER_READ";
    public static final String CUSTOMER_CREATE = "CUSTOMER_CREATE";
    public static final String CUSTOMER_UPDATE = "CUSTOMER_UPDATE";
    public static final String CUSTOMER_DELETE = "CUSTOMER_DELETE";
    public static final String CUSTOMER_EXPORT = "CUSTOMER_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_CUSTOMER_READ = "hasAuthority('CUSTOMER_READ')";
    public static final String HAS_CUSTOMER_CREATE = "hasAuthority('CUSTOMER_CREATE')";
    public static final String HAS_CUSTOMER_UPDATE = "hasAuthority('CUSTOMER_UPDATE')";
    public static final String HAS_CUSTOMER_DELETE = "hasAuthority('CUSTOMER_DELETE')";
    public static final String HAS_CUSTOMER_EXPORT = "hasAuthority('CUSTOMER_EXPORT')";

}
