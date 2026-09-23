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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
// @RepositoryRestResource(path = "mem", itemResourceRel = "mems",
// collectionResourceRel = "mems")
// @PreAuthorize("hasRole('ROLE_USER')")
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, JpaSpecificationExecutor<MemberEntity> {

    List<MemberEntity> findByDeptUidAndDeletedFalse(String deptUid);

    Optional<MemberEntity> findByUid(String uid);

    Optional<MemberEntity> findByUser_UidAndOrgUidAndDeletedFalse(String uid, String orgUid);

    Optional<MemberEntity> findByMobileAndOrgUidAndDeletedFalse(String mobile, String orgUid);

    @Query("select m from MemberEntity m where m.mobile = :mobile and m.orgUid = :orgUid and m.deleted = false and (m.country = :country or (:country = '86' and (m.country is null or m.country = '')))")
    Optional<MemberEntity> findByMobileAndCountryAndOrgUidAndDeletedFalse(
            @Param("mobile") String mobile,
            @Param("country") String country,
            @Param("orgUid") String orgUid);

    Optional<MemberEntity> findByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Optional<MemberEntity> findByUserAndOrgUidAndDeletedFalse(UserEntity user, String orgUid);

    /**
     * 查询用户在指定组织下的成员记录（含已软删），用于管理员成员补齐时恢复历史软删记录，
     * 避免重复插入。
     */
    Optional<MemberEntity> findByUser_UidAndOrgUid(String userUid, String orgUid);

    /**
     * 查询用户名下所有有效（未软删）成员记录，用于 User 联系方式变更后的镜像同步。
     * 语义：Member.mobile/email/country 是关联 User 联系方式在组织维度的镜像副本
     * （MemberRestService.create 以 member 联系方式作为平台用户解析的 join key）。
     */
    List<MemberEntity> findByUser_UidAndDeletedFalse(String userUid);

    /**
     * T0-3 存量盘点：取全部有效成员并预取关联用户，用于联系方式一致性审计（dry-run）。
     */
    @Query("select m from MemberEntity m join fetch m.user where m.deleted = false")
    List<MemberEntity> findAllActiveWithUser();

    long countByOrgUidAndDeletedFalse(String orgUid);

    Boolean existsByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Boolean existsByMobileAndOrgUidAndDeletedFalse(String email, String orgUid);

    @Query("select case when count(m) > 0 then true else false end from MemberEntity m where m.mobile = :mobile and m.orgUid = :orgUid and m.deleted = false and (m.country = :country or (:country = '86' and (m.country is null or m.country = '')))")
    Boolean existsByMobileAndCountryAndOrgUidAndDeletedFalse(
            @Param("mobile") String mobile,
            @Param("country") String country,
            @Param("orgUid") String orgUid);

    Boolean existsByUid(String uid);
}
