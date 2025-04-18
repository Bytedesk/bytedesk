/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 21:20:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 11:10:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import com.bytedesk.core.base.BaseResponseNoOrg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganizationResponseSimple extends BaseResponseNoOrg {

    private static final long serialVersionUID = 1L;
    
    private String name;

    private String logo;

    private String code;

    private String description;

    // 认证状态：未认证、已认证、审核中、审核失败
    private String verifyStatus;
}
