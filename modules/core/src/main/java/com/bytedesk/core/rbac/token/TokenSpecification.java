/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:52:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 15:54:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;

public class TokenSpecification extends BaseSpecification {

    public static Specification<TokenEntity> search(TokenRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Add more predicates based on request fields
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
