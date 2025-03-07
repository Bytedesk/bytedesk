/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 10:03:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.unanswered;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;

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

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({UnansweredEntityListener.class})
@Table(name = "bytedesk_kbase_unanswered")
public class UnansweredEntity extends BaseEntity {

    private String content;

    @Builder.Default
    @Column(name = "unanswered_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    private String threadUid;

    private String threadTopic;

    private String messageUid;

    private String userUid;
}
