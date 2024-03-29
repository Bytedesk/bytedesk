/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 14:00:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import com.bytedesk.core.rbac.authority.AuthorityResponse;
import com.bytedesk.core.utils.BaseResponse;

/**
 * @author bytedesk.com on 2019-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleResponse extends BaseResponse {

    private String rid;
    
    private String name;

    private String value;

    private String description;

    /**
     * 区分是工作组内部成员角色
     * 还是平台级角色
     */
    private String type;

    private List<AuthorityResponse> authorities;

}
