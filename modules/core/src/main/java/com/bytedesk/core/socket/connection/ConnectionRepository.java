/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:52:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.socket.connection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConnectionRepository extends JpaRepository<ConnectionEntity, Long>, JpaSpecificationExecutor<ConnectionEntity> {

    Optional<ConnectionEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<ConnectionEntity> findByClientId(String clientId);

    boolean existsByClientId(String clientId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ConnectionEntity c set c.lastHeartbeatAt = :now where c.clientId = :clientId and (c.lastHeartbeatAt is null or c.lastHeartbeatAt <= :threshold)")
    int updateHeartbeatIfOlder(@Param("clientId") String clientId, @Param("now") long now, @Param("threshold") long threshold);

    java.util.List<ConnectionEntity> findByUserUidAndDeletedFalse(String userUid);

    long countByUserUidAndStatusAndDeletedFalse(String userUid, String status);

    // Boolean existsByPlatform(String platform);
}
