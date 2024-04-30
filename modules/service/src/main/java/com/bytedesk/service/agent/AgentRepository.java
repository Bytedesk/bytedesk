/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-22 12:17:20
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 
 */
@Repository
@Tag(name = "agent account info - 客服账号信息")
// @PreAuthorize("hasRole('ROLE_ADMIN')")
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    Optional<Agent> findByUid(String uid);

    Optional<Agent> findByEmail(String email);

    Optional<Agent> findByMobile(String mobile);

    Optional<Agent> findByUser_Uid(String uid);

    // Page<Agent> findByOrganization_Oid(String oid, Pageable pageable);
    Page<Agent> findByOrgOid(String oid, Pageable pageable);
}
