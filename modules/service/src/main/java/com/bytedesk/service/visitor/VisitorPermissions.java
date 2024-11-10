/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:55:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

public class VisitorPermissions {

    public static final String VISITOR_PREFIX = "VISITOR_";
    // Visitor permissions
    public static final String VISITOR_CREATE = "hasAuthority('VISITOR_CREATE')";
    public static final String VISITOR_READ = "hasAuthority('VISITOR_READ')";
    public static final String VISITOR_UPDATE = "hasAuthority('VISITOR_UPDATE')";
    public static final String VISITOR_DELETE = "hasAuthority('VISITOR_DELETE')";
    public static final String VISITOR_EXPORT = "hasAuthority('VISITOR_EXPORT')";
}