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

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberApplyRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /** 目标群 uid（apply/create 时必填） */
    private String groupUid;

    /** 申请理由（可选） */
    private String reason;

    /** 拒绝原因（reject 时可选） */
    private String rejectReason;

    /** 状态筛选（查询用） */
    private String status;

    /** 轮次（approve/reject/cancel 幂等校验用，可选） */
    private Integer attempt;
}
