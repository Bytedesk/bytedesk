/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-28 12:12:32
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

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.bytedesk.core.rbac.user.User;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * https://docs.spring.io/spring-security/reference/
 */
@Repository
@Tag(name = "member - 成员")
@RepositoryRestResource(path = "mem", itemResourceRel = "mems", collectionResourceRel = "mems")
// @PreAuthorize("hasRole('ROLE_USER')")
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    // @Override
    // @PreAuthorize("#member?.user == null or #member?.user?.username ==
    // authentication?.username")
    // Member save(@Param("member") Member member);

    // @Override
    // // @PreAuthorize("@memberRepository.findById(#id)?.user?.username ==
    // // authentication?.username")
    // void deleteById(@Param("id") Long id);

    // @Override
    // // @PreAuthorize("#member?.user?.username == authentication?.username")
    // void delete(@Param("member") Member member);

    Optional<Member> findByUidAndDeleted(String uid, Boolean deleted);

    Optional<Member> findByUser_UidAndDeleted(String uid, Boolean deleted);

    Optional<Member> findByMobileAndOrgUidAndDeleted(String mobile, String orgUid, Boolean deleted);

    Optional<Member> findByEmailAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

    Optional<Member> findByUserAndOrgUidAndDeleted(User user, String orgUid, Boolean deleted);

    Page<Member> findByDepartmentsUidInAndOrgUidAndDeleted(String dids[],
            String orgUid, Boolean deleted, Pageable pageable);

    // Page<Member> findByOrgUidAndDeleted(String orgUid, boolean deleted, Pageable
    // pageable);

    Boolean existsByEmailAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

    Boolean existsByMobileAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

}
