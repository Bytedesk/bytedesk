/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 09:07:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 15:24:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RobotSpecification extends BaseSpecification<RobotEntity, RobotRequest> {
    
    public static Specification<RobotEntity> search(RobotRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            // 使用基类方法处理超级管理员权限和组织过滤
            addOrgFilterIfNotSuperUser(root, criteriaBuilder, predicates, request, authService);
            // 查询level == 'PLATFORM' 或者 orgUid == request.getOrgUid()
            // predicates.add(criteriaBuilder.or(
            //     criteriaBuilder.equal(root.get("level"), LevelEnum.PLATFORM.name()),
            //     criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid())
            // ));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            // nickname
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // description
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
            if (StringUtils.hasText(request.getLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), request.getLevel()));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            // kbEnabled
            if (request.getKbEnabled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("kbEnabled"), request.getKbEnabled()));
            }
            // kbUid
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            // 方便前端搜索
            if (StringUtils.hasText(request.getPrompt())) {
                predicates.add(criteriaBuilder.like(root.get("llm").get("prompt"), "%" + request.getPrompt() + "%"));
            }
            // 
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
