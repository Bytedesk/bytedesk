/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-13 16:14:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-13 18:33:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long>, JpaSpecificationExecutor<TopicEntity> {

    Optional<TopicEntity> findByUid(String uid);
    
    Optional<TopicEntity> findFirstByUserUid(String userUid);

    Boolean existsByUid(String uid);

    @Query(value="select * from bytedesk_core_topic where topics like %:topic% ", nativeQuery = true)
    Set<TopicEntity> findByTopicsContains(@Param("topic") String topic);
}
