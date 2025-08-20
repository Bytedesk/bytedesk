/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-13 18:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 20:57:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.topic;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TopicSpecification {

    public static Specification<TopicEntity> search(TopicRequest request, AuthService authService) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 根据userUid查询
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(cb.equal(root.get("userUid"), request.getUserUid()));
            }

            // 根据orgUid查询
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.add(cb.equal(root.get("orgUid"), request.getOrgUid()));
            }

            // 根据topic查询
            if (StringUtils.hasText(request.getTopic())) {
                predicates.add(cb.isMember(request.getTopic(), root.get("topics")));
            }

            // 只查询未删除的记录
            predicates.add(cb.equal(root.get("deleted"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 