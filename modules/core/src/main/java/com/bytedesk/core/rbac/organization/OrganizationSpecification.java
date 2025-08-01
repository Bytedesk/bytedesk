/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 15:16:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:33:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationSpecification extends BaseSpecification<OrganizationEntity, OrganizationRequest> {
    
    public static Specification<OrganizationEntity> search(OrganizationRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            //
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // code
            if (StringUtils.hasText(request.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + request.getCode() + "%"));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();

                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("code"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
