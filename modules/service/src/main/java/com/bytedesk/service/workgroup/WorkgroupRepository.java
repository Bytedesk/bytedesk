/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-07 11:49:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

// import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Repository
// @Tag(name = "workgroup info - 客服技能组")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface WorkgroupRepository extends JpaRepository<WorkgroupEntity, Long>, JpaSpecificationExecutor<WorkgroupEntity> {

    Optional<WorkgroupEntity> findByUid(@NonNull String uid);

    // existsByUid
    boolean existsByUid(@NonNull String uid);

    // Optional<Workgroup> findByNicknameAndOrgUidAndDeleted(String nickname, String orgUid, Boolean deleted);

    // Page<Workgroup> findByOrgUidAndDeleted(String orgUid, Boolean deleted, Pageable pageable);

}
