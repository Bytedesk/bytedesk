/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-17 18:01:46
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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Data;
import lombok.EqualsAndHashCode;
// import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
// @Accessors(chain = true)
public abstract class BaseRequest implements Serializable {

    public String uid;

    public int pageNumber;

    public int pageSize;

    public String type;

    public String content;

    public String client;

    private String orgUid;

    public Pageable getPageable() {
        return PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "updatedAt");
    }
}
