/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-15 08:46:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-30 15:42:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    private String type;

    private String extra;

    public static NoticeProtobuf fromJson(String json) {
        return JSON.parseObject(json, NoticeProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
