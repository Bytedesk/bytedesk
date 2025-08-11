/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:25:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  member: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.repository.query.Param;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.security.access.prepost.PreAuthorize;

import com.bytedesk.core.rbac.user.UserEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * https://docs.spring.io/spring-security/reference/
 */
@Tag(name = "member - 成员")
// @RepositoryRestResource(path = "mem", itemResourceRel = "mems", collectionResourceRel = "mems")
// @PreAuthorize("hasRole('ROLE_USER')")
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {

    List<MemberEntity> findByDeptUidAndDeletedFalse(String deptUid);

    Optional<MemberEntity> findByUid(String uid);

    Optional<MemberEntity> findByUser_UidAndDeletedFalse(String uid);

    Optional<MemberEntity> findByMobileAndOrgUidAndDeletedFalse(String mobile, String orgUid);

    Optional<MemberEntity> findByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Optional<MemberEntity> findByUserAndOrgUidAndDeletedFalse(UserEntity user, String orgUid);

    Boolean existsByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Boolean existsByMobileAndOrgUidAndDeletedFalse(String email, String orgUid);

    Boolean existsByUid(String uid);
}
