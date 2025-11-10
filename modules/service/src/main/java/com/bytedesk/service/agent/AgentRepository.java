/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:27:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
import org.springframework.transaction.annotation.Transactional;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "agent account info")
public interface AgentRepository extends JpaRepository<AgentEntity, Long>, JpaSpecificationExecutor<AgentEntity> {

    Optional<AgentEntity> findByUid(String uid);

    Optional<AgentEntity> findByUserUid(String userUid);

    List<AgentEntity> findByOrgUid(String orgUid);

    Optional<AgentEntity> findByEmailAndOrgUidAndDeletedFalse(String email, String orgUid);

    Optional<AgentEntity> findByMobileAndOrgUidAndDeletedFalse(String mobile, String orgUid);

    Optional<AgentEntity> findByUserUidAndOrgUidAndDeletedFalse(String userUid, String orgUid);


    List<AgentEntity> findByDeletedFalse();

    List<AgentEntity> findByOrgUidAndDeletedFalse(String orgUid);

    Boolean existsByUserUidAndOrgUidAndDeletedFalse(String userUid, String orgUid);

    
    @Transactional
    @Modifying
    @Query(value = "update AgentEntity set deleted = :deleted where uid = :uid")
    void updateDeletedByUid(@Param("deleted") Boolean deleted, @Param("uid") String uid);

    @Transactional
    @Modifying
    @Query(value = "update bytedesk_service_agent set status = :status where uuid = :uid", nativeQuery = true)
    void updateStatusByUid(@Param("status") String status, @Param("uid") String uid);
    
    // 根据状态和启用状态查找客服
    // @Query("SELECT a.id FROM AgentEntity a WHERE a.status = ?1 AND a.enabled = ?2")
    // List<Long> findByStatusAndEnabled(String status, boolean enabled);
    
    // 根据技能和启用状态查找客服
    // @Query("SELECT a.id FROM AgentEntity a WHERE a.skills LIKE %?1% AND a.enabled = ?2")
    // List<Long> findBySkillsAndEnabled(List<String> skills, boolean enabled);
    
    // 根据在线状态和启用状态查找客服
    // @Query("SELECT a.id FROM AgentEntity a WHERE a.connected = ?1 AND a.enabled = ?2")
    // List<Long> findByConnectedAndEnabled(boolean connected, boolean enabled);
    
    // 更新客服状态
    // @Modifying
    // @Query("UPDATE AgentEntity a SET a.status = ?2 WHERE a.id = ?1")
    // void updateStatus(Long agentId, String status);
    
    // 增加工作负载
    // @Modifying
    // @Query("UPDATE AgentEntity a SET a.workload = a.workload + 1 WHERE a.id = ?1")
    // void incrementWorkload(Long agentId);
    
    // 减少工作负载
    // @Modifying
    // @Query("UPDATE AgentEntity a SET a.workload = a.workload - 1 WHERE a.id = ?1")
    // void decrementWorkload(Long agentId);

    @Query("SELECT a.uid FROM AgentEntity a WHERE a.status = :status AND a.enabled = true")
    List<String> findByStatusAndEnabled(@Param("status") String status);
    
    // @Query("SELECT a.uid FROM AgentEntity a WHERE a.skills LIKE %:skill% AND a.enabled = true")
    // List<String> findBySkillsAndEnabled(@Param("skill") String skill);
    
    
    @Query("SELECT a.status FROM AgentEntity a WHERE a.uid = :agentUid")
    String getAgentStatus(@Param("agentUid") String agentUid);
    
    @Modifying
    @Query("UPDATE AgentEntity a SET a.status = :status WHERE a.uid = :agentUid")
    void updateStatus(@Param("agentUid") String agentUid, @Param("status") String status);
    
    // @Modifying
    // @Query("UPDATE AgentEntity a SET a.workload = a.workload + 1 WHERE a.uid = :agentUid")
    // void incrementWorkload(@Param("agentUid") String agentUid);
    
    // @Modifying
    // @Query("UPDATE AgentEntity a SET a.workload = a.workload - 1 WHERE a.uid = :agentUid")
    // void decrementWorkload(@Param("agentUid") String agentUid);
    
    Boolean existsByUid(String uid);

    /**
     * Check if any non-deleted agent references the given AgentSettings uid
     */
    Boolean existsBySettings_UidAndDeletedFalse(String settingsUid);
}
