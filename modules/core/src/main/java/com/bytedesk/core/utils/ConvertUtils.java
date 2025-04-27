/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 22:46:45
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
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.AuthorityResponse;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadResponse;

public class ConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper(); // 添加静态ModelMapper实例

    private ConvertUtils() {
    }

    public static UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        // 无需进行authorities转换，因为UserDetailsImpl中已经包含了authorities
        return modelMapper.map(userDetails, UserResponse.class);
    }

    public static UserResponse convertToUserResponse(UserEntity user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        Set<GrantedAuthority> authorities = filterUserGrantedAuthorities(user);
        userResponse.setAuthorities(authorities);
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
        return modelMapper.map(user, UserProtobuf.class);
    }

    public static String convertToUserProtobufString(UserEntity user) {
        UserProtobuf userProtobuf = convertToUserProtobuf(user);
        return JSON.toJSONString(userProtobuf);
    }

    public static ThreadProtobuf convertToThreadProtobuf(ThreadEntity thread) {
        ThreadProtobuf threadProtobuf = modelMapper.map(thread, ThreadProtobuf.class);
        //
        UserProtobuf user = JSON.parseObject(thread.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        threadProtobuf.setUser(user);
        //
        return threadProtobuf;
    }

    public static ThreadResponse convertToThreadResponse(ThreadEntity thread) {
        ThreadResponse threadResponse = modelMapper.map(thread, ThreadResponse.class);
        // 用于更新robot-agent-llm配置，不能修改为UserProtobuf,
        // 否则会内容缺失，因为可能为RobotProtobuf类型, 其中含有llm字段
        // if (thread.getAgent() != null) {
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // threadResponse.setAgent(agent);
        // }
        // agent
        if (thread.getAgent() != null) {
            UserProtobuf agent = UserProtobuf.fromJson(thread.getAgent()); // JSON.parseObject(thread.getAgent(),
                                                                           // UserProtobuf.class);
            threadResponse.setAgentProtobuf(agent);
        }
        if (thread.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(thread.getUser()); // JSON.parseObject(thread.getUser(),
                                                                         // UserProtobuf.class);
            threadResponse.setUser(user);
        }
        // robot
        if (thread.getRobot() != null) {
            UserProtobuf robot = UserProtobuf.fromJson(thread.getRobot()); // JSON.parseObject(thread.getRobot(),
                                                                           // UserProtobuf.class);
            threadResponse.setRobot(robot);
        }
        if (thread.getWorkgroup() != null) {
            UserProtobuf workgroup = UserProtobuf.fromJson(thread.getWorkgroup()); // JSON.parseObject(thread.getWorkgroup(),
                                                                                   // UserProtobuf.class);
            threadResponse.setWorkgroup(workgroup);
        }
        if (thread.getInvites() != null) {
            // 将string[]为UserProtobuf[]，并存入threadResponse.setInvites()中
            for (String invite : thread.getInvites()) {
                UserProtobuf inviteUser = UserProtobuf.fromJson(invite); // JSON.parseObject(invite,
                                                                         // UserProtobuf.class);
                threadResponse.getInvites().add(inviteUser);
            }
        }
        if (thread.getMonitors() != null) {
            for (String monitor : thread.getMonitors()) {
                UserProtobuf monitorUser = UserProtobuf.fromJson(monitor); // JSON.parseObject(monitor,
                                                                           // UserProtobuf.class);
                threadResponse.getMonitors().add(monitorUser);
            }
        }
        if (thread.getAssistants() != null) {
            for (String assistant : thread.getAssistants()) {
                UserProtobuf assistantUser = UserProtobuf.fromJson(assistant); // JSON.parseObject(assistant,
                                                                               // UserProtobuf.class);
                threadResponse.getAssistants().add(assistantUser);
            }
        }
        //
        return threadResponse;
    }

    public static RoleResponse convertToRoleResponse(RoleEntity entity) {
        // return modelMapper.map(role, RoleResponse.class);
        RoleResponse roleResponse = modelMapper.map(entity, RoleResponse.class);
        // 将Set<AuthorityEntity> authorities转换为Set<AuthorityResponse> authorities
        roleResponse.setAuthorities(
                entity.getAuthorities().stream()
                        .map(authorityEntity -> ConvertUtils
                                .convertToAuthorityResponse(authorityEntity))
                        .collect(Collectors.toSet()));
        return roleResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageEntity message) {

        MessageResponse messageResponse = modelMapper.map(message, MessageResponse.class);
        //
        if (message.getUser() != null) {
            // UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
            UserProtobuf user = UserProtobuf.fromJson(message.getUser());
            if (user.getExtra() == null) {
                user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
            }
            messageResponse.setUser(user);
        }

        // extra格式不固定，前端需要根据type字段来解析，所以此处不能使用MessageExtra
        // extra
        // if (message.getExtra() != null) {
        // // MessageExtra extra = JSON.parseObject(message.getExtra(),
        // MessageExtra.class);
        // MessageExtra extra = MessageExtra.fromJson(message.getExtra());
        // if (extra.getFeedback() == null) {
        // extra.setFeedback(BytedeskConsts.EMPTY_JSON_STRING);
        // }
        // messageResponse.setExtra(extra);
        // }

        // thread
        if (message.getThread() != null) {
            ThreadResponse thread = convertToThreadResponse(message.getThread());
            messageResponse.setThread(thread);
        }

        return messageResponse;
    }

    public static AuthorityResponse convertToAuthorityResponse(AuthorityEntity authorityEntity) {
        return modelMapper.map(authorityEntity, AuthorityResponse.class);
    }

    public static BytedeskPropertiesResponse convertToBytedeskPropertiesResponse(
            BytedeskProperties bytedeskProperties) {
        // return modelMapper.map(bytedeskProperties, BytedeskPropertiesResponse.class);
        BytedeskPropertiesResponse response = modelMapper.map(bytedeskProperties, BytedeskPropertiesResponse.class);
        // 明确设置Custom所有字段的值，确保从配置中获取
        if (bytedeskProperties.getCustom() != null) {
            response.getCustom().setShowRightCornerChat(bytedeskProperties.getCustom().getShowRightCornerChat());
            response.getCustom().setLoginUsernameEnable(bytedeskProperties.getCustom().getLoginUsernameEnable());
            response.getCustom().setLoginMobileEnable(bytedeskProperties.getCustom().getLoginMobileEnable());
            response.getCustom().setLoginEmailEnable(bytedeskProperties.getCustom().getLoginEmailEnable());
            response.getCustom().setLoginScanEnable(bytedeskProperties.getCustom().getLoginScanEnable());
            response.getCustom().setDocUrlShow(bytedeskProperties.getCustom().getDocUrlShow());
            response.getCustom().setDocUrl(bytedeskProperties.getCustom().getDocUrl());
            response.getCustom().setEnabled(bytedeskProperties.getCustom().getEnabled());
            response.getCustom().setName(bytedeskProperties.getCustom().getName());
            response.getCustom().setLogo(bytedeskProperties.getCustom().getLogo());
            response.getCustom().setDescription(bytedeskProperties.getCustom().getDescription());
            response.getCustom().setPrivacyPolicyUrl(bytedeskProperties.getCustom().getPrivacyPolicyUrl());
            response.getCustom().setTermsOfServiceUrl(bytedeskProperties.getCustom().getTermsOfServiceUrl());
        }
        return response;
    }

    public static UploadResponse convertToUploadResponse(UploadEntity entity) {
        UploadResponse uploadResponse = modelMapper.map(entity, UploadResponse.class);
        // 上一行没有自动初始化isLlm字段，所以这里需要手动设置
        // uploadResponse.setIsLlm(entity.isLlm());
        return uploadResponse;
    }

}
