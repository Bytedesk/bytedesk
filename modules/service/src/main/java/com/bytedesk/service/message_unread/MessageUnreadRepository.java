/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-23 12:23:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageUnreadRepository extends JpaRepository<MessageUnreadEntity, Long>, JpaSpecificationExecutor<MessageUnreadEntity> {

    Optional<MessageUnreadEntity> findByUid(String uid);

    boolean existsByUid(String uid);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByThreadTopicContainsAndUserNotContains(String uid1, String uid2);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByUid(String uid);
}
