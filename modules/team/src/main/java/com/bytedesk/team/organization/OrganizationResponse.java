/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 21:20:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-01 22:17:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.organization;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.bytedesk.team.department.Department;

@Projection(name = "organizationResponse", types = Organization.class)
public interface OrganizationResponse {

    @Value("#{target.id}")
    long getId();

    String getOid();

    String getNickname();

    Set<Department> getDepartments();

    @Value("#{target.getDepartments().size()}")
    int getDepCount();

    // @Value("#{target.address.toString()}")
    // String getAddress();

}
