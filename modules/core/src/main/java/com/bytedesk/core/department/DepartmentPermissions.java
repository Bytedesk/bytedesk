/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 22:05:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

public class DepartmentPermissions {

    public static final String DEPARTMENT_PREFIX = "DEPARTMENT_";
    // Department permissions
    public static final String DEPARTMENT_CREATE = "hasAuthority('DEPARTMENT_CREATE')";
    public static final String DEPARTMENT_READ = "hasAuthority('DEPARTMENT_READ')";
    public static final String DEPARTMENT_UPDATE = "hasAuthority('DEPARTMENT_UPDATE')";
    public static final String DEPARTMENT_DELETE = "hasAuthority('DEPARTMENT_DELETE')";
    public static final String DEPARTMENT_EXPORT = "hasAuthority('DEPARTMENT_EXPORT')";
}