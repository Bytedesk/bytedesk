/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task_comment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskCommentSpecification extends BaseSpecification<TaskCommentEntity, TaskCommentRequest> {
    
    public static Specification<TaskCommentEntity> search(TaskCommentRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用带层级过滤的基础条件
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService, TaskCommentPermissions.MODULE_NAME));
            // name
            // if (StringUtils.hasText(request.getName())) {
            //     predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            // }
            // taskUid
            if (StringUtils.hasText(request.getTaskUid())) {
                predicates.add(criteriaBuilder.equal(root.get("taskUid"), request.getTaskUid()));
            }
            // content
            if (StringUtils.hasText(request.getContent())) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + request.getContent() + "%"));
            }
            // description
            // if (StringUtils.hasText(request.getDescription())) {
            //     predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            // }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // level - 如果指定了level则精确过滤
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            // 
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
