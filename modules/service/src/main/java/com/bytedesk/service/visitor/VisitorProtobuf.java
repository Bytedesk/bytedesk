/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-21 14:53:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 所有字段跟user.proto中字段一一对应
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class VisitorProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

	private String uid;

    private String visitorUid;

    private String nickname;

    private String avatar;

    // ROBOT/AGENT/SYSTEM/USER/VISITOR/WORKGROUP
    @Builder.Default
    private String type = UserTypeEnum.VISITOR.name();

    private String extra;

    public static VisitorProtobuf fromJson(String user) {
        return JSON.parseObject(user, VisitorProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

}
