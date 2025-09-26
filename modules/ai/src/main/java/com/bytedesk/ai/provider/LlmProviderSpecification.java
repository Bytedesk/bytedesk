/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-25 13:49:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-12 23:44:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LlmProviderSpecification extends BaseSpecification<LlmProviderEntity, LlmProviderRequest> {
    
    public static Specification<LlmProviderEntity> search(LlmProviderRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // enabled
            if (request.getEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), request.getEnabled()));
            }
            // 
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("nickname"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("type"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
