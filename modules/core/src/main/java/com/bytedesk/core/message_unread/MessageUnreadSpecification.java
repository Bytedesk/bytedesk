/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-21 12:50:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:45:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message_unread;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageUnreadSpecification extends BaseSpecification<MessageUnreadEntity, MessageUnreadRequest> {

    public static Specification<MessageUnreadEntity> search(MessageUnreadRequest request, AuthService authService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 使用基础过滤条件，包括 deleted = false
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));

            // 拉取访客未读消息，使用系统自动生成uid
            // uid 是系统自动生成访客uid
            if (StringUtils.hasText(request.getUid())) {
                // log.info("search message unread by uid: {}", request.getUid());
                // thread.topic contains uid
                predicates.add(criteriaBuilder.like(root.get("thread").get("topic"), "%" + request.getUid() + "%"));
                // 而且 user not contains uid
                predicates.add(criteriaBuilder.not(criteriaBuilder.like(root.get("user"), "%" + request.getUid() + "%")));
            }

            // 客服端加载未读，根据用户订阅的thread.topic加载

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
