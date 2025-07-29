/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-29 08:37:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.List;
import java.util.Optional;

// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.lang.NonNull;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
// import org.springframework.web.bind.annotation.CrossOrigin;

import com.bytedesk.core.rbac.user.UserEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * // @RepositoryRestResource(exported = false) // 隐藏接口
 */
// https://docs.spring.io/spring-data/rest/reference/customizing/configuring-cors.html
// @CrossOrigin
@Repository
@Tag(name = "organization - 公司/组织")
// https://docs.spring.io/spring-data/rest/reference/security.html
// @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER')")
// https://docs.spring.io/spring-data/rest/reference/customizing/configuring-the-rest-url-path.html
// use excerptProjection to self define response format:
// @RepositoryRestResource(path = "org", excerptProjection = OrganizationResponse.class)
public interface OrganizationRepository
                extends JpaRepository<OrganizationEntity, Long>, JpaSpecificationExecutor<OrganizationEntity> {

        Optional<OrganizationEntity> findByUid(String uid);

        // @RestResource(path = "name", rel = "name")
        Optional<OrganizationEntity> findByNameAndDeleted(String name, Boolean deleted);

        Optional<OrganizationEntity> findByCodeAndDeleted(String code, Boolean deleted);

        List<OrganizationEntity> findByDeletedFalse();

        //
        // @NonNull
        // @PreAuthorize("hasRole('ROLE_SUPER')")
        // List<Organization> findAll();

        //
        Page<OrganizationEntity> findByUser(UserEntity user, Pageable pageable);

        Boolean existsByNameAndDeleted(String name, Boolean deleted);

        Boolean existsByCodeAndDeleted(String code, Boolean deleted);

        // 检查 name 唯一性（排除指定 uid）
        Boolean existsByNameAndDeletedAndUidNot(String name, Boolean deleted, String uid);

        // 检查 code 唯一性（排除指定 uid）
        Boolean existsByCodeAndDeletedAndUidNot(String code, Boolean deleted, String uid);

        // @RestResource(exported = false)
        void deleteById(@NonNull Long id);

}
