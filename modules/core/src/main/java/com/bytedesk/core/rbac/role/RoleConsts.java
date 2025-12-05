/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:20:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 22:58:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

/**
 * 角色类型常量
 *
 * @author bytedesk.com
 */
public class RoleConsts {

    private RoleConsts() {}

    // public static final String HAS_ANY_ROLE = "hasAnyRole('SUPER', 'ADMIN', 'AGENT')";

    // super - 超级管理员（平台级）
    public static final String ROLE_SUPER = "ROLE_SUPER";
    // admin - 组织管理员（组织级）
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    // dept_admin - 部门管理员（部门级）
    public static final String ROLE_DEPT_ADMIN = "ROLE_DEPT_ADMIN";
    // workgroup_admin - 工作组管理员（工作组级）
    public static final String ROLE_WORKGROUP_ADMIN = "ROLE_WORKGROUP_ADMIN";
    // customer service - 客服
    public static final String ROLE_AGENT = "ROLE_AGENT";
    // end user - 注册用户
    public static final String ROLE_USER = "ROLE_USER";

}
