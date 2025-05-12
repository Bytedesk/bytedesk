/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 15:41:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 13:39:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberSpecification extends BaseSpecification {

    public static Specification<MemberEntity> search(MemberRequest request) {
        // log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request.getOrgUid()));
            
            // 查询member.user.username
            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(criteriaBuilder.like(root.get("user").get("username"), "%" + request.getUsername() + "%"));
            }
            // 
            if (StringUtils.hasText(request.getDeptUid())) {
                predicates.add(criteriaBuilder.equal(root.get("deptUid"), request.getDeptUid()));
            }
            //
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getNickname() + "%"));
            }
            // email
            if (StringUtils.hasText(request.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }
            // mobile
            if (StringUtils.hasText(request.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + request.getMobile() + "%"));
            }
            // status
            // if (StringUtils.hasText(request.getStatus())) {
            //     predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            // }
            // jobNo
            if (StringUtils.hasText(request.getJobNo())) {
                predicates.add(criteriaBuilder.like(root.get("jobNo"), "%" + request.getJobNo() + "%"));
            }
            // jobTitle
            if (StringUtils.hasText(request.getJobTitle())) {
                predicates.add(criteriaBuilder.like(root.get("jobTitle"), "%" + request.getJobTitle() + "%"));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
