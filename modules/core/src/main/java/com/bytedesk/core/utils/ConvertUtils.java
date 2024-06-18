/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-14 12:24:36
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
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserDetailsImpl;
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadResponseSimple;

public class ConvertUtils {
    
    private ConvertUtils() {
    }

     public static UserResponse convertToUserResponse(UserDetailsImpl userDetails) {
        UserResponse userResponse = new ModelMapper().map(userDetails, UserResponse.class);

        return userResponse;
    }


    public static UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = new ModelMapper().map(user, UserResponse.class);
        Set<GrantedAuthority> authorities = user.getUserOrganizationRoles().stream()
                .flatMap(uor -> uor.getRole().getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getValue())))
                .collect(Collectors.toSet());
        userResponse.setAuthorities(authorities);
        return userResponse;
    }

    public static UserResponseSimple convertToUserResponseSimple(User user) {
        return new ModelMapper().map(user, UserResponseSimple.class);
    }

    public static ThreadResponseSimple convertToThreadResponseSimple(Thread thread) {
        return new ModelMapper().map(thread, ThreadResponseSimple.class);
    }

    public static RoleResponse convertToRoleResponse(Role role) {
        return new ModelMapper().map(role, RoleResponse.class);
    }
    
    public static MessageResponse convertToMessageResponse(Message message) {

        MessageResponse messageResponse = new ModelMapper().map(message, MessageResponse.class);

        if (message.getThreads() != null && message.getThreads().size() > 0) {
            messageResponse.setThread(convertToThreadResponseSimple(message.getThreads().get(0)));
        }
        
        UserResponseSimple user = JSON.parseObject(message.getUser(), UserResponseSimple.class);
        messageResponse.setUser(user);

        return messageResponse;
    }

   

}
