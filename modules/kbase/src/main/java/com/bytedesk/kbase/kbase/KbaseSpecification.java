/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-20 18:03:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KbaseSpecification extends BaseSpecification<KbaseEntity, KbaseRequest> {

    public static Specification<KbaseEntity> search(KbaseRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 使用基类方法处理超级管理员权限和组织过滤
            addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            
            if (request.getQueryNotebase()) {
                predicates.add(
                    criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("type"), KbaseTypeEnum.HELPCENTER.name()),
                        criteriaBuilder.equal(root.get("type"), KbaseTypeEnum.NOTEBASE.name())
                    )
                );
            } else if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }

            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();
                
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("description"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
