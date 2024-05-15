/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-25 15:36:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-24 17:26:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseResponse;

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
public class DepartmentResponse extends BaseResponse {

    private static final long serialVersionUID = 5377636189L;    
    
    // private String did;

    private String name;

    private String description;

    private String type;

    @Builder.Default
    private Set<DepartmentResponse> children = new HashSet<>();
}
