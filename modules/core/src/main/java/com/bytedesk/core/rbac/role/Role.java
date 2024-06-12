/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-11 18:06:55
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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.authority.Authority;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * role table
 *
 * @author im.bytedesk.com
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_role", uniqueConstraints = {
	@UniqueConstraint(columnNames = { "name", "orgUid" }),
})
public class Role extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Column(nullable = false)
	private String name;

	private String displayName;

	/**
	 * value is a keyword in h2 db
	 */
	// @Column(name = "by_value", nullable = false)
	// private String value;

	private String description;

	@Column(name = TypeConsts.COLUMN_NAME_TYPE)
	private String type;

	@JsonIgnore
	@Builder.Default
	@ManyToMany
	private Set<Authority> authorities = new HashSet<>();

	/**
	 * 除系统自带角色之外，
	 * 允许管理员-自己创建角色
	 */
	// @JsonIgnore
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JsonBackReference("user-roles") // 避免无限递归
	// private User user;
	private String orgUid;

	public void addAuthority(Authority authority) {
		this.authorities.add(authority);
	}

	public void removeAuthority(Authority authority) {
		this.authorities.remove(authority);
	}

}
