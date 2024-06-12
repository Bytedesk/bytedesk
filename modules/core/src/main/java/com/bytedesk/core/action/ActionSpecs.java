/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-30 15:49:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-05 23:10:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;

public class ActionSpecs extends BaseSpecification {

    public static Specification<Action> search(ActionRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            //
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + request.getTitle() + "%"));
            }
            if (StringUtils.hasText(request.getAction())) {
                predicates.add(criteriaBuilder.like(root.get("action"), "%" + request.getAction() + "%"));
            }
            if (StringUtils.hasText(request.getDescription())) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription() + "%"));
            }
            if (StringUtils.hasText(request.getIp())) {
                predicates.add(criteriaBuilder.like(root.get("ip"), "%" + request.getIp() + "%"));
            }
            if (StringUtils.hasText(request.getIpLocation())) {
                predicates.add(criteriaBuilder.like(root.get("ipLocation"), "%" + request.getIpLocation() + "%"));
            }
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.like(root.get("type"), "%" + request.getType() + "%"));
            }
            if (StringUtils.hasText(request.getNickname())) {
                predicates
                        .add(criteriaBuilder.like(root.get("user").get("nickname"), "%" + request.getNickname() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
