/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-04 15:57:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.bytedesk.core.enums.PlatformEnum;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * You do NOT want this repository exposed for REST operations!: exported =
 * false
 */
// @RepositoryRestResource(exported = false)
// @PreAuthorize("hasRole('ROLE_ADMIN')")
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUid(String uid);

    Optional<User> findByEmailAndPlatformAndDeleted(String email, PlatformEnum platform, Boolean deleted);

    Optional<User> findByMobileAndPlatformAndDeleted(String mobile, PlatformEnum platform, Boolean deleted);

    Optional<User> findByUsernameAndPlatformAndDeleted(String username, PlatformEnum platform, Boolean deleted);

    Boolean existsByUsernameAndPlatformAndDeleted(String username, PlatformEnum platform, Boolean deleted);

    Boolean existsByMobileAndPlatformAndDeleted(String mobile, PlatformEnum platform, Boolean deleted);

    Boolean existsByEmailAndPlatformAndDeleted(String email, PlatformEnum platform, Boolean deleted);

}
