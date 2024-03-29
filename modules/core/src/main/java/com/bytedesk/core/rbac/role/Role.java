/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 14:02:15
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

import com.bytedesk.core.rbac.authority.Authority;
import com.bytedesk.core.rbac.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * role table
 *
 * @author im.bytedesk.com
 */
@Data
@Entity
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_role")
public class Role implements Serializable {

	private static final long serialVersionUID = -2461905686314283608L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false, length = 127)
	private String rid;

	/**
	 * name
	 */
	private String name;
	// @Enumerated(EnumType.STRING)
	// @Column(length = 20)
	// private ERole name;

	/**
	 * value
	 */
	private String value;

	/**
	 * 
	 */
	private String description;

	/**
	 * 
	 */
	@Column(name = "by_type")
	private String type;

	/**
	 * 
	 */
	@JsonIgnore
	@Builder.Default
	@OneToMany
	private List<Authority> authorities = new ArrayList<>();
	// @JsonIgnore
	// @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	// @JoinTable(name = "core_role_authorities", joinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)), inverseJoinColumns = @JoinColumn(name = "authority_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)))
	// private List<Authority> authorities;

	/**
	 * 除系统自带角色之外，
	 * 允许管理员-自己创建角色
	 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference // 避免无限递归
	private User user;

}
