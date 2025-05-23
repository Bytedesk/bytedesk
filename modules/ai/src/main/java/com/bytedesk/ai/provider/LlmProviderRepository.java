/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 14:58:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LlmProviderRepository extends JpaRepository<LlmProviderEntity, Long>, JpaSpecificationExecutor<LlmProviderEntity> {
    
    Optional<LlmProviderEntity> findByUid(String uid);

    List<LlmProviderEntity> findByNameAndLevelAndDeletedFalse(String name, String level);

    Optional<LlmProviderEntity> findByNameAndLevelAndOrgUidAndDeletedFalse(String name, String level, String orgUid);
    
    Boolean existsByNameAndLevelAndDeletedFalse(String name, String level);

    Boolean existsByNameAndLevelAndStatusAndDeletedFalse(String name, String level, String status);

    Boolean existsByNameAndLevelAndOrgUidAndDeletedFalse(String name, String level, String orgUid);

    List<LlmProviderEntity> findByStatusAndLevelAndDeletedFalse(String status, String level);
}
