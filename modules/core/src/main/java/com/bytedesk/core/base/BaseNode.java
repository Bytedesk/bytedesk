/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 16:36:21
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
public abstract class BaseNode implements Serializable {

    private static final long serialVersionUID = 1L;

    public String toJson() {
        return JSON.toJSONString(this);
    }
    
}
