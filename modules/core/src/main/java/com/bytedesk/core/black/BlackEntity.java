/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-28 22:02:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 14:17:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * black list 
 * 黑名单
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({BlackEntityListener.class})
@Table(name = "bytedesk_core_black")
public class BlackEntity extends BaseEntity {

    // 黑名单类型
    @Builder.Default
    private String type = BlackTypeEnum.VISITOR.name();

    private String reason;

    // AI: 考虑到黑名单功能主要用于用户访问控制,需要频繁查询和过滤,我建议使用单独字段存储的方式:
    // 黑名单用户uid
    @Column(nullable = false)
    private String blackUid;

    // 黑名单用户昵称
    private String blackNickname;

    // 黑名单用户头像
    private String blackAvatar;

    // 是否封禁ip
    @Builder.Default
    private boolean blockIp = false;

    // 执行拉黑的用户uid
    @Column(nullable = false)
    private String userUid;

    // 执行拉黑的用户昵称
    private String userNickname;

    // 执行拉黑的用户头像
    private String userAvatar;

     // 开始时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime = LocalDateTime.now();

    // 结束时间
    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime = LocalDateTime.now().plusHours(24);

    // 会话主题
    private String threadTopic;
}
