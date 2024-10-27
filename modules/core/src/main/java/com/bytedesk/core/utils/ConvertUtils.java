/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-10 11:42:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProtobuf;

public class ConvertUtils {

    private static final ModelMapper modelMapper = new ModelMapper(); // 添加静态ModelMapper实例

    private ConvertUtils() {
    }

    public static UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        UserResponse userResponse = modelMapper.map(userDetails, UserResponse.class);

        return userResponse;
    }

    public static UserResponse convertToUserResponse(UserEntity user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        Set<GrantedAuthority> authorities = user.getUserOrganizationRoles().stream()
                .flatMap(uor -> uor.getRole().getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getValue())))
                .collect(Collectors.toSet());
        userResponse.setAuthorities(authorities);
        return userResponse;
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

    public static RoleResponse convertToRoleResponse(RoleEntity role) {
        return modelMapper.map(role, RoleResponse.class);
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

}
