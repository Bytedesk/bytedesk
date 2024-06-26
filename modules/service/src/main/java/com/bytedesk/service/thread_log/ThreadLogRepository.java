/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 10:48:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 11:30:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadLogRepository extends JpaRepository<ThreadLog, Long> {
    
    Page<ThreadLog> findByOrgUid(String orgUid, Pageable pageable);

    Boolean existsByUid(String uid);
}
