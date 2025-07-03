/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 09:19:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-22 12:22:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// 一个用户可以属于多个组织，每个组织中可以多个角色
@Entity
@Data
@EqualsAndHashCode(callSuper=false, exclude = { "user", "organization", "roles" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bytedesk_core_user_org_role")
public class UserOrganizationRoleEntity implements Serializable  {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonBackReference("user-orgRoles")
    private UserEntity user;

    @ManyToOne
    @com.fasterxml.jackson.annotation.JsonBackReference("org-userRoles")
    private OrganizationEntity organization;

    // 可以设置多个角色
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "bytedesk_core_user_org_role_roles",
        joinColumns = @JoinColumn(name = "user_org_role_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"authorities"})
    private Set<RoleEntity> roles = new HashSet<>();
    
    // 用户角色有效期
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    // 可能还有其他字段，如权限等

    
}
