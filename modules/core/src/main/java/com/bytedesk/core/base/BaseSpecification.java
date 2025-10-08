/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-26 16:42:38
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

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.I18Consts;
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
public abstract class BaseSpecification<T, TRequest> {

    /**
     * 通用的搜索方法，需要子类实现具体的查询逻辑
     * 
     * @param request 请求对象
     * @param authService 认证服务
     * @return Specification对象
     */
    public static <T, TRequest extends BaseRequest> Specification<T> search(TRequest request, AuthService authService) {
        throw new UnsupportedOperationException("Method search needs to be implemented in child class");
    }

    /**
     * 获取基础查询条件
     * 
     * @param root 查询根对象
     * @param criteriaBuilder 条件构建器
     * @param request 请求对象
     * @param authService 认证服务
     * @return 基础查询条件列表
     */
    protected static List<Predicate> getBasicPredicates(Root<?> root, CriteriaBuilder criteriaBuilder, BaseRequest request, AuthService authService) {
        // 验证超级管理员权限（如有必要会修改 request.superUser）
        validateSuperUserPermission(request, authService);
        
        UserEntity user = authService.getUser();
        // 非超级管理员必须提供 orgUid
        if (user != null && !Boolean.TRUE.equals(request.getSuperUser()) && !StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid不能为空(非超级管理员必须指定组织)");
        }
        
        // 验证请求的 orgUid 是否与当前用户的 orgUid 相同
        if (StringUtils.hasText(request.getOrgUid())) {
            if (user != null && !Boolean.TRUE.equals(request.getSuperUser())) {
                String userOrgUid = user.getOrgUid();
                if (StringUtils.hasText(userOrgUid) && !userOrgUid.equals(request.getOrgUid())) {
                    throw new IllegalArgumentException("无权访问其他组织的数据");
                }
            }
        }
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        
        // 只有非超级管理员且有 orgUid 时才加 orgUid 条件
        if (!Boolean.TRUE.equals(request.getSuperUser()) && StringUtils.hasText(request.getOrgUid())) {
            predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
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
                throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
            }
            if (!user.isSuperUser()) {
                // 如果不是超级管理员，则设置为false
                request.setSuperUser(false);
            }
        }
    }

}
