/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 09:50:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-25 11:26:42
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

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserSpecification extends BaseSpecification {

    public static Specification<UserEntity> search(UserRequest request) {
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
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
