/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-11 15:58:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 17:19:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

public enum OrganizationVerifyTypeEnum {
    PERSONAL, // 个人认证
    COMPANY, // 企业认证
    GOVERNMENT, // 政府认证
    INSTITUTION, // 机构认证
    INDUSTRY, // 行业认证
    NON_PROFIT, // 非盈利组织认证
    OTHERS; // 其他认证
}
