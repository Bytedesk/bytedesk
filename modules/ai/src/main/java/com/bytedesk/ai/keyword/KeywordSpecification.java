/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-06 10:05:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-06 12:32:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.keyword;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeywordSpecification extends BaseSpecification {
    
    public static Specification<Keyword> search(KeywordRequest request) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            // 
            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(criteriaBuilder.like(root.get("keyword"), "%" + request.getKeyword() + "%"));
            }
            if (StringUtils.hasText(request.getReply())) {
                predicates.add(criteriaBuilder.like(root.get("reply"), "%" + request.getReply() + "%"));
            }
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("uid"), request.getCategoryUid()));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
