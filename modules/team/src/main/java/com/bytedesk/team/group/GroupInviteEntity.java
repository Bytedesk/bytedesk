/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-28 11:20:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-28 12:27:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "bytedesk_team_group_invites")
public class GroupInviteEntity extends BaseEntity {
    
    @ManyToOne
    private GroupEntity group;
    
    @ManyToOne
    private UserEntity inviter;
    
    @ManyToOne
    private UserEntity invitee;
    
    private GroupInviteStatus status = GroupInviteStatus.PENDING;
    
    private LocalDateTime expireTime;
    
    private String message;  // 邀请消息
} 