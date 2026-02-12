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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TopicSubscriptionRepository
        extends JpaRepository<TopicSubscriptionEntity, Long>, JpaSpecificationExecutor<TopicSubscriptionEntity> {

    Optional<TopicSubscriptionEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    boolean existsByUserUidAndTopicAndDeletedFalse(String userUid, String topic);

    Optional<TopicSubscriptionEntity> findFirstByUserUidAndTopicAndDeletedFalse(String userUid, String topic);

    Optional<TopicSubscriptionEntity> findFirstByUserUidAndTopic(String userUid, String topic);

    List<TopicSubscriptionEntity> findByTopicAndDeletedFalse(String topic);
}
