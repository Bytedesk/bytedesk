/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-13 08:24:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageLeaveRepository extends JpaRepository<MessageLeaveEntity, Long>, JpaSpecificationExecutor<MessageLeaveEntity> {

    Optional<MessageLeaveEntity> findByUid(String uid);

    @Query("SELECT COUNT(m) FROM MessageLeaveEntity m WHERE m.orgUid = :orgUid AND m.status = :status AND m.deleted = false")
    long countByOrgUidAndStatusAndDeletedFalse(@Param("orgUid") String orgUid, @Param("status") String status);
}
