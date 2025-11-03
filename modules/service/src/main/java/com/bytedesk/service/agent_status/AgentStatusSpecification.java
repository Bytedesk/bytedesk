/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 16:07:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-02 06:12:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_status;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;

public class AgentStatusSpecification extends BaseSpecification<AgentStatusEntity, AgentStatusRequest> {
    
    public static Specification<AgentStatusEntity> search(AgentStatusRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // agent like
            if (StringUtils.hasText(request.getAgent())) {
                predicates.add(criteriaBuilder.like(root.get("agent").as(String.class), "%" + request.getAgent() + "%"));
            }
            // status like
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.like(root.get("status").as(String.class), "%" + request.getStatus() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
