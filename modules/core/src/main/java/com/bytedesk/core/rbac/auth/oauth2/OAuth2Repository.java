/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:51:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-10 23:51:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OAuth2Repository extends JpaRepository<OAuth2Entity, Long>, JpaSpecificationExecutor<OAuth2Entity> {
    
}
