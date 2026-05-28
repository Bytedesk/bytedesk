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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 * https://spring.io/guides/tutorials/react-and-spring-data-rest/
 * You do NOT want this repository exposed for REST operations!: exported =
 * false
 */
// @RepositoryRestResource(exported = false)
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUid(String uid);

    /**
     * 用于 profile/前端组织展示：预加载组织 owner 信息，避免 LAZY 导致 owner 为空。
     */
    @EntityGraph(attributePaths = {
            "currentOrganization",
            "currentOrganization.user",
            "userOrganizationRoles",
            "userOrganizationRoles.organization",
            "userOrganizationRoles.organization.user",
            "userOrganizationRoles.roles"
    })
    @Query("select u from UserEntity u where u.uid = :uid")
    Optional<UserEntity> findByUidWithOrganizations(@Param("uid") String uid);

    Optional<UserEntity> findByEmailAndPlatformAndDeletedFalse(String email, String platform);

    Optional<UserEntity> findByMobileAndPlatformAndDeletedFalse(String mobile, String platform);

    @Query("select u from UserEntity u where u.mobile = :mobile and u.platform = :platform and u.deleted = false "
            + "and (u.country = :country or ((u.country is null or u.country = '') "
            + "and not exists (select 1 from UserEntity exactUser where exactUser.mobile = :mobile "
            + "and exactUser.platform = :platform and exactUser.deleted = false and exactUser.country = :country)))")
    Optional<UserEntity> findByMobileAndCountryAndPlatformAndDeletedFalse(
            @Param("mobile") String mobile,
            @Param("country") String country,
            @Param("platform") String platform);

    Optional<UserEntity> findByUsernameAndPlatformAndDeletedFalse(String username, String platform);

    Boolean existsByUsernameAndPlatformAndDeletedFalse(String username, String platform);

    Boolean existsByMobileAndPlatformAndDeletedFalse(String mobile, String platform);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u "
            + "where u.mobile = :mobile and u.platform = :platform and u.deleted = false "
            + "and (u.country = :country or u.country is null or u.country = '')")
    Boolean existsByMobileAndCountryAndPlatformAndDeletedFalse(
            @Param("mobile") String mobile,
            @Param("country") String country,
            @Param("platform") String platform);

    Boolean existsByEmailAndPlatformAndDeletedFalse(String email, String platform);

    Boolean existsByUsernameAndMobileAndPlatformAndDeletedFalse(String username, String mobile, String platform);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u "
            + "where u.username = :username and u.mobile = :mobile and u.platform = :platform and u.deleted = false "
            + "and (u.country = :country or u.country is null or u.country = '')")
    Boolean existsByUsernameAndMobileAndCountryAndPlatformAndDeletedFalse(
            @Param("username") String username,
            @Param("mobile") String mobile,
            @Param("country") String country,
            @Param("platform") String platform);

    Boolean existsBySuperUserAndDeletedFalse(Boolean superUser);

    Optional<UserEntity> findFirstBySuperUserAndDeletedFalse(Boolean superUser);

}
