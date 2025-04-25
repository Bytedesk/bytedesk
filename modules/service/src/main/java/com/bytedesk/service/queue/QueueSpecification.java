/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-17 14:47:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-25 21:32:40
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
import com.bytedesk.core.topic.TopicUtils;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueSpecification extends BaseSpecification {

    public static Specification <QueueEntity> search (QueueRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
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
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
