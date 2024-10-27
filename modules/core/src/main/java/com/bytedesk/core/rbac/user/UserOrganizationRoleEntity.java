/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 09:19:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:15:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.io.Serializable;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=false, exclude = { "user", "organization", "role" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_core_user_org_role")
public class UserOrganizationRoleEntity implements Serializable  {

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private OrganizationEntity organization;

    @ManyToOne
    private RoleEntity role;
    
    // 可能还有其他字段，如权限、有效期等
}
