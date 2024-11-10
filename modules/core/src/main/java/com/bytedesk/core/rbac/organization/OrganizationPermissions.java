/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:57:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-05 16:57:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

public class OrganizationPermissions {

    public static final String ORGANIZATION_PREFIX = "ORGANIZATION_";
    // Organization permissions
    public static final String ORGANIZATION_CREATE = "hasAuthority('ORGANIZATION_CREATE')";
    public static final String ORGANIZATION_READ = "hasAuthority('ORGANIZATION_READ')";
    public static final String ORGANIZATION_UPDATE = "hasAuthority('ORGANIZATION_UPDATE')";
    public static final String ORGANIZATION_DELETE = "hasAuthority('ORGANIZATION_DELETE')";
    public static final String ORGANIZATION_EXPORT = "hasAuthority('ORGANIZATION_EXPORT')";


}