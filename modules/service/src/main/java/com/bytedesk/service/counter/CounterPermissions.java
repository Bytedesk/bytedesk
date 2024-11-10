/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 21:53:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.counter;

public class CounterPermissions {

    public static final String COUNTER_PREFIX = "COUNTER_";
    // Counter permissions
    public static final String COUNTER_CREATE = "hasAuthority('COUNTER_CREATE')";
    public static final String COUNTER_READ = "hasAuthority('COUNTER_READ')";
    public static final String COUNTER_UPDATE = "hasAuthority('COUNTER_UPDATE')";
    public static final String COUNTER_DELETE = "hasAuthority('COUNTER_DELETE')";
    public static final String COUNTER_EXPORT = "hasAuthority('COUNTER_EXPORT')";
}