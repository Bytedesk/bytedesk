/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:20:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-23 15:48:22
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

    // super - 超级管理员
    public static final String ROLE_SUPER = "ROLE_SUPER";
    // admin - 管理员
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    // member - 团队成员
    public static final String ROLE_MEMBER = "ROLE_MEMBER";
    // customer service - 客服
    public static final String ROLE_AGENT = "ROLE_AGENT";


    /**
     * 付费注册用户
     */
    // public static final String ROLE_VIP = "ROLE_VIP";
    /**
     * 试用会员
     */
    // public static final String ROLE_TRY = "ROLE_TRY";
    /**
     * 第三方代理公司
     */
    // public static final String ROLE_PROXY = "ROLE_PROXY";
    /**
     * 免费注册用户
     */
    // public static final String ROLE_FREE = "ROLE_FREE";

    /**
     * 工作组：
     * 客服组长
     */
    // public static final String ROLE_WORKGROUP_ADMIN = "ROLE_WORKGROUP_ADMIN";
    // /**
    //  * 客服账号：
    //  * 接待访客角色，如果要接待访客，必须赋予此角色
    //  */
    // public static final String ROLE_WORKGROUP_AGENT = "ROLE_WORKGROUP_AGENT";
    // /**
    //  * 质检账号
    //  */
    // public static final String ROLE_WORKGROUP_CHECKER = "ROLE_WORKGROUP_CHECKER";


}
