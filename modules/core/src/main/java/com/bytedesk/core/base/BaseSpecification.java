/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 22:46:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
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
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.utils.ApplicationContextHolder;

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
        boolean platformDefaultOrgRequest = isPlatformDefaultOrgRequest(request);
        boolean platformLevelRequest = LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel());

        // 非超级管理员一般必须提供 orgUid；但平台级数据允许任何登录用户在不传 orgUid 的情况下查询
        if (user != null
                && !Boolean.TRUE.equals(request.getSuperUser())
                && !StringUtils.hasText(request.getOrgUid())
                && !platformLevelRequest) {
            throw new IllegalArgumentException("orgUid should not be null (org uid must be provided for non-super request)");
        }
        
        // 验证请求的 orgUid 是否与当前用户的 orgUid 相同
        if (StringUtils.hasText(request.getOrgUid())) {
            if (user != null
                    && !Boolean.TRUE.equals(request.getSuperUser())
                    && !platformDefaultOrgRequest
                    && !platformLevelRequest) {
                String userOrgUid = user.getOrgUid();
                if (StringUtils.hasText(userOrgUid) && !userOrgUid.equals(request.getOrgUid())) {
                    throw new IllegalArgumentException("No permission to access data of other organizations");
                }
            }
        }
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        
        // 只有非超级管理员且有 orgUid 时才加 orgUid 条件
        if (!Boolean.TRUE.equals(request.getSuperUser())
                && StringUtils.hasText(request.getOrgUid())
                && !platformLevelRequest) {
            // 部分实体（如 BaseEntityNoOrg）没有 orgUid 字段，避免 Criteria 构建时报错
            boolean hasOrgUidAttribute = true;
            try {
                root.get("orgUid");
            } catch (IllegalArgumentException ex) {
                hasOrgUidAttribute = false;
            }

            if (hasOrgUidAttribute) {
                predicates.add(criteriaBuilder.equal(root.get("orgUid"), request.getOrgUid()));
            }
        }
        
        return predicates;
    }

    /**
     * 无组织实体（BaseEntityNoOrg）使用的基础查询条件：仅过滤 deleted。
     * 允许不传 orgUid，避免 org 维度校验导致的查询失败。
     */
    protected static List<Predicate> getBasicPredicatesNoOrg(
            Root<?> root,
            CriteriaBuilder criteriaBuilder,
            BaseRequestNoOrg request,
            AuthService authService) {

        validateIsSuperUserPermission(request, authService);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
        return predicates;
    }

    /**
     * 获取带层级过滤的查询条件
     * 根据用户权限过滤可访问的数据层级
     * 
     * @param root 查询根对象
     * @param criteriaBuilder 条件构建器
     * @param request 请求对象
     * @param authService 认证服务
     * @param module 模块名称，如 TAG, QUICKREPLY 等
     * @return 基础查询条件列表
     */
    protected static List<Predicate> getBasicPredicatesWithLevel(
            Root<?> root, 
            CriteriaBuilder criteriaBuilder, 
            BaseRequest request, 
            AuthService authService,
            String module) {
        
        // 先获取基础条件
        List<Predicate> predicates = getBasicPredicates(root, criteriaBuilder, request, authService);
        
        UserEntity user = authService.getUser();
        if (user == null) {
            return predicates;
        }
        
        // 超级管理员可以访问所有数据
        if (user.isSuperUser() || Boolean.TRUE.equals(request.getSuperUser())) {
            return predicates;
        }
        
        // 获取权限服务
        PermissionService permissionService = ApplicationContextHolder.getBean(PermissionService.class);
        if (permissionService == null) {
            return predicates;
        }
        
        // 获取用户可访问的层级
        Set<String> accessibleLevels = permissionService.getAccessibleLevels(module, "READ");
        
        if (accessibleLevels.isEmpty()) {
            // 没有任何层级权限，只能访问自己创建的数据
            predicates.add(criteriaBuilder.equal(root.get("userUid"), user.getUid()));
        } else {
            // 构建层级过滤条件
            List<Predicate> levelPredicates = new ArrayList<>();
            
            // 用户可以访问的层级数据
            if (!accessibleLevels.isEmpty()) {
                levelPredicates.add(root.get("level").in(accessibleLevels));
            }
            
            // 用户始终可以访问自己创建的数据
            levelPredicates.add(criteriaBuilder.equal(root.get("userUid"), user.getUid()));
            
            // 平台级数据对所有登录用户可见（只读）
            if (!accessibleLevels.contains(LevelEnum.PLATFORM.name())) {
                levelPredicates.add(criteriaBuilder.equal(root.get("level"), LevelEnum.PLATFORM.name()));
            }
            
            // 使用 OR 组合层级条件
            predicates.add(criteriaBuilder.or(levelPredicates.toArray(new Predicate[0])));
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

    protected static void validateIsSuperUserPermission(BaseRequestNoOrg request, AuthService authService) {
        if (Boolean.TRUE.equals(request.getSuperUser())) {
            UserEntity user = authService.getUser();
            if (user == null) {
                throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
            }
            if (!user.isSuperUser()) {
                request.setSuperUser(false);
            }
        }
    }

    private static boolean isPlatformDefaultOrgRequest(BaseRequest request) {
        if (request == null) {
            return false;
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            return false;
        }
        boolean isDefaultOrg = BytedeskConsts.DEFAULT_ORGANIZATION_UID.equals(request.getOrgUid());
        boolean isPlatformLevel = LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel());
        return isDefaultOrg && isPlatformLevel;
    }

}
