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
package com.bytedesk.demos.goods;

import com.bytedesk.core.base.BasePermissions;

public class GoodsPermissions extends BasePermissions {

    public static final String GOODS_PREFIX = "GOODS_";

    // Goods Permission Constants - Platform Level
    public static final String GOODS_PLATFORM_CREATE = "GOODS_PLATFORM_CREATE";
    public static final String GOODS_PLATFORM_READ = "GOODS_PLATFORM_READ";
    public static final String GOODS_PLATFORM_UPDATE = "GOODS_PLATFORM_UPDATE";
    public static final String GOODS_PLATFORM_DELETE = "GOODS_PLATFORM_DELETE";
    public static final String GOODS_PLATFORM_EXPORT = "GOODS_PLATFORM_EXPORT";

    // Goods Permission Constants - Organization Level
    public static final String GOODS_ORGANIZATION_CREATE = "GOODS_ORGANIZATION_CREATE";
    public static final String GOODS_ORGANIZATION_READ = "GOODS_ORGANIZATION_READ";
    public static final String GOODS_ORGANIZATION_UPDATE = "GOODS_ORGANIZATION_UPDATE";
    public static final String GOODS_ORGANIZATION_DELETE = "GOODS_ORGANIZATION_DELETE";
    public static final String GOODS_ORGANIZATION_EXPORT = "GOODS_ORGANIZATION_EXPORT";

    // Goods Permission Constants - Department Level
    public static final String GOODS_DEPARTMENT_CREATE = "GOODS_DEPARTMENT_CREATE";
    public static final String GOODS_DEPARTMENT_READ = "GOODS_DEPARTMENT_READ";
    public static final String GOODS_DEPARTMENT_UPDATE = "GOODS_DEPARTMENT_UPDATE";
    public static final String GOODS_DEPARTMENT_DELETE = "GOODS_DEPARTMENT_DELETE";
    public static final String GOODS_DEPARTMENT_EXPORT = "GOODS_DEPARTMENT_EXPORT";

    // Goods Permission Constants - Workgroup Level
    public static final String GOODS_WORKGROUP_CREATE = "GOODS_WORKGROUP_CREATE";
    public static final String GOODS_WORKGROUP_READ = "GOODS_WORKGROUP_READ";
    public static final String GOODS_WORKGROUP_UPDATE = "GOODS_WORKGROUP_UPDATE";
    public static final String GOODS_WORKGROUP_DELETE = "GOODS_WORKGROUP_DELETE";
    public static final String GOODS_WORKGROUP_EXPORT = "GOODS_WORKGROUP_EXPORT";

    // Goods Permission Constants - User Level
    public static final String GOODS_AGENT_CREATE = "GOODS_AGENT_CREATE";
    public static final String GOODS_AGENT_READ = "GOODS_AGENT_READ";
    public static final String GOODS_AGENT_UPDATE = "GOODS_AGENT_UPDATE";
    public static final String GOODS_AGENT_DELETE = "GOODS_AGENT_DELETE";
    public static final String GOODS_AGENT_EXPORT = "GOODS_AGENT_EXPORT";
    // 用户级权限
    public static final String GOODS_USER_READ = "GOODS_USER_READ";
    public static final String GOODS_USER_CREATE = "GOODS_USER_CREATE";
    public static final String GOODS_USER_UPDATE = "GOODS_USER_UPDATE";
    public static final String GOODS_USER_DELETE = "GOODS_USER_DELETE";
    public static final String GOODS_USER_EXPORT = "GOODS_USER_EXPORT";


    // PreAuthorize expressions for any level
    public static final String HAS_GOODS_CREATE_ANY_LEVEL = "hasAnyAuthority('GOODS_PLATFORM_CREATE', 'GOODS_ORGANIZATION_CREATE', 'GOODS_DEPARTMENT_CREATE', 'GOODS_WORKGROUP_CREATE', 'GOODS_AGENT_CREATE', 'GOODS_USER_CREATE')";
    public static final String HAS_GOODS_READ_ANY_LEVEL = "hasAnyAuthority('GOODS_PLATFORM_READ', 'GOODS_ORGANIZATION_READ', 'GOODS_DEPARTMENT_READ', 'GOODS_WORKGROUP_READ', 'GOODS_AGENT_READ', 'GOODS_USER_READ')";
    public static final String HAS_GOODS_UPDATE_ANY_LEVEL = "hasAnyAuthority('GOODS_PLATFORM_UPDATE', 'GOODS_ORGANIZATION_UPDATE', 'GOODS_DEPARTMENT_UPDATE', 'GOODS_WORKGROUP_UPDATE', 'GOODS_AGENT_UPDATE', 'GOODS_USER_UPDATE')";
    public static final String HAS_GOODS_DELETE_ANY_LEVEL = "hasAnyAuthority('GOODS_PLATFORM_DELETE', 'GOODS_ORGANIZATION_DELETE', 'GOODS_DEPARTMENT_DELETE', 'GOODS_WORKGROUP_DELETE', 'GOODS_AGENT_DELETE', 'GOODS_USER_DELETE')";
    public static final String HAS_GOODS_EXPORT_ANY_LEVEL = "hasAnyAuthority('GOODS_PLATFORM_EXPORT', 'GOODS_ORGANIZATION_EXPORT', 'GOODS_DEPARTMENT_EXPORT', 'GOODS_WORKGROUP_EXPORT', 'GOODS_AGENT_EXPORT', 'GOODS_USER_EXPORT')";

}
