/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 13:47:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserOrganizationRoleResponse;
import com.bytedesk.core.rbac.organization.OrganizationResponseSimple;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadResponse;
import com.bytedesk.core.workflow.WorkflowEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        // 无需进行authorities转换，因为UserDetailsImpl中已经包含了authorities
        return getModelMapper().map(userDetails, UserResponse.class);
    }

    public static UserResponse convertToUserResponse(UserEntity user) {
        UserResponse userResponse = getModelMapper().map(user, UserResponse.class);
        
        // 手动处理 userOrganizationRoles 映射
        if (user.getUserOrganizationRoles() != null) {
            Set<UserOrganizationRoleResponse> userOrgRoleResponses = user.getUserOrganizationRoles().stream()
                .map(uor -> {
                    UserOrganizationRoleResponse response = new UserOrganizationRoleResponse();
                    // 映射组织信息
                    if (uor.getOrganization() != null) {
                        OrganizationResponseSimple orgResponse = new OrganizationResponseSimple();
                        orgResponse.setUid(uor.getOrganization().getUid());
                        orgResponse.setName(uor.getOrganization().getName());
                        orgResponse.setLogo(uor.getOrganization().getLogo());
                        orgResponse.setCode(uor.getOrganization().getCode());
                        orgResponse.setDescription(uor.getOrganization().getDescription());
                        orgResponse.setVerifyStatus(uor.getOrganization().getVerifyStatus());
                        response.setOrganization(orgResponse);
                    }
                    // 映射角色信息
                    if (uor.getRoles() != null) {
                        Set<RoleResponse> roleResponses = uor.getRoles().stream()
                            .map(role -> {
                                RoleResponse roleResponse = new RoleResponse();
                                roleResponse.setUid(role.getUid());
                                roleResponse.setName(role.getName());
                                roleResponse.setDescription(role.getDescription());
                                roleResponse.setLevel(role.getLevel());
                                return roleResponse;
                            })
                            .collect(Collectors.toSet());
                        response.setRoles(roleResponses);
                    }
                    return response;
                })
                .collect(Collectors.toSet());
            userResponse.setUserOrganizationRoles(userOrgRoleResponses);
        }
        
        Set<GrantedAuthority> authorities = filterUserGrantedAuthorities(user);
        userResponse.setAuthorities(authorities);
        
        // 设置密码修改时间
        userResponse.setPasswordModifiedAt(user.getPasswordModifiedAtString());
        
        return userResponse;
    }

    public static Set<GrantedAuthority> filterUserGrantedAuthorities(UserEntity user) {
        // 添加用户的权限，使用方式，如：@PreAuthorize("hasAnyAuthority('SUPER', 'ADMIN')")
        Set<GrantedAuthority> authorities = user.getUserOrganizationRoles().stream()
                .filter(uor -> uor.getOrganization().equals(user.getCurrentOrganization())) // 过滤步骤
                .flatMap(uor -> uor.getRoles().stream()
                        .flatMap(role -> role.getAuthorities().stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.getValue()))))
                .collect(Collectors.toSet());
        // log.info("authorities only: {}", authorities);

        // 添加用户的角色，使用方式，在Controller或Service方法配置注解：@PreAuthorize("hasAnyRole('ADMIN',
        // 'SUPER', 'CS')")，
        // 无需加角色前缀ROLE_，因为已经在RoleEntity的name中配置了
        authorities.addAll(user.getUserOrganizationRoles().stream()
                .filter(uor -> uor.getOrganization().equals(user.getCurrentOrganization())) // 过滤步骤
                .flatMap(uor -> uor.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName())))
                .collect(Collectors.toSet()));
        // log.info("authorities with roles: {}", authorities);

        return authorities;
    }

    public static UserProtobuf convertToUserProtobuf(UserEntity user) {
        return getModelMapper().map(user, UserProtobuf.class);
    }

    public static UserProtobuf convertToUserProtobuf(WorkflowEntity workflow) {
        UserProtobuf userProtobuf = getModelMapper().map(workflow, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.WORKFLOW.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufString(WorkflowEntity workflow) {
        UserProtobuf userProtobuf = convertToUserProtobuf(workflow);
        return JSON.toJSONString(userProtobuf);
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
        ThreadResponse threadResponse = getModelMapper().map(thread, ThreadResponse.class);
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
        if (thread.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
            if (user != null) {
                threadResponse.setUser(user);
            }
        }
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
        // 手动设置状态判断字段
        // threadResponse.setNewStatus(thread.isNew());
        // threadResponse.setRobotingStatus(thread.isRoboting());
        // threadResponse.setQueuingStatus(thread.isQueuing());
        // threadResponse.setOfflineStatus(thread.isOffline());
        // threadResponse.setChattingStatus(thread.isChatting());
        // threadResponse.setTimeoutStatus(thread.isTimeout());
        // threadResponse.setClosedStatus(thread.isClosed());
        
        // // 手动设置类型判断字段
        // threadResponse.setCustomerServiceType(thread.isCustomerService());
        // threadResponse.setRobotType(thread.isRobotType());
        // threadResponse.setWorkgroupType(thread.isWorkgroupType());
        // threadResponse.setAgentType(thread.isAgentType());
        // threadResponse.setUnifiedType(thread.isUnifiedType());
        
        // // 手动设置渠道判断字段
        // threadResponse.setWeChatMpChannel(thread.isWeChatMp());
        // threadResponse.setWeChatMiniChannel(thread.isWeChatMini());
        
        // 手动设置业务逻辑字段
        threadResponse.setRobotToAgent(thread.isRobotToAgent());
        threadResponse.setValid(thread.isValid());
        
        // 手动设置消息统计字段
        threadResponse.setAllMessageCount(thread.getAllMessageCount());
        threadResponse.setVisitorMessageCount(thread.getVisitorMessageCount());
        threadResponse.setAgentMessageCount(thread.getAgentMessageCount());
        threadResponse.setSystemMessageCount(thread.getSystemMessageCount());
        threadResponse.setRobotMessageCount(thread.getRobotMessageCount());
        
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
