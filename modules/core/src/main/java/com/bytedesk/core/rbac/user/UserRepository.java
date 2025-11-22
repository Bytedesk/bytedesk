/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:26:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;

// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * You do NOT want this repository exposed for REST operations!: exported =
 * false
 */
// @RepositoryRestResource(exported = false)
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUid(String uid);

    Optional<UserEntity> findByEmailAndPlatformAndDeletedFalse(String email, String platform);

    Optional<UserEntity> findByMobileAndPlatformAndDeletedFalse(String mobile, String platform);

    Optional<UserEntity> findByUsernameAndPlatformAndDeletedFalse(String username, String platform);

    Boolean existsByUsernameAndPlatformAndDeletedFalse(String username, String platform);

    Boolean existsByMobileAndPlatformAndDeletedFalse(String mobile, String platform);

    Boolean existsByEmailAndPlatformAndDeletedFalse(String email, String platform);

    Boolean existsByUsernameAndMobileAndPlatformAndDeletedFalse(String username, String mobile, String platform);

    Boolean existsBySuperUserAndDeletedFalse(Boolean superUser);

}
