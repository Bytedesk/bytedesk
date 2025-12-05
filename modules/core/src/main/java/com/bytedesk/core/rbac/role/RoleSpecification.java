/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-11 18:13:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 13:15:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleSpecification extends BaseSpecification<RoleEntity, RoleRequest> {

    public static Specification<RoleEntity> search(RoleRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            if (request.getSystem() != null) {
                predicates.add(criteriaBuilder.equal(root.get("system"), request.getSystem()));
                if (Boolean.FALSE.equals(request.getSystem()) && StringUtils.hasText(request.getOrgUid())) {
                    predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
                }
            } else {
                //
                if (StringUtils.hasText(request.getLevel())) {
                    predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
                }
                // 过滤 orgUid
                if (StringUtils.hasText(request.getOrgUid())) {
                    predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
                }
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
