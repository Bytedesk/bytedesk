/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 16:14:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:33:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthoritySpecification extends BaseSpecification<AuthorityEntity, AuthorityRequest> {
    
    public static Specification<AuthorityEntity> search(AuthorityRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicatesNoOrg(root, criteriaBuilder, request, authService));
            // predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // value
            if (StringUtils.hasText(request.getValue())) {
                predicates.add(criteriaBuilder.like(root.get("value"), "%" + request.getValue() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // level
            if (request.getLevel()!= null) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }

            UserEntity user = authService.getUser();
            if (user != null && !user.isSuperUser()) {
                OrganizationEntity currentOrganization = user.getCurrentOrganization();
                int orgVipLevel = (currentOrganization != null && currentOrganization.getVipLevel() != null)
                    ? Math.max(currentOrganization.getVipLevel(), 0)
                    : 0;

                Predicate noVipRequirement = criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("vipLevel")),
                    criteriaBuilder.lessThanOrEqualTo(root.get("vipLevel"), 0)
                );

                Predicate vipAllowedByLevel = criteriaBuilder.lessThanOrEqualTo(root.get("vipLevel"), orgVipLevel);

                predicates.add(criteriaBuilder.or(noVipRequirement, vipAllowedByLevel));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
