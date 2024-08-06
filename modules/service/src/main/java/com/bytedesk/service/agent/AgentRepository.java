/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 10:59:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Repository
@Tag(name = "agent account info")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findByUid(String uid);

    Optional<Agent> findByEmailAndOrgUidAndDeleted(String email, String orgUid, Boolean deleted);

    Optional<Agent> findByMobileAndOrgUidAndDeleted(String mobile, String orgUid, Boolean deleted);

    // Optional<Agent> findByUser_Uid(String uid);
    Optional<Agent> findByUserUidAndOrgUidAndDeleted(String userUid, String orgUid, Boolean deleted);

    // Page<Agent> findByOrgUidAndDeleted(String orgUid, Boolean deleted, Pageable pageable);

    // Boolean existsByMobileAndDeleted(String mobile, Boolean deleted);

    // Boolean existsByEmailAndDeleted(String email, Boolean deleted);

    Boolean existsByUserUidAndOrgUidAndDeleted(String userUid, String orgUid, Boolean deleted);

    @Transactional
    @Modifying
	@Query(value = "update service_agent set is_connected = :connected where user_uid = :uid", nativeQuery = true)
    void updateConnectedByUid(@Param("connected") Boolean conncted, @Param("uid") String uid);
    
    @Transactional
    @Modifying
    @Query(value = "update Agent set deleted = :deleted where uid = :uid")
    void updateDeletedByUid(@Param("deleted") Boolean deleted, @Param("uid") String uid);
    
}
