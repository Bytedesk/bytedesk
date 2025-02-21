/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:09:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-11 09:03:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceStatisticRepository extends JpaRepository<ServiceStatisticEntity, Long>, JpaSpecificationExecutor<ServiceStatisticEntity> {

}
