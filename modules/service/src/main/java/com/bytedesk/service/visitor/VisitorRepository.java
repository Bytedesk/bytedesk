/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:28:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VisitorRepository extends JpaRepository<VisitorEntity, Long>, JpaSpecificationExecutor<VisitorEntity> {

    Optional<VisitorEntity> findByUidAndDeleted(String uid, Boolean deleted);

    Optional<VisitorEntity> findByVisitorUidAndOrgUidAndDeleted(String visitorUid, String orgUid, Boolean deleted);

    List<VisitorEntity> findByStatusAndDeleted(String status, Boolean deleted);

    @Modifying
    @Transactional
    @Query("UPDATE VisitorEntity v SET v.status = :status WHERE v.uid = :uid")
    int updateStatusByUid(String uid, String status);
}
