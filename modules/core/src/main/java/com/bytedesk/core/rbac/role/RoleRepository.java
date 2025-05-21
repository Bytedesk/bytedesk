/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 08:44:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

// import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByUid(String uid);

    Optional<RoleEntity> findByNameAndLevel(String name, String level);

    Optional<RoleEntity> findByNameAndOrgUidAndDeletedFalse(String name, String orgUid);
    
    Page<RoleEntity> findByOrgUidAndDeletedFalse(String orgUid, Pageable pageable);

    Boolean existsByNameAndOrgUidAndDeletedFalse(String name, String orgUid);

    Boolean existsByNameAndLevel(String name, String level);

    @Transactional
    void deleteById(@NonNull Long id);

    Boolean existsByUid(String uid);
}
