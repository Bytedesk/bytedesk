/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group.apply;

/**
 * 入群申请状态机（参考 OrganizationApplyStatusEnum，增加群场景终态）
 * PENDING: 待审批
 * APPROVED: 已通过
 * REJECTED: 已拒绝
 * CANCELED: 申请人已撤回
 * INVALIDATED: 群转私有/解散等原因失效
 * FULFILLED: 申请人已通过其他途径（如邀请）入群
 */
public enum GroupMemberApplyStatusEnum {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELED,
    INVALIDATED,
    FULFILLED;
}
