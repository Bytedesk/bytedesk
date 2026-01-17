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
package com.bytedesk.service.workgroup_routing;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkgroupRoutingRepository extends JpaRepository<WorkgroupRoutingEntity, Long>, JpaSpecificationExecutor<WorkgroupRoutingEntity> {

    Optional<WorkgroupRoutingEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<WorkgroupRoutingEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid);

    List<WorkgroupRoutingEntity> findByWorkgroupUidAndDeletedFalseOrderByUpdatedAtDescIdDesc(String workgroupUid, Pageable pageable);

    // Boolean existsByPlatform(String platform);
}
