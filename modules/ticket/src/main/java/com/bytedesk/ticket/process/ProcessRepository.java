/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-15 16:45:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface ProcessRepository extends JpaRepository<ProcessEntity, Long>, JpaSpecificationExecutor<ProcessEntity> {

    Optional<ProcessEntity> findByUid(String uid);

    Optional<ProcessEntity> findByUidAndOrgUidAndType(String uid, String orgUid, String type);

    List<ProcessEntity> findByOrgUidAndStatus(String orgUid, String status);

    /** 查找有 BPMN schema 但缺少 flowgramSchema 的流程（用于迁移） */
    List<ProcessEntity> findBySchemaIsNotNullAndFlowgramSchemaIsNull();

    /** 含软删除记录的精确查询：初始化需区分「不存在」与「被软删除」 */
    @Query("SELECT p FROM ProcessEntity p WHERE p.uid = :uid")
    Optional<ProcessEntity> findByUidIncludingDeleted(@Param("uid") String uid);

    /** 行锁查询：串行化同组织默认流程的自动/手动初始化，覆盖多实例竞争 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProcessEntity p WHERE p.uid = :uid")
    Optional<ProcessEntity> findByUidForUpdate(@Param("uid") String uid);

    // Boolean existsByPlatform(String platform);
}
