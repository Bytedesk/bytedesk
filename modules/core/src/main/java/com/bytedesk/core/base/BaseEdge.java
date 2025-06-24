/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 17:12:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String name;

    private String type;

    private String description;

    private String sourceUid;

    private String targetUid;
    
    // 新增字段，对应JSON中的sourceNodeID和targetNodeID
    private String sourceNodeID;
    
    private String targetNodeID;
    
    // 新增字段，对应JSON中的sourcePortID
    private String sourcePortID;

    public String toJson() {
        return JSON.toJSONString(this);
    }
    
}
