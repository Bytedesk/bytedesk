/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 12:50:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.rbac.authority.AuthorityEntity;

@Data
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ RoleEntityListener.class })
@Table(name = "bytedesk_core_role", uniqueConstraints = {
// @UniqueConstraint(columnNames = { "name", "orgUid" }), // is_deleted=false
// 时不加限定
})
public class RoleEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Column(nullable = false)
	private String name;

	private String description;

	// 超级管理员、管理员等是平台角色，用户自定义角色是Level.ORGANIZATION
	// @Builder.Default
	// private String level = LevelEnum.PLATFORM.name();

	// 是否是系统角色
	@Builder.Default
	@Column(name = "is_system", nullable = false)
	private boolean system = false;

	@Builder.Default
	@ManyToMany
	@JoinTable(name = "bytedesk_core_role_authority", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<AuthorityEntity> authorities = new HashSet<>();

	// 除系统自带角色之外，允许管理员-自己创建角色
	// private String userUid;

	// @Builder.Default
	// private String platform = PlatformEnum.BYTEDESK.name();

	public void addAuthority(AuthorityEntity authority) {
		this.authorities.add(authority);
	}

	public void removeAuthority(AuthorityEntity authority) {
		this.authorities.remove(authority);
	}

}
