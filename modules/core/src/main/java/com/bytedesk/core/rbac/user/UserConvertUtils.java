/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 11:55:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.role.RoleResponseSimple;
import com.bytedesk.core.utils.ApplicationContextHolder;

import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;

@UtilityClass
public class UserConvertUtils {

    private static String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate.trim();
            }
        }
        return null;
    }

    private static void addGrantedAuthorityIfPresent(Set<GrantedAuthority> authorities, String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        authorities.add(new SimpleGrantedAuthority(value.trim()));
    }

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setUid(userDetails.getUid());
        userResponse.setOrgUid(userDetails.getOrgUid());
        userResponse.setPlatform(userDetails.getPlatform());
        userResponse.setCreatedAt(userDetails.getCreatedAt());
        userResponse.setUpdatedAt(userDetails.getUpdatedAt());

        userResponse.setUsername(userDetails.getUsername());
        userResponse.setNickname(userDetails.getNickname());
        userResponse.setEmail(userDetails.getEmail());
        userResponse.setMobile(userDetails.getMobile());
        userResponse.setCountry(userDetails.getCountry());
        userResponse.setAvatar(userDetails.getAvatar());
        userResponse.setDescription(userDetails.getDescription());
        userResponse.setSex(parseUserSex(userDetails.getSex()));
        userResponse.setEnabled(Boolean.TRUE.equals(userDetails.getEnabled()));
        userResponse.setSuperUser(Boolean.TRUE.equals(userDetails.getSuperUser()));
        userResponse.setEmailVerified(Boolean.TRUE.equals(userDetails.getEmailVerified()));
        userResponse.setMobileVerified(Boolean.TRUE.equals(userDetails.getMobileVerified()));

        userResponse.setCurrentOrganization(convertToOrganizationResponseSimple(userDetails.getCurrentOrganization()));
        userResponse.setCurrentRoles(convertToRoleResponseSimples(userDetails.getCurrentRoles()));
        userResponse.setUserOrganizationRoles(convertToUserOrganizationRoleResponses(userDetails.getUserOrganizationRoles()));

        // UserDetailsImpl 已包含 authorities
        if (userDetails.getAuthorities() != null) {
            userResponse.setAuthorities(new HashSet<>(userDetails.getAuthorities()));
        }

        return userResponse;
    }

    public static UserResponse convertToUserResponse(UserEntity user) {
        if (user == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        // BaseResponse / BaseEntityNoOrg fields
        userResponse.setUid(user.getUid());
        userResponse.setUserUid(user.getUserUid());
        // 对于 User，orgUid 语义上等同于当前组织
        userResponse.setOrgUid(user.getOrgUid());
        userResponse.setLevel(user.getLevel());
        userResponse.setPlatform(user.getPlatform());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        // UserResponse fields
        userResponse.setNum(user.getNum());
        userResponse.setUsername(user.getUsername());
        userResponse.setNickname(user.getNickname());
        userResponse.setEmail(user.getEmail());
        userResponse.setMobile(user.getMobile());
        userResponse.setCountry(user.getCountry());
        userResponse.setAvatar(user.getAvatar());
        userResponse.setDescription(user.getDescription());
        userResponse.setSex(parseUserSex(user.getSex()));
        userResponse.setEnabled(user.isEnabled());
        userResponse.setSuperUser(user.isSuperUser());
        userResponse.setEmailVerified(user.isEmailVerified());
        userResponse.setMobileVerified(user.isMobileVerified());
        userResponse.setRegisterSource(user.getRegisterSource());

        // 特别注意：currentOrganization 必须手动转换，避免 ModelMapper / 懒加载导致字段缺失或循环引用
        userResponse.setCurrentOrganization(convertToOrganizationResponseSimple(user.getCurrentOrganization()));

        // currentRoles 手动映射（避免间接使用 getModelMapper）
        userResponse.setCurrentRoles(convertToRoleResponseSimples(user.getCurrentRoles()));

        // userOrganizationRoles（多组织角色关系）
        userResponse.setUserOrganizationRoles(convertToUserOrganizationRoleResponses(user.getUserOrganizationRoles()));

        // authorities
        userResponse.setAuthorities(filterUserGrantedAuthorities(user));

        // 设置密码修改时间
        userResponse.setPasswordModifiedAt(user.getPasswordModifiedAtString());

        return userResponse;
    }

    private static Set<UserOrganizationRoleResponse> convertToUserOrganizationRoleResponses(Set<UserOrganizationRoleEntity> userOrganizationRoles) {
        if (userOrganizationRoles == null) {
            return null;
        }

        Set<UserOrganizationRoleResponse> responses = new HashSet<>();
        for (UserOrganizationRoleEntity uor : userOrganizationRoles) {
            if (uor == null || uor.getOrganization() == null) {
                continue;
            }

            UserOrganizationRoleResponse resp = new UserOrganizationRoleResponse();
            resp.setOrganization(convertToOrganizationResponseSimple(uor.getOrganization()));

            // roles 是 LAZY；如果未初始化，不强制触发加载，避免 detached entity 场景抛 LazyInitializationException
            if (Hibernate.isInitialized(uor.getRoles()) && uor.getRoles() != null) {
                resp.setRoles(uor.getRoles().stream().map(UserConvertUtils::convertToRoleResponseManual).collect(Collectors.toSet()));
            } else {
                resp.setRoles(null);
            }

            responses.add(resp);
        }

        return responses;
    }

    private static RoleResponse convertToRoleResponseManual(RoleEntity role) {
        if (role == null) {
            return null;
        }

        RoleResponse resp = new RoleResponse();
        resp.setUid(role.getUid());
        resp.setOrgUid(role.getOrgUid());
        resp.setUserUid(role.getUserUid());
        resp.setLevel(role.getLevel());
        resp.setPlatform(role.getPlatform());
        resp.setCreatedAt(role.getCreatedAt());
        resp.setUpdatedAt(role.getUpdatedAt());
        resp.setExpiresAt(role.getExpiresAt());

        resp.setName(role.getName());
        resp.setValue(role.getValue());
        resp.setDescription(role.getDescription());
        resp.setSystem(role.getSystem());
        // authorities 不在这里展开（避免额外懒加载/体积膨胀）
        resp.setAuthorities(null);
        return resp;
    }

    private static UserEntity.Sex parseUserSex(String sexValue) {
        if (sexValue == null || sexValue.isBlank()) {
            return UserEntity.Sex.UNKNOWN;
        }
        try {
            return UserEntity.Sex.valueOf(sexValue.trim().toUpperCase());
        } catch (Exception e) {
            return UserEntity.Sex.UNKNOWN;
        }
    }

    private static OrganizationResponseSimple convertToOrganizationResponseSimple(OrganizationEntity organization) {
        if (organization == null) {
            return null;
        }
        OrganizationResponseSimple response = OrganizationResponseSimple.builder()
                .uid(organization.getUid())
                .name(organization.getName())
                .logo(organization.getLogo())
                .code(organization.getCode())
                .description(organization.getDescription())
                .verifyStatus(organization.getVerifyStatus())
                .enabled(organization.getEnabled())
            .vipExpireDate(organization.getVipExpireDate())
            .vipExpireLoginCheckEnabled(organization.getVipExpireLoginCheckEnabled())
                .customServerEnabled(organization.getCustomServerEnabled())
                .customServerHost(organization.getCustomServerHost())
                .build();

        // owner user（OrganizationEntity.user 默认为 LAZY，未初始化时不强行触发加载）
        if (organization.getUser() != null && Hibernate.isInitialized(organization.getUser())) {
            response.setUser(convertToUserResponseSimpleManual(organization.getUser()));
        }

        return response;
    }

    /**
     * Public wrapper for converting OrganizationEntity to OrganizationResponseSimple.
     * Keep the actual mapping logic in the existing private method to avoid duplication.
     */
    public static OrganizationResponseSimple toOrganizationResponseSimple(OrganizationEntity organization) {
        return convertToOrganizationResponseSimple(organization);
    }

    private static UserResponseSimple convertToUserResponseSimpleManual(UserEntity user) {
        if (user == null) {
            return null;
        }
        return UserResponseSimple.builder()
                .uid(user.getUid())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .description(user.getDescription())
                .enabled(user.isEnabled())
                .superUser(user.isSuperUser())
                .build();
    }

    private static Set<RoleResponseSimple> convertToRoleResponseSimples(Set<RoleEntity> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream().map(UserConvertUtils::convertToRoleResponseSimpleManual).collect(Collectors.toSet());
    }

    private static RoleResponseSimple convertToRoleResponseSimpleManual(RoleEntity role) {
        if (role == null) {
            return null;
        }
        RoleResponseSimple roleResponse = new RoleResponseSimple();
        roleResponse.setUid(role.getUid());
        roleResponse.setOrgUid(role.getOrgUid());
        roleResponse.setUserUid(role.getUserUid());
        roleResponse.setLevel(role.getLevel());
        roleResponse.setPlatform(role.getPlatform());
        roleResponse.setCreatedAt(role.getCreatedAt());
        roleResponse.setUpdatedAt(role.getUpdatedAt());
        roleResponse.setExpiresAt(role.getExpiresAt());

        roleResponse.setName(role.getName());
        roleResponse.setValue(role.getValue());
        roleResponse.setDescription(role.getDescription());
        roleResponse.setSystem(role.getSystem());
        return roleResponse;
    }

    public static Set<GrantedAuthority> filterUserGrantedAuthorities(UserEntity user) {
        // 添加用户的权限，使用方式，如：@PreAuthorize("hasAnyAuthority('SUPER', 'ADMIN')")
        Set<GrantedAuthority> authorities = new HashSet<>();
        // Set<GrantedAuthority> authorities = user.getUserOrganizationRoles().stream()
        //         .filter(uor -> uor.getOrganization().equals(user.getCurrentOrganization())) // 过滤步骤
        //         .flatMap(uor -> uor.getRoles().stream()
        //                 .flatMap(role -> role.getAuthorities().stream()
        //                         .map(authority -> new SimpleGrantedAuthority(authority.getValue()))))
        //         .collect(Collectors.toSet());
        // log.info("authorities only: {}", authorities);

        // 添加用户的角色，使用方式，在Controller或Service方法配置注解：@PreAuthorize("hasAnyRole('ADMIN', 'SUPER', 'CS')")，
        // 无需加角色前缀ROLE_，因为已经在RoleEntity的name中配置了
        // authorities.addAll(user.getUserOrganizationRoles().stream()
        //         .filter(uor -> uor.getOrganization().equals(user.getCurrentOrganization())) // 过滤步骤
        //         .flatMap(uor -> uor.getRoles().stream()
        //                 .map(role -> new SimpleGrantedAuthority(role.getName())))
        //         .collect(Collectors.toSet()));
        // log.info("authorities with roles: {}", authorities);

        // currentRoles already reflects the active organization; merge their authorities and role names
        if (user.getCurrentRoles() != null) {
            user.getCurrentRoles().forEach(role -> {
                if (role == null) {
                    return;
                }

				// Role expiration: expired role should not grant authorities.
				ZonedDateTime expiresAt = role.getExpiresAt();
				if (expiresAt != null && expiresAt.isBefore(ZonedDateTime.now())) {
					return;
				}
                if (role.getAuthorities() != null) {
                    role.getAuthorities().forEach(authority -> {
                        if (authority == null) {
                            return;
                        }
                        addGrantedAuthorityIfPresent(
                                authorities,
                                firstNonBlank(authority.getValue(), authority.getUid()));
                    });
                }

                // role.value 可能为空（自定义角色未填），兜底到 role.name（必填）/uid
                addGrantedAuthorityIfPresent(authorities, firstNonBlank(role.getValue(), role.getUid()));
            });
        }

        // 兼容处理：
        // - 旧格式：MODULE_LEVEL_ACTION（例如 TAG_PLATFORM_READ）
        // - 新格式：MODULE_ACTION（例如 TAG_READ）
        //
        // 目标：系统内部不再依赖权限字符串中的层级，但要保证旧数据/旧 @PreAuthorize 仍可临时工作。
        // 策略：
        // 1) 如果存在旧格式权限，归一化出 MODULE_ACTION 追加进去。
        // 2) 如果存在新格式权限，派生出旧格式 MODULE_LEVEL_ACTION 追加进去（仅作为别名）。
        // expandAndNormalizePermissionAuthorities(authorities);

        return authorities;
    }

    public static UserProtobuf convertToUserProtobuf(UserEntity user) {
        return getModelMapper().map(user, UserProtobuf.class);
    }

    public static String convertToUserProtobufString(UserEntity user) {
        UserProtobuf userProtobuf = convertToUserProtobuf(user);
        return JSON.toJSONString(userProtobuf);
    }

}
