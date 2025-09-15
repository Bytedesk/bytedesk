/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:09:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long>, JpaSpecificationExecutor<FeedbackEntity> {

    Optional<FeedbackEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    // Optional<FeedbackEntity> findByNameAndOrgUidAndTypeAndDeletedFalse(String name, String orgUid, String type);

    // Boolean existsByPlatform(String platform);
}
