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
package com.bytedesk.core.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.config.properties.BytedeskPropertiesResponse;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadEntity;
import com.bytedesk.core.message_unread.MessageUnreadResponse;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.AuthorityResponse;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.role.RoleResponseSimple;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.core.thread.ThreadContent;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

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
        // 无需进行authorities转换，因为UserDetailsImpl中已经包含了authorities
        return getModelMapper().map(userDetails, UserResponse.class);
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

        // authorities
        userResponse.setAuthorities(filterUserGrantedAuthorities(user));

        // 设置密码修改时间
        userResponse.setPasswordModifiedAt(user.getPasswordModifiedAtString());

        return userResponse;
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
        return roles.stream().map(ConvertUtils::convertToRoleResponseSimpleManual).collect(Collectors.toSet());
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


    // private static void expandAndNormalizePermissionAuthorities(Set<GrantedAuthority> authorities) {
    //     if (authorities == null || authorities.isEmpty()) {
    //         return;
    //     }

    //     Set<String> rawValues = authorities.stream()
    //             .map(GrantedAuthority::getAuthority)
    //             .filter(StringUtils::hasText)
    //             .collect(Collectors.toSet());

    //     Set<String> derived = new HashSet<>();

    //     for (String value : rawValues) {
    //         // 跳过角色名（例如 SUPER/ADMIN/AGENT 等），只处理权限值。
    //         if (!value.contains("_")) {
    //             continue;
    //         }

    //         // 旧 -> 新：MODULE_LEVEL_ACTION => MODULE_ACTION
    //         String normalized = normalizeLevelPermission(value);
    //         if (StringUtils.hasText(normalized) && !normalized.equals(value)) {
    //             derived.add(normalized);
    //         }

    //         // 新 -> 旧：MODULE_ACTION => MODULE_LEVEL_ACTION（仅用于兼容老的 hasAnyAuthority('..._PLATFORM_...')）
    //         if (isModuleActionPermission(value)) {
    //             derived.addAll(expandToLegacyLevels(value));
    //         }
    //     }

    //     for (String v : derived) {
    //         authorities.add(new SimpleGrantedAuthority(v));
    //     }
    // }

    // private static boolean isModuleActionPermission(String value) {
    //     // 期望格式：<MODULE>_<ACTION>
    //     // MODULE 可能包含下划线（例如 WORKFLOW_LOG_READ），但 ACTION 一般是最后一个 token。
    //     int lastUnderscore = value.lastIndexOf('_');
    //     if (lastUnderscore <= 0 || lastUnderscore >= value.length() - 1) {
    //         return false;
    //     }
    //     String action = value.substring(lastUnderscore + 1);
    //     // 常见动作集合（项目里普遍使用）
    //     return Set.of("READ", "CREATE", "UPDATE", "DELETE", "EXPORT").contains(action.toUpperCase());
    // }

    // private static String normalizeLevelPermission(String value) {
    //     // 将：<MODULE>_<LEVEL>_<ACTION> 归一化为：<MODULE>_<ACTION>
    //     int lastUnderscore = value.lastIndexOf('_');
    //     if (lastUnderscore <= 0 || lastUnderscore >= value.length() - 1) {
    //         return value;
    //     }
    //     String action = value.substring(lastUnderscore + 1);
    //     String prefix = value.substring(0, lastUnderscore); // 去掉 _ACTION

    //     int secondLastUnderscore = prefix.lastIndexOf('_');
    //     if (secondLastUnderscore <= 0 || secondLastUnderscore >= prefix.length() - 1) {
    //         return value;
    //     }

    //     String levelCandidate = prefix.substring(secondLastUnderscore + 1).toUpperCase();
    //     if (!Set.of("PLATFORM", "ORGANIZATION", "DEPARTMENT", "WORKGROUP", "AGENT", "USER").contains(levelCandidate)) {
    //         return value;
    //     }

    //     String modulePrefix = prefix.substring(0, secondLastUnderscore + 1); // 保留末尾 _
    //     return modulePrefix + action;
    // }

    // private static Set<String> expandToLegacyLevels(String basePermission) {
    //     int lastUnderscore = basePermission.lastIndexOf('_');
    //     if (lastUnderscore <= 0 || lastUnderscore >= basePermission.length() - 1) {
    //         return Set.of();
    //     }
    //     String action = basePermission.substring(lastUnderscore + 1);
    //     String modulePrefix = basePermission.substring(0, lastUnderscore + 1);

    //     Set<String> expanded = new HashSet<>();
    //     expanded.add(modulePrefix + "PLATFORM_" + action);
    //     expanded.add(modulePrefix + "ORGANIZATION_" + action);
    //     expanded.add(modulePrefix + "DEPARTMENT_" + action);
    //     expanded.add(modulePrefix + "WORKGROUP_" + action);
    //     expanded.add(modulePrefix + "AGENT_" + action);
    //     expanded.add(modulePrefix + "USER_" + action);
    //     return expanded;
    // }

    public static UserProtobuf convertToUserProtobuf(UserEntity user) {
        return getModelMapper().map(user, UserProtobuf.class);
    }

    public static String convertToUserProtobufString(UserEntity user) {
        UserProtobuf userProtobuf = convertToUserProtobuf(user);
        return JSON.toJSONString(userProtobuf);
    }

    public static ThreadProtobuf convertToThreadProtobuf(ThreadEntity thread) {
        ThreadProtobuf threadProtobuf = getModelMapper().map(thread, ThreadProtobuf.class);
        //
        if (thread.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                threadProtobuf.setUser(user);
            }
        }
        //
        return threadProtobuf;
    }

    public static ThreadResponse convertToThreadResponse(ThreadEntity thread) {
        // ensureThreadResponseTypeMapConfigured(modelMapper);
        ThreadResponse threadResponse = getModelMapper().map(thread, ThreadResponse.class);

        // 兼容：thread.content 可能已升级为 ThreadContent JSON；对外仍返回可读摘要
        ThreadContent tc = ThreadContent.fromStored(thread.getContent());
        if (tc != null) {
            threadResponse.setContentObject(tc);
            threadResponse.setContent(tc.getDisplayText());
        }
        // 用于更新robot-agent-llm配置，不能修改为UserProtobuf,
        // 否则会内容缺失，因为可能为RobotProtobuf类型, 其中含有llm字段
        // if (thread.getAgent() != null) {
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // threadResponse.setAgent(agent);
        // }
        // agent
        // if (thread.getAgent() != null) {
        //     UserProtobuf agent = UserProtobuf.fromJson(thread.getAgent());
        //     threadResponse.setAgentProtobuf(agent);
        // }
        UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
        if (user != null) {
            threadResponse.setUser(user);
        }
        // if (thread.getOwner() != null) {
        //     if (Hibernate.isInitialized(thread.getOwner())) {
        //         threadResponse.setOwner(convertToUserProtobuf(thread.getOwner()));
        //     } else {
        //         threadResponse.setOwner(null);
        //     }
        // }
        // robot
        // if (thread.getRobot() != null) {
        //     UserProtobuf robot = UserProtobuf.fromJson(thread.getRobot());
        //     threadResponse.setRobotProtobuf(robot);
        // }
        // if (thread.getWorkgroup() != null) {
        //     UserProtobuf workgroup = UserProtobuf.fromJson(thread.getWorkgroup());
        //     threadResponse.setWorkgroup(workgroup);
        // }
        if (thread.getInvites() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getInvites().clear();
            // 将string[]为UserProtobuf[]，并存入threadResponse.setInvites()中
            for (String invite : thread.getInvites()) {
                UserProtobuf inviteUser = UserProtobuf.fromJson(invite);
                if (inviteUser != null) {
                    threadResponse.getInvites().add(inviteUser);
                }
            }
        }
        if (thread.getMonitors() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getMonitors().clear();
            for (String monitor : thread.getMonitors()) {
                UserProtobuf monitorUser = UserProtobuf.fromJson(monitor);
                if (monitorUser != null) {
                    threadResponse.getMonitors().add(monitorUser);
                }
            }
        }
        if (thread.getAssistants() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getAssistants().clear();
            for (String assistant : thread.getAssistants()) {
                UserProtobuf assistantUser = UserProtobuf.fromJson(assistant);
                if (assistantUser != null) {
                    threadResponse.getAssistants().add(assistantUser);
                }
            }
        }
        
        // 手动设置业务逻辑字段
        threadResponse.setRobotToAgent(thread.isRobotToAgent());
        // 关闭来源类型
        threadResponse.setCloseType(thread.getCloseType());
        //
        return threadResponse;
    }

    public static ThreadResponseSimple convertToThreadResponseSimple(ThreadEntity thread) {
        ThreadContent tc = ThreadContent.fromStored(thread.getContent());
        String displayContent = tc != null ? tc.getDisplayText() : thread.getContent();
        ThreadResponseSimple threadResponse = ThreadResponseSimple.builder()
            .topic(thread.getTopic())
            .content(displayContent)
            .type(thread.getType())
            .status(thread.getStatus())
            .top(thread.getTop())
            .unread(thread.getUnread())
            .mute(thread.getMute())
            .hide(thread.getHide())
            .star(thread.getStar())
            .fold(thread.getFold())
            .closeType(thread.getCloseType())
            .note(thread.getNote())
            // .offline(thread.getOffline())
            .channel(thread.getChannel())
            .extra(thread.getExtra())
            .agent(thread.getAgent())
            .workgroup(thread.getWorkgroup())
        .build();
        if (tc != null) {
            threadResponse.setContentObject(tc);
        }
        // ThreadResponseSimple threadResponse = getModelMapper().map(thread, ThreadResponseSimple.class);
        threadResponse.setUser(thread.getUserProtobuf());
        // 
        return threadResponse;
    }
    
    public static RoleResponse convertToRoleResponse(RoleEntity entity) {
        // return modelMapper.map(role, RoleResponse.class);
        RoleResponse roleResponse = getModelMapper().map(entity, RoleResponse.class);
        // 将Set<AuthorityEntity> authorities转换为Set<AuthorityResponse> authorities
        roleResponse.setAuthorities(
                entity.getAuthorities().stream()
                        .map(authorityEntity -> ConvertUtils
                                .convertToAuthorityResponse(authorityEntity))
                        .collect(Collectors.toSet()));
        return roleResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageEntity message) {

        MessageResponse messageResponse = getModelMapper().map(message, MessageResponse.class);
        //
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                messageResponse.setUser(user);
            }
        }

        // thread
        if (message.getThread() != null) {
            ThreadResponse thread = convertToThreadResponse(message.getThread());
            messageResponse.setThread(thread);
        }

        return messageResponse;
    }

    public static AuthorityResponse convertToAuthorityResponse(AuthorityEntity authorityEntity) {
        return getModelMapper().map(authorityEntity, AuthorityResponse.class);
    }

    public static BytedeskPropertiesResponse convertToBytedeskPropertiesResponse(
            BytedeskProperties bytedeskProperties) {
        // return modelMapper.map(bytedeskProperties, BytedeskPropertiesResponse.class);
        BytedeskPropertiesResponse response = getModelMapper().map(bytedeskProperties, BytedeskPropertiesResponse.class);
        
        // 确保使用加密后的licenseKey
        response.setLicenseKey(bytedeskProperties.getLicenseKey());
        
        // 明确设置Custom所有字段的值，确保从配置中获取
        if (bytedeskProperties.getCustom() != null) {
            response.getCustom().setMqttWebsocketUrl(bytedeskProperties.getCustom().getMqttWebsocketUrl());
            response.getCustom().setShowRightCornerChat(bytedeskProperties.getCustom().getShowRightCornerChat());
            response.getCustom().setLoginUsernameEnable(bytedeskProperties.getCustom().getLoginUsernameEnable());
            response.getCustom().setLoginMaxRetryCount(bytedeskProperties.getCustom().getLoginMaxRetryCount());
            response.getCustom().setLoginMaxRetryLockTime(bytedeskProperties.getCustom().getLoginMaxRetryLockTime());
            response.getCustom().setLoginMobileEnable(bytedeskProperties.getCustom().getLoginMobileEnable());
            // response.getCustom().setLoginEmailEnable(bytedeskProperties.getCustom().getLoginEmailEnable());
            response.getCustom().setLoginScanEnable(bytedeskProperties.getCustom().getLoginScanEnable());
            response.getCustom().setDocUrlShow(bytedeskProperties.getCustom().getDocUrlShow());
            response.getCustom().setDocUrl(bytedeskProperties.getCustom().getDocUrl());
            response.getCustom().setEnabled(bytedeskProperties.getCustom().getEnabled());
            response.getCustom().setName(bytedeskProperties.getCustom().getName());
            response.getCustom().setLogo(bytedeskProperties.getCustom().getLogo());
            response.getCustom().setDescription(bytedeskProperties.getCustom().getDescription());
            response.getCustom().setPrivacyPolicyUrl(bytedeskProperties.getCustom().getPrivacyPolicyUrl());
            response.getCustom().setTermsOfServiceUrl(bytedeskProperties.getCustom().getTermsOfServiceUrl());
            // 
            response.getCustom().setAllowRegister(bytedeskProperties.getCustom().getAllowRegister());
            response.getCustom().setForceValidateMobile(bytedeskProperties.getCustom().getForceValidateMobile());
            response.getCustom().setForceValidateEmail(bytedeskProperties.getCustom().getForceValidateEmail());
        }

        return response;
    }

    public static UploadResponse convertToUploadResponse(UploadEntity entity) {
        UploadResponse uploadResponse = getModelMapper().map(entity, UploadResponse.class);
        // 上一行没有自动初始化isLlm字段，所以这里需要手动设置
        // uploadResponse.setIsLlm(entity.isLlm());
        return uploadResponse;
    }

    public static MessageUnreadResponse convertToMessageUnreadResponse(MessageUnreadEntity message) {

        MessageUnreadResponse messageResponse = getModelMapper().map(message, MessageUnreadResponse.class);
        //
        if (message.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                messageResponse.setUser(user);
            }
        }

        // thread
        if (message.getThread() != null) {
            ThreadResponse thread = ConvertUtils.convertToThreadResponse(message.getThread());
            messageResponse.setThread(thread);
        }

        return messageResponse;
    }
    
}
