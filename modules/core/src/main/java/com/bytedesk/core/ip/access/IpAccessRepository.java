/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:49:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-01 14:57:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface IpAccessRepository extends JpaRepository<IpAccessEntity, Long> {
    
    Optional<IpAccessEntity> findFirstByIpAndEndpointAndAccessTimeAfter(String ip, String endpoint, ZonedDateTime time);
} 