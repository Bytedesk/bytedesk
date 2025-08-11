/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:25:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import java.util.Optional;

// import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.lang.NonNull;
// import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Tag(name = "department - 部门/分公司")
// @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER')")
// @RepositoryRestResource(path = "dep")
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {

    Optional<DepartmentEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid);

    Optional<DepartmentEntity> findByUid(String uid);

    Boolean existsByNameAndOrgUidAndDeletedFalse(String name, String orgUid);

    Boolean existsByUid(String uid);

}
