/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 17:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 10:58:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_unread;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface MessageUnreadRepository
        extends JpaRepository<MessageUnreadEntity, Long>, JpaSpecificationExecutor<MessageUnreadEntity> {

    Optional<MessageUnreadEntity> findByUid(String uid);

    List<MessageUnreadEntity> findByUserUid(String userUid);

    @Transactional
    void deleteByUserUid(String userUid);

    int countByUserUid(String userUid);
}
