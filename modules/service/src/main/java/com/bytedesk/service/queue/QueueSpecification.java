/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-08 12:30:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-07 15:54:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.topic.TopicUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueSpecification extends BaseSpecification<QueueEntity, QueueRequest> {

    public static Specification<QueueEntity> search(QueueRequest request, AuthService authService) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // 使用基类方法处理超级管理员权限和组织过滤
            // addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            
            // 根据nickname查询
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // day
            if (StringUtils.hasText(request.getDay())) {
                predicates.add(criteriaBuilder.like(root.get("day"), "%" + request.getDay() + "%"));
            }
            // agentUid
            if (StringUtils.hasText(request.getAgentUid())) {
                String queueTopic = TopicUtils.getQueueTopicFromUid(request.getAgentUid());
                String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                // 根据topic + day查询
                predicates.add(criteriaBuilder.equal(root.get("topic"), queueTopic));
                predicates.add(criteriaBuilder.equal(root.get("day"), today));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
