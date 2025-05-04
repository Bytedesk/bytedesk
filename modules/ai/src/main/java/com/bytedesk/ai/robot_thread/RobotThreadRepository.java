/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:09:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 23:06:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_thread;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RobotThreadRepository extends JpaRepository<RobotThreadEntity, Long>, JpaSpecificationExecutor<RobotThreadEntity> {
    
    Optional<RobotThreadEntity> findByUid(String uid);

    Boolean existsByUid(String uid);

    Optional<RobotThreadEntity> findFirstByTopic(String topic);

    Boolean existsByTopic(String topic);
}
