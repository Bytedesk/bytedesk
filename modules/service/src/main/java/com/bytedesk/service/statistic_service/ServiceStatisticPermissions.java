/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-21 11:39:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_service;

public class ServiceStatisticPermissions {

    public static final String ServiceStatistic_PREFIX = "ServiceStatistic_";
    // ServiceStatistic permissions
    public static final String ServiceStatistic_CREATE = "hasAuthority('ServiceStatistic_CREATE')";
    public static final String ServiceStatistic_READ = "hasAuthority('ServiceStatistic_READ')";
    public static final String ServiceStatistic_UPDATE = "hasAuthority('ServiceStatistic_UPDATE')";
    public static final String ServiceStatistic_DELETE = "hasAuthority('ServiceStatistic_DELETE')";
    public static final String ServiceStatistic_EXPORT = "hasAuthority('ServiceStatistic_EXPORT')";

    // 
    
    
}