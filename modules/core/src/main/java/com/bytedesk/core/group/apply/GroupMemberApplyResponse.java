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

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GroupMemberApplyResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String groupUid;

    /** 申请人摘要（管理端列表展示用） */
    private String applicantUserUid;

    private String applicantNickname;

    private String applicantAvatar;

    private String status;

    private Integer attempt;

    private String reason;

    private String rejectReason;

    private String handledByUid;

    private ZonedDateTime handledAt;

    /** 申请创建时间（前端按轮次展示） */
    private ZonedDateTime appliedAt;
}
