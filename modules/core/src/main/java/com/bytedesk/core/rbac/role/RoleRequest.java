/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 09:45:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RoleRequest extends BaseRequest {

    private String name;

    private String description;

    @Builder.Default
	private String level = LevelEnum.PLATFORM.name();

    @Builder.Default
    private Set<String> authorityUids = new HashSet<>();

    // @Builder.Default
	// private Set<String> memberUids = new HashSet<>();

    private String userUid;

    @Builder.Default
	private String platform = PlatformEnum.BYTEDESK.name();
}
