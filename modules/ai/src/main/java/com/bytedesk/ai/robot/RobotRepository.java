/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:23:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// @Tag(name = "robot info")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface RobotRepository extends JpaRepository<RobotEntity, Long>, JpaSpecificationExecutor<RobotEntity> {

    Optional<RobotEntity> findByUid(String uid);

    Optional<RobotEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid);
    
    Boolean existsByUidAndDeleted(String uid, Boolean deleted);

    Boolean existsByNicknameAndOrgUidAndDeleted(String nickname, String orgUid, Boolean deleted);

    List<RobotEntity> findByLevelAndDeletedFalseAndType(String level, String type);

}
