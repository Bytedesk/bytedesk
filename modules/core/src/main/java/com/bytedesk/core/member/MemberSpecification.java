/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-07 15:41:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:33:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberSpecification extends BaseSpecification<MemberEntity, MemberRequest> {

    public static Specification<MemberEntity> search(MemberRequest request, AuthService authService) {
        log.info("member search request: {}, {}, {}", request, request.getOrgUid(), request.getSearchText());
        return (root, query, criteriaBuilder) -> {
            // 校验：非超级管理员必须传 orgUid
            if (!Boolean.TRUE.equals(request.getSuperUser()) && !StringUtils.hasText(request.getOrgUid())) {
                throw new IllegalArgumentException("orgUid不能为空(非超级管理员必须指定组织)");
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.addAll(getBasicPredicates(root, criteriaBuilder, request));
            
            // 查询member.user.username
            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(criteriaBuilder.like(root.get("user").get("username"), "%" + request.getUsername() + "%"));
            }
            // deptUid and subDeptUids with OR relationship
            if (StringUtils.hasText(request.getDeptUid()) || (request.getSubDeptUids() != null && request.getSubDeptUids().size() > 0)) {
                List<Predicate> deptPredicates = new ArrayList<>();
                if (StringUtils.hasText(request.getDeptUid())) {
                    deptPredicates.add(criteriaBuilder.equal(root.get("deptUid"), request.getDeptUid()));
                }
                if (request.getSubDeptUids() != null && request.getSubDeptUids().size() > 0) {
                    for (String subDeptUid : request.getSubDeptUids()) {
                        deptPredicates.add(criteriaBuilder.equal(root.get("deptUid"), subDeptUid));
                    }
                }
                predicates.add(criteriaBuilder.or(deptPredicates.toArray(new Predicate[0])));
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
            // searchText
            if (StringUtils.hasText(request.getSearchText())) {
                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(criteriaBuilder.like(root.get("nickname"), "%" + request.getSearchText() + "%"));
                searchPredicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getSearchText() + "%"));
                searchPredicates.add(criteriaBuilder.like(root.get("mobile"), "%" + request.getSearchText() + "%"));
                searchPredicates.add(criteriaBuilder.like(root.get("jobNo"), "%" + request.getSearchText() + "%"));
                searchPredicates.add(criteriaBuilder.like(root.get("jobTitle"), "%" + request.getSearchText() + "%"));
                predicates.add(criteriaBuilder.or(searchPredicates.toArray(new Predicate[0])));
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
