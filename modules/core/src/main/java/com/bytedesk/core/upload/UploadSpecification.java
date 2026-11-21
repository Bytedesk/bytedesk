/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-26 12:03:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:35:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseSpecification;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UploadSpecification extends BaseSpecification<UploadEntity, UploadRequest> {
    
    public static Specification<UploadEntity> search(UploadRequest request, AuthService authService) {
        log.info("request: {}", request);
        return (root, query, criteriaBuilder) -> {
            validateSuperUserPermission(request, authService);

            List<Predicate> predicates = new ArrayList<>();
            // predicates.addAll(getBasicPredicates(root, criteriaBuilder, request, authService));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // nickname 查询 user
            if (StringUtils.hasText(request.getNickname())) {
                predicates.add(criteriaBuilder.like(root.get("user"), "%" + request.getNickname() + "%"));
            }

            // 文件名
            if (StringUtils.hasText(request.getFileName())) {
                predicates.add(criteriaBuilder.like(root.get("fileName"), "%" + request.getFileName() + "%"));
            }

            // 上传类型
            if (StringUtils.hasText(request.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }

            // 分类
            if (StringUtils.hasText(request.getCategoryUid())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryUid"), request.getCategoryUid()));
            }
            
            // 知识库
            if (StringUtils.hasText(request.getKbUid())) {
                predicates.add(criteriaBuilder.equal(root.get("kbUid"), request.getKbUid()));
            }

            // orgUid：非超级管理员只能查看本组织数据；未传 orgUid 时补全当前用户组织，避免越权查询全部
            if (Boolean.TRUE.equals(request.getSuperUser())) {
                if (StringUtils.hasText(request.getOrgUid())) {
                    List<String> matchedOrgUids = resolveOrganizationUidsForKeyword(request.getOrgUid());
                    if (matchedOrgUids.isEmpty()) {
                        predicates.add(criteriaBuilder.disjunction());
                    } else {
                        predicates.add(root.get("orgUid").in(matchedOrgUids));
                    }
                }
                // 超级管理员未传 orgUid：查询全部组织
            } else {
                UserEntity user = authService.getUser();
                if (user == null) {
                    throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
                }
                String userOrgUid = user.getOrgUid();
                boolean platformOrgMember = BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(userOrgUid);
                if (StringUtils.hasText(request.getOrgUid())
                        && !platformOrgMember
                        && !request.getOrgUid().equals(userOrgUid)) {
                    throw new IllegalArgumentException(I18Consts.I18N_ORGANIZATION_ACCESS_DENIED);
                }
                if (StringUtils.hasText(request.getOrgUid())) {
                    // 平台成员可显式查询指定组织，其余用户已在上面校验为本人组织
                    predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
                } else if (StringUtils.hasText(userOrgUid)) {
                    predicates.add(criteriaBuilder.equal(root.get("orgUid"), userOrgUid));
                } else {
                    throw new IllegalArgumentException(I18Consts.I18N_ORG_UID_REQUIRED);
                }
            }
            //
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
