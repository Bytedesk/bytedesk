/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 07:21:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-03 09:29:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueMemberSpecification extends BaseSpecification {
    
    public static Specification <QueueMemberEntity> search(QueueMemberRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // if (request.getStartTime() != null && request.getEndTime() != null) {
            //     predicates.add(criteriaBuilder.between(root.get("createdAt"), request.getStartTime(), request.getEndTime()));
            // }

            // queueNickname
            if (StringUtils.hasText(request.getQueueNickname())) {
                predicates.add(criteriaBuilder.like(root.get("queueNickname"), "%" + request.getQueueNickname() + "%"));
            }

            // 可选条件
            // if (StringUtils.hasText(request.getWorkgroupUid())) {
            //     predicates.add(criteriaBuilder.equal(root.get("workgroupUid"), request.getWorkgroupUid()));
            // }
            // if (StringUtils.hasText(request.getAgentUid())) {
            //     predicates.add(criteriaBuilder.equal(root.get("agentUid"), request.getAgentUid()));
            // }

            // 根据visitorNickname查询
            // if (StringUtils.hasText(request.getVisitorNickname())) {
            //     predicates.add(criteriaBuilder.like(root.get("visitorNickname"), "%" + request.getVisitorNickname() + "%"));
            // }
            // 根据agentNickname查询
            // if (StringUtils.hasText(request.getAgentNickname())) {
            //     predicates.add(criteriaBuilder.like(root.get("agentNickname"), "%" + request.getAgentNickname() + "%"));
            // }
            // 根据client查询
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.like(root.get("client"), "%" + request.getClient() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
