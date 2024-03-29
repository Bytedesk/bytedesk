package com.bytedesk.core.rbac.user;

import java.util.ArrayList;
import java.util.List;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.utils.AuditModel;
import com.bytedesk.core.utils.StringListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_user", uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
})
public class User extends AuditModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * uuid
	 */
	@Column(name = "uuid", unique = true, nullable = false, length = 127)
	private String uid;

	/**
	 * 
	 */
	@Column(unique = true)
	private String num;

	/**
	 * 
	 */
	@Column(unique = true, nullable = false)
	private String username;

	private String nickname;

	@JsonIgnore
	@Column(nullable = false)
	private String password;

	@Email(message = "email format error")
	@Column(unique = true)
	private String email;

	@Digits(message = "phone length error", fraction = 0, integer = 11)
	private String mobile;

	private String avatar;

	@Builder.Default
	private String description = "default user description";

	/**
	 * 
	 */
	@Builder.Default
	@Column(name = "is_enabled")
	private boolean enabled = true;

	/**
	 */
	@Builder.Default
	@Column(name = "is_super")
	private boolean superUser = false;

	/**
	 * 
	 */
	@Builder.Default
	@Column(name = "is_verified")
	private boolean verified = false;

	/**
	 * 
	 */
	@Builder.Default
	@OneToMany(fetch = FetchType.EAGER)
	@JsonManagedReference // 避免无限递归
	private List<Role> roles = new ArrayList<>();
	// @Builder.Default
	// @ManyToMany(fetch = FetchType.EAGER)
	// @JoinTable(name = "core_user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	// private List<Role> roles = new ArrayList<>();

	/**
	 * 
	 */
	@Builder.Default
	@Convert(converter = StringListConverter.class)
	private List<String> organizations = new ArrayList<>();

}