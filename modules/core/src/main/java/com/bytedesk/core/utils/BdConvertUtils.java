/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-11 11:53:16
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

import org.modelmapper.ModelMapper;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.rbac.role.RoleResponse;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserResponseSimple;

public class BdConvertUtils {
    private BdConvertUtils() {
    }

    public static UserResponseSimple convertTUserResponseSimple(User user) {
        return new ModelMapper().map(user, UserResponseSimple.class);
    }

    public static RoleResponse convertToRoleResponse(Role role) {
        return new ModelMapper().map(role, RoleResponse.class);
    }
    
    public static MessageResponse convertToMessageResponse(Message message) {

        MessageResponse messageResponse = new ModelMapper().map(message, MessageResponse.class);

        UserResponseSimple user = JSON.parseObject(message.getUser(), UserResponseSimple.class);
        messageResponse.setUser(user);
        // messageResponse.setUser(convertTUserResponseSimple(message.getUser()));

        return messageResponse;
    }



}
