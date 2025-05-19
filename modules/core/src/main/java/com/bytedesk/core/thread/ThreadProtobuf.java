/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 11:01:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ThreadProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

	private String uid;

    private String topic;

    private ThreadTypeEnum type;

    private ThreadProcessStatusEnum status;

    private UserProtobuf user;

    private ClientEnum client;

    private String extra;

    public static ThreadProtobuf fromJson(String json) {
        return JSON.parseObject(json, ThreadProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    // public static ThreadProtobuf fromEntity(ThreadEntity thread) {
    //     return ThreadProtobuf.builder()
    //             .uid(thread.getUid())
    //             .topic(thread.getTopic())
    //             .type(thread.getType())
    //             .status(thread.getStatus())
    //             .user(UserProtobuf.fromEntity(thread.getUser()))
    //             .extra(thread.getExtra())
    //             .build();
    // }
}
