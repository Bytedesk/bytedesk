/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.topic_subscription;

import com.bytedesk.core.base.BaseEntityNoOrg;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "bytedesk_core_topic_subscription",
        indexes = {
                @Index(name = "idx_topic_subscription_topic", columnList = "topic"),
                @Index(name = "idx_topic_subscription_user_uid", columnList = "user_uid")
        })
public class TopicSubscriptionEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, length = 512)
    private String topic;
}
