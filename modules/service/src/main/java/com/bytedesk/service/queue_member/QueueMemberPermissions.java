/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-02 15:02:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

public class QueueMemberPermissions {

    public static final String VISITOR_COUNTER_PREFIX = "VISITOR_COUNTER_";
    // QueueMember permissions
    public static final String VISITOR_COUNTER_CREATE = "hasAuthority('VISITOR_COUNTER_CREATE')";
    public static final String VISITOR_COUNTER_READ = "hasAuthority('VISITOR_COUNTER_READ')";
    public static final String VISITOR_COUNTER_UPDATE = "hasAuthority('VISITOR_COUNTER_UPDATE')";
    public static final String VISITOR_COUNTER_DELETE = "hasAuthority('VISITOR_COUNTER_DELETE')";
    public static final String VISITOR_COUNTER_EXPORT = "hasAuthority('VISITOR_COUNTER_EXPORT')";
}