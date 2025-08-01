/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:49:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 11:44:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_white;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IpWhiteRepository extends JpaRepository<IpWhiteEntity, Long>, JpaSpecificationExecutor<IpWhiteEntity> {
    
    boolean existsByIpAndDeletedFalse(String ip);

    Optional<IpWhiteEntity> findByIpAndDeletedFalse(String ip);

    Optional<IpWhiteEntity> findByUid(String uid);
} 
