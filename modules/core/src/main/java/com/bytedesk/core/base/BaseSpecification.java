/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 23:19:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * 基础Specification类
 * 提供通用的查询条件构建方法
 */
public abstract class BaseSpecification {

    /**
     * 获取基础查询条件
     * 
     * @param root 查询根对象
     * @param criteriaBuilder 条件构建器
     * @param orgUid 组织ID
     * @return 基础查询条件列表
     */
    protected static List<Predicate> getBasicPredicates(Root<?> root, CriteriaBuilder criteriaBuilder, String orgUid) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        if (StringUtils.hasText(orgUid)) {
            predicates.add(criteriaBuilder.equal(root.get("orgUid"), orgUid));
        }
        return predicates;
    }

    /**
     * 检查并验证超级管理员权限
     * 如果前端设置了superUser标志，则需要判断当前用户是否是超级管理员
     * 如果不是超级管理员，则将superUser设置为false
     * 
     * @param request 请求对象，必须继承自BaseRequest
     * @param authService 认证服务
     * @throws NotLoginException 如果用户未登录
     */
    protected static void validateSuperUserPermission(BaseRequest request, AuthService authService) {
        if (Boolean.TRUE.equals(request.getSuperUser())) {
            UserEntity user = authService.getUser();
            if (user == null) {
                throw new NotLoginException("login first");
            }
            if (!user.isSuperUser()) {
                // 如果不是超级管理员，则设置为false
                request.setSuperUser(false);
            }
        }
    }

    /**
     * 根据超级管理员权限和orgUid添加组织过滤条件
     * 
     * @param root 查询根对象
     * @param criteriaBuilder 条件构建器
     * @param predicates 条件列表
     * @param request 请求对象
     * @param authService 认证服务
     */
    protected static void addOrgFilterIfNotSuperUser(Root<?> root, CriteriaBuilder criteriaBuilder, 
                                                   List<Predicate> predicates, BaseRequest request, AuthService authService) {
        // 先验证超级管理员权限
        validateSuperUserPermission(request, authService);
        
        // 如果不是超级管理员且有orgUid，则添加组织过滤条件
        if (!Boolean.TRUE.equals(request.getSuperUser()) && StringUtils.hasText(request.getOrgUid())) {
            predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
        }
    }
}
