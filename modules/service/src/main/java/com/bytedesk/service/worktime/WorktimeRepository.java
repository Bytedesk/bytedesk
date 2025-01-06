/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:46:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-12 11:38:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorktimeRepository extends JpaRepository<WorktimeEntity, Long> {
    
    Optional<WorktimeEntity> findByUid(String uid);
}
