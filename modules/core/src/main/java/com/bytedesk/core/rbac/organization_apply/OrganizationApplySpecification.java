/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 22:19:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:34:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization_apply;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.rbac.auth.AuthService;

import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrganizationApplySpecification extends BaseSpecification<OrganizationApplyEntity, OrganizationApplyRequest> {
    
    public static Specification<OrganizationApplyEntity> search(OrganizationApplyRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 申请记录既可能按 orgUid（组织管理员查看）查询，也可能按 userUid（用户查看我的申请）查询。
            // - 按 orgUid 查询：沿用 org 维度权限校验
            // - 按 userUid 查询：允许不传 orgUid
            if (StringUtils.hasText(request.getOrgUid())) {
                predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            } 
            else {
                // 无组织维度查询（例如：用户查看“我的申请”）
                // OrganizationApplyRequest 继承 BaseRequest（不是 BaseRequestNoOrg），因此这里使用同等逻辑：
                // 1) 校验 superUser 标志是否合法
                // 2) 仅过滤 deleted=false，不强制 orgUid
                validateSuperUserPermission(request, authService);
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            }

            if (StringUtils.hasText(request.getUserUid())) {
                predicates.add(criteriaBuilder.equal(root.get("userUid"), request.getUserUid()));
            }
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
