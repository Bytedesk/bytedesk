/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *  Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.bytedesk.service.agent.AgentEntity;

// import io.swagger.v3.oas.annotations.tags.Tag;

@Repository
public interface WorkgroupRepository extends JpaRepository<WorkgroupEntity, Long>, JpaSpecificationExecutor<WorkgroupEntity> {

    Optional<WorkgroupEntity> findByUid(String uid);

    boolean existsByUid(String uid);

    /**
     * 查找包含指定客服的所有工作组
     * 
     * @param agent 客服实体
     * @return 包含该客服的工作组列表
     */
    List<WorkgroupEntity> findByAgentsContaining(AgentEntity agent);
    
    /**
     * 通过客服UID查找包含该客服的所有工作组
     * 
     * @param agentUid 客服UID
     * @return 包含该客服的工作组列表
     */
    @Query("SELECT w FROM WorkgroupEntity w JOIN w.agents a WHERE a.uid = :agentUid")
    List<WorkgroupEntity> findByAgentUid(@Param("agentUid") String agentUid);
}
