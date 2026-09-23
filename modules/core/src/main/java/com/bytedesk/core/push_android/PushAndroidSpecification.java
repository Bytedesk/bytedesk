/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushAndroidSpecification extends BaseSpecification<PushAndroidEntity, PushAndroidRequest> {

    public static Specification<PushAndroidEntity> search(PushAndroidRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}",
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用带层级过滤的基础条件
            predicates.addAll(getBasicPredicatesWithLevel(root, criteriaBuilder, request, authService, PushAndroidPermissions.MODULE_NAME));
            // name
            if (StringUtils.hasText(request.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + request.getName() + "%"));
            }
            if (StringUtils.hasText(request.getReceiver())) {
                predicates.add(criteriaBuilder.equal(root.get("receiver"), request.getReceiver()));
            }
            if (StringUtils.hasText(request.getProvider())) {
                predicates.add(criteriaBuilder.equal(root.get("provider"), request.getProvider()));
            }
            if (StringUtils.hasText(request.getMessageId())) {
                predicates.add(criteriaBuilder.equal(root.get("messageId"), request.getMessageId()));
            }
            if (StringUtils.hasText(request.getMessageUid())) {
                predicates.add(criteriaBuilder.equal(root.get("messageUid"), request.getMessageUid()));
            }
            if (StringUtils.hasText(request.getThreadUid())) {
                predicates.add(criteriaBuilder.equal(root.get("threadUid"), request.getThreadUid()));
            }
            // type
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
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
