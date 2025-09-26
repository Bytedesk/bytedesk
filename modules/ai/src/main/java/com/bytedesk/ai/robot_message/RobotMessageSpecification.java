/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:08:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 11:33:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;


import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobotMessageSpecification extends BaseSpecification<RobotMessageEntity, RobotMessageRequest> {
    
    public static Specification<RobotMessageEntity> search(RobotMessageRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // user.nickname
            if (StringUtils.hasText(request.getUserNickname())) {
                predicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getUserNickname() + "%"));
            }
            // robot.nickname
            if (StringUtils.hasText(request.getRobotNickname())) {
                predicates.add(criteriaBuilder.like(root.get("robot"), "%" + request.getRobotNickname() + "%"));
            }
            //
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            //answer
            if (StringUtils.hasText(request.getAnswer())) {
                predicates.add(criteriaBuilder.like(root.get("answer"), "%" + request.getAnswer() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
