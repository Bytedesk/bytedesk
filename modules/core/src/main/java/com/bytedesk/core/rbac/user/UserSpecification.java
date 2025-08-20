/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 09:50:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:34:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserSpecification extends BaseSpecification<UserEntity, UserRequest> {

    public static Specification<UserEntity> search(UserRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // load all users, including deleted users
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // username
            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(criteriaBuilder.equal(root.get("username"), request.getUsername()));
            }
            // 
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.equal(root.get("nickname"), request.getNickname()));
            }
            // email
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.equal(root.get("email"), request.getEmail()));
            }
            // mobile
            if (StringUtils.hasText(request.getMobile())) {
                predicates.add(criteriaBuilder.equal(root.get("mobile"), request.getMobile()));
            }
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> orPredicates = new ArrayList<>();
                String searchText = request.getSearchText();

                orPredicates.add(criteriaBuilder.like(root.get("username"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("nickname"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("email"), "%" + searchText + "%"));
                orPredicates.add(criteriaBuilder.like(root.get("mobile"), "%" + searchText + "%"));

                predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
