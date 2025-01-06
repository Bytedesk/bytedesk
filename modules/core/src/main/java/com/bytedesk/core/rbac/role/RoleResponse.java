/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 16:59:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.authority.AuthorityResponse;

/**
 * @author bytedesk.com on 2019-06-24
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;    
    
    private String name;

    private String description;

    private String level;

    @Builder.Default
    private Set<AuthorityResponse> authorities = new HashSet<>();

    // @Builder.Default
    // private Set<String> memberUids = new HashSet<>();

    // private String userUid;
    private LocalDateTime createdAt;

    private String platform;

}
