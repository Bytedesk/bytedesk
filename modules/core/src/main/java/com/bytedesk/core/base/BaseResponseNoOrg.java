/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 11:48:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.base;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseResponseNoOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String uid;

    protected String userUid;

    private String level;

    private String platform;

    protected ZonedDateTime createdAt;
    
    protected ZonedDateTime updatedAt;

    public String getCreatedAt() {
        return BdDateUtils.formatDatetimeToString(createdAt);
    }

    public String getUpdatedAt() {
        return BdDateUtils.formatDatetimeToString(updatedAt);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
