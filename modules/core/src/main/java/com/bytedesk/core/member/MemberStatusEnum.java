/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 10:32:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 13:33:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

public enum MemberStatusEnum {
    APPLYING, // 申请中，申请加入组织，待组织确认
    INVITING, // 邀请中，刚添加的成员，待登录确认
    ACTIVE, // 激活，已登录确认
    LOCK, // 锁定，锁定后不能访问公司信息
    LEAVE, // 离职，离职后不能访问公司信息
}
