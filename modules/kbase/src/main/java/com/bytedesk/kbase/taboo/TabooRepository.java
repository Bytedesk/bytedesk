/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 22:36:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 17:14:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.taboo;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TabooRepository extends JpaRepository<TabooEntity, Long>, JpaSpecificationExecutor<TabooEntity> {
    
    Optional<TabooEntity> findByUid(String uid);

    Optional<TabooEntity> findByContentContainsAndOrgUidAndDeletedFalse(String content, String orgUid);

    boolean existsByContentContainsAndOrgUidAndDeletedFalse(String content, String orgUid);

    List<TabooEntity> findByOrgUidAndDeletedFalse(String orgUid);
}
