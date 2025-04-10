/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 09:40:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-24 23:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartzRepository extends JpaRepository<QuartzEntity, Long> {
    
    Optional<QuartzEntity> findByJobName(String jobName);

    Optional<QuartzEntity> findByUid(String uid);

    Page<QuartzEntity> findByOrgUidAndDeleted(String orgUid, Boolean deleted, Pageable pageable);

    Boolean existsByJobName(String jobName);

    // @Transactional
    // void deleteByUid(String uid);
}
