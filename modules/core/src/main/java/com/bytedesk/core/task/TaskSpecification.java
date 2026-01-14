/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 15:44:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskSpecification extends BaseSpecification<TaskEntity, TaskRequest> {
    
    public static Specification<TaskEntity> search(TaskRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));

            // 可见性：
            // - 组织任务（level=ORGANIZATION）对组织成员可见
            // - 私人任务（level=USER）仅对创建者可见
            // 说明：Task 当前不走 getBasicPredicatesWithLevel（权限模块），这里采用明确规则防止泄露其他用户私人任务。
            UserEntity user = authService.getUser();
            if (user != null) {
                Predicate orgVisible = criteriaBuilder.equal(root.get("level"), LevelEnum.ORGANIZATION.name());
                Predicate userVisible = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("level"), LevelEnum.USER.name()),
                        criteriaBuilder.equal(root.get("userUid"), user.getUid()));
                predicates.add(criteriaBuilder.or(orgVisible, userVisible));
            } else {
                // 未登录时仅允许查询组织级（避免暴露个人数据）
                predicates.add(criteriaBuilder.equal(root.get("level"), LevelEnum.ORGANIZATION.name()));
            }
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
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
