/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 12:19:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 10:57:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LlmModelRepository extends JpaRepository<LlmModelEntity, Long>, JpaSpecificationExecutor<LlmModelEntity> {

    Optional<LlmModelEntity> findByUid(String uid);

    // Optional<LlmModelEntity> findByNameAndLevel(String name, String level);

    Optional<LlmModelEntity> findByNameAndProviderUid(String name, String providerUid);

    Boolean existsByUid(String uid);

    Boolean existsByNameAndLevel(String name, String level);

    Boolean existsByNameAndProviderUid(String name, String providerUid);

    // Optional<LlmModel> findByNickname(String nickname);

    // Boolean existsByNickname(String nickname);

    List<LlmModelEntity> findByProviderUid(String providerUid);

    // List<LlmModelEntity> findByProviderNameAndOrgUidAndDeletedFalse(String providerName, String orgUid);
}
