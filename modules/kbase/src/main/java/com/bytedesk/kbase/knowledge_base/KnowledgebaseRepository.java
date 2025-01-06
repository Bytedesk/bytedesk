/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 21:44:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KnowledgebaseRepository
        extends JpaRepository<KnowledgebaseEntity, Long>, JpaSpecificationExecutor<KnowledgebaseEntity> {

    Optional<KnowledgebaseEntity> findByUid(String uid);

    List<KnowledgebaseEntity> findByLevelAndTypeAndDeleted(String level, String type, Boolean deleted);

    List<KnowledgebaseEntity> findByLevelAndTypeAndOrgUidAndDeleted(String level, String type, String orgUid, Boolean deleted);

    List<KnowledgebaseEntity> findByLevelAndTypeAndAgentUidAndDeleted(String level, String type, String agentUid, Boolean deleted);

}
