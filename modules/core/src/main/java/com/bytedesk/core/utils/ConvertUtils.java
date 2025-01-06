/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-09 12:21:51
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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnread;
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

public class ConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper(); // 添加静态ModelMapper实例

    private ConvertUtils() {}

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

        // 添加用户的角色，使用方式，在Controller或Service方法配置注解：@PreAuthorize("hasAnyRole('ADMIN', 'SUPER', 'CS')")，
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
        //
        UserProtobuf user = JSON.parseObject(thread.getUser(), UserProtobuf.class);
        threadResponse.setUser(user);

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

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageUnread message) {

        MessageResponse messageResponse = modelMapper.map(message, MessageResponse.class);

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    public static AuthorityResponse convertToAuthorityResponse(AuthorityEntity authorityEntity) {
        return modelMapper.map(authorityEntity, AuthorityResponse.class);
    }

}
