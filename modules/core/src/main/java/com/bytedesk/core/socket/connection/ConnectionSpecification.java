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
package com.bytedesk.core.socket.connection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionSpecification extends BaseSpecification<ConnectionEntity, ConnectionRequest> {
    
    public static Specification<ConnectionEntity> search(ConnectionRequest request, AuthService authService) {
        // log.info("request: {} orgUid: {} pageNumber: {} pageSize: {}", 
        //     request, request.getOrgUid(), request.getPageNumber(), request.getPageSize());
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            // clientId (searchable in frontend)
            if (StringUtils.hasText(request.getClientId())) {
                predicates.add(criteriaBuilder.like(root.get("clientId"), "%" + request.getClientId() + "%"));
            }
            // protocol (searchable in frontend)
            if (StringUtils.hasText(request.getProtocol())) {
                predicates.add(criteriaBuilder.equal(root.get("protocol"), request.getProtocol()));
            }
            // status: CONNECTED / DISCONNECTED (searchable in frontend)
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            // userUid (may be used by API / advanced filters)
            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
