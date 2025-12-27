/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-31 13:17:49
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
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ RoleEntityListener.class })
@Table(name = "bytedesk_core_role", indexes = {
		@Index(name = "idx_role_uid", columnList = "uuid")
})
public class RoleEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Column(nullable = false)
	private String name;

	@Column(name = "role_value")
	private String value;

	private String description;

	// 是否是系统角色
	@Builder.Default
	@Column(name = "is_system")
	private Boolean system = false;

	@Builder.Default
	@ManyToMany
	@JoinTable(name = "bytedesk_core_role_authority", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<AuthorityEntity> authorities = new HashSet<>();

	public void addAuthority(AuthorityEntity authority) {
		this.authorities.add(authority);
	}

	public void removeAuthority(AuthorityEntity authority) {
		this.authorities.remove(authority);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RoleEntity other)) {
			return false;
		}
		// Prefer DB identity when available
		if (this.getId() != null && other.getId() != null) {
			return this.getId().equals(other.getId());
		}
		// Fallback to stable business uid
		if (this.getUid() != null && other.getUid() != null) {
			return this.getUid().equals(other.getUid());
		}
		return false;
	}

	@Override
	public final int hashCode() {
		if (this.getId() != null) {
			return this.getId().hashCode();
		}
		if (this.getUid() != null) {
			return this.getUid().hashCode();
		}
		return System.identityHashCode(this);
	}

}
