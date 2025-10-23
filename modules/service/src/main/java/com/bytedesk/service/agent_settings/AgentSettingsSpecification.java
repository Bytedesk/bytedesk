/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent_settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

/**
 * Specification for querying AgentSettings entities
 */
@Slf4j
public class AgentSettingsSpecification extends BaseSpecification<AgentSettingsEntity, AgentSettingsRequest> {

    public static Specification<AgentSettingsEntity> search(AgentSettingsRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Add basic predicates (deleted, orgUid, etc.)
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            
            // Search by name or description
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));
                
                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            
            // Filter by enabled status
            if (request.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }
            
            // Filter by default status
            if (request.getIsDefault() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDefault"), request.getIsDefault()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
