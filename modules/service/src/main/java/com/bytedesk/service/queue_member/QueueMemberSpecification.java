/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 07:21:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 16:07:08
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

            // queueNickname - 搜索所有三种队列类型，不考虑线程类型
            if (StringUtils.hasText(request.getQueueNickname())) {
                List<Predicate> queuePredicates = new ArrayList<>();
                
                // 只有当agentQueue不为空时才添加条件
                Predicate agentQueueNotNull = criteriaBuilder.isNotNull(root.get("agentQueue"));
                Predicate agentQueueMatch = criteriaBuilder.like(root.get("agentQueue").get("nickname"), 
                                                               "%" + request.getQueueNickname() + "%");
                queuePredicates.add(criteriaBuilder.and(agentQueueNotNull, agentQueueMatch));
                
                // 只有当robotQueue不为空时才添加条件
                Predicate robotQueueNotNull = criteriaBuilder.isNotNull(root.get("robotQueue"));
                Predicate robotQueueMatch = criteriaBuilder.like(root.get("robotQueue").get("nickname"), 
                                                               "%" + request.getQueueNickname() + "%");
                queuePredicates.add(criteriaBuilder.and(robotQueueNotNull, robotQueueMatch));
                
                // 只有当workgroupQueue不为空时才添加条件
                Predicate workgroupQueueNotNull = criteriaBuilder.isNotNull(root.get("workgroupQueue"));
                Predicate workgroupQueueMatch = criteriaBuilder.like(root.get("workgroupQueue").get("nickname"), 
                                                                   "%" + request.getQueueNickname() + "%");
                queuePredicates.add(criteriaBuilder.and(workgroupQueueNotNull, workgroupQueueMatch));
                
                // 将三种队列条件用OR连接
                predicates.add(criteriaBuilder.or(queuePredicates.toArray(new Predicate[0])));
            }

            // 根据visitorNickname查询
            if (StringUtils.hasText(request.getVisitorNickname())) {
                predicates.add(criteriaBuilder.like(root.get("thread").get("user"), "%" + request.getVisitorNickname() + "%"));
            }

            // 根据agentNickname查询
            if (StringUtils.hasText(request.getAgentNickname())) {
                predicates.add(criteriaBuilder.like(root.get("thread").get("agent"), "%" + request.getAgentNickname() + "%"));
            }

            // robotNickname
            if (StringUtils.hasText(request.getRobotNickname())) {
                predicates.add(criteriaBuilder.like(root.get("thread").get("robot"), "%" + request.getRobotNickname() + "%"));
            }
            
            // 根据client查询
            if (StringUtils.hasText(request.getClient())) {
                predicates.add(criteriaBuilder.like(root.get("thread").get("client"), "%" + request.getClient() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
