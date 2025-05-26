/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:21:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 16:13:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BlackRepository extends JpaRepository<BlackEntity, String>, JpaSpecificationExecutor<BlackEntity> {

    Optional<BlackEntity> findByUid(String uid);

    Optional<BlackEntity> findFirstByBlackUidAndDeletedFalse(String blackUid);

    List<BlackEntity> findByEndTimeBeforeAndDeletedFalse(LocalDateTime endTime);

    Optional<BlackEntity> findByBlackUidAndOrgUidAndDeletedFalse(String blackUid, String orgUid);
}
