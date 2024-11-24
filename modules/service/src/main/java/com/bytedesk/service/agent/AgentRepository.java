/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 15:27:39
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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.tags.Tag;

@Repository
@Tag(name = "agent account info")
public interface AgentRepository extends JpaRepository<AgentEntity, Long>, JpaSpecificationExecutor<AgentEntity> {

    Optional<AgentEntity> findByUid(String uid);

    Optional<AgentEntity> findByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Optional<AgentEntity> findByMobileAndOrgUidAndDeletedFalse(String mobile, String orgUid);

    Optional<AgentEntity> findByUserUidAndOrgUidAndDeletedFalse(String userUid, String orgUid);

    List<AgentEntity> findByConnectedAndDeletedFalse(boolean connected);

    Boolean existsByUserUidAndOrgUidAndDeletedFalse(String userUid, String orgUid);

    @Transactional
    @Modifying
	@Query(value = "update bytedesk_service_agent set is_connected = :connected where user_uid = :userUid", nativeQuery = true)
    void updateConnectedByUserUid(@Param("connected") Boolean connected, @Param("userUid") String userUid);
    
    @Transactional
    @Modifying
    @Query(value = "update AgentEntity set deleted = :deleted where uid = :uid")
    void updateDeletedByUid(@Param("deleted") Boolean deleted, @Param("uid") String uid);

    @Transactional
    @Modifying
    @Query(value = "update bytedesk_service_agent set status = :status where uuid = :uid", nativeQuery = true)
    void updateStatusByUid(@Param("status") String status, @Param("uid") String uid);
    
}
