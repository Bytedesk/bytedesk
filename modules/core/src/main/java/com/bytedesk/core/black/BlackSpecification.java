/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-01 13:08:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlackSpecification extends BaseSpecification<BlackEntity, BlackRequest> {

    public static Specification<BlackEntity> search(BlackRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 使用基类方法处理超级管理员权限和组织过滤
            addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            
            if (StringUtils.hasText(request.getBlackNickname())) {
                predicates.add(criteriaBuilder.like(root.get("blackNickname"), "%" + request.getBlackNickname() + "%"));
            }
            
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
