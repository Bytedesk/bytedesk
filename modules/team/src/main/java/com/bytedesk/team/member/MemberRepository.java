/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 12:34:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  member: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.repository.query.Param;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.bytedesk.core.rbac.user.UserEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * https://docs.spring.io/spring-security/reference/
 */
@Repository
@Tag(name = "member - 成员")
// @RepositoryRestResource(path = "mem", itemResourceRel = "mems", collectionResourceRel = "mems")
// @PreAuthorize("hasRole('ROLE_USER')")
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {

    List<MemberEntity> findByDeptUidAndDeleted(String deptUid, Boolean deleted);

    Optional<MemberEntity> findByUidAndDeleted(String uid, Boolean deleted);

    Optional<MemberEntity> findByUser_UidAndDeleted(String uid, Boolean deleted);

    Optional<MemberEntity> findByMobileAndOrgUidAndDeleted(String mobile, String orgUid, Boolean deleted);

    Optional<MemberEntity> findByEmailAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

    Optional<MemberEntity> findByUserAndOrgUidAndDeleted(UserEntity user, String orgUid, Boolean deleted);

    Boolean existsByEmailAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

    Boolean existsByMobileAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

}
