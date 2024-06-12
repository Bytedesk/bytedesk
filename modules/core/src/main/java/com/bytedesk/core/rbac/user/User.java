package com.bytedesk.core.rbac.user;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
// "roles",
@EqualsAndHashCode(callSuper = true, exclude = { "password", "currentOrganization", "userOrganizationRoles" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UserEntityListener.class })
// @DiscriminatorValue("User")
@Table(name = "core_user", uniqueConstraints = {
		// num, username, email, mobile is unique combined with platform, not self
		@UniqueConstraint(columnNames = { "num", "platform" }),
		@UniqueConstraint(columnNames = { "username", "platform" }),
		@UniqueConstraint(columnNames = { "email", "platform" }),
		@UniqueConstraint(columnNames = { "mobile", "platform" }),
})
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	// @Column(unique = true)
	private String num;

	// used in authjwtToken, should not be null
	@NotBlank(message = "username is required")
	@Column(nullable = false)
	private String username;

	private String nickname;

	@JsonIgnore
	private String password;

	@Email(message = "email format error")
	// @Column(unique = true)
	private String email;

	// TODO: including country
	// @Digits(message = "phone length error", fraction = 0, integer = 11)
	// @Column(unique = true)
	private String mobile;

	@Builder.Default
	private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

	@Builder.Default
	private String description = I18Consts.I18N_USER_DESCRIPTION;

	// @Embedded
	// private UserSettings userSettings;

	@Builder.Default
	private Sex sex = Sex.UNKNOW;

	@Builder.Default
	@Column(name = "is_enabled")
	private boolean enabled = true;

	@Builder.Default
	@Column(name = "is_super")
	private boolean superUser = false;

	@Builder.Default
	@Column(name = "is_email_verified")
	private boolean emailVerified = false;

	@Builder.Default
	@Column(name = "is_mobile_verified")
	private boolean mobileVerified = false;

	@Builder.Default
	private String platform = BdConstants.PLATFORM_BYTEDESK;

	// @Builder.Default
	// @ManyToMany(fetch = FetchType.EAGER)
	// private Set<Role> roles = new HashSet<>();

	// // TODO: restrict one user to one org? to be determinated which make roles
	// complicated
	// @Builder.Default
	// @Convert(converter = StringSetConverter.class)
	// private Set<String> organizations = new HashSet<>();

	//
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Organization currentOrganization;

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserOrganizationRole> userOrganizationRoles = new HashSet<>();

	// 添加方法以简化对用户组织和角色的管理
	public void addOrganizationRole(Organization organization, Role role) {
		UserOrganizationRole uor = UserOrganizationRole.builder().user(this).organization(organization).role(role)
				.build();
		this.userOrganizationRoles.add(uor);
		//
		if (this.currentOrganization == null) {
			this.currentOrganization = organization;
		}
	}

	public void removeOrganizationRole(Organization organization, Role role) {
		UserOrganizationRole uor = UserOrganizationRole.builder().user(this).organization(organization).role(role)
				.build();
		if (this.userOrganizationRoles.contains(uor)) {
			this.userOrganizationRoles.remove(uor);
		}
	}

	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
	// 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
	// of type character varying
	@JdbcTypeCode(SqlTypes.JSON)
	private String extra = BdConstants.EMPTY_JSON_STRING;

	// return the first organization oid
	public String getOrgUid() {
		if (this.currentOrganization == null) {
			return BdConstants.EMPTY_STRING;
		}
		return this.currentOrganization.getUid();
	}

	// /** */
	// public void addRole(Role role) {
	// if (!this.roles.contains(role)) {
	// this.roles.add(role);
	// // role.getUsers().add(this);
	// }
	// }

	// public void removeRole(Role role) {
	// if (this.roles.contains(role)) {
	// this.roles.remove(role);
	// // role.getUsers().remove(this);
	// }
	// }

	//
	// 定义性别枚举
	public enum Sex {
		MALE,
		FEMALE,
		UNKNOW // unknown
	}

	@Override
	public String toString() {
		return "User [uid=" + this.getUid() +
				", username=" + this.username +
				", email=" + this.email +
				", mobile=" + this.mobile +
				"]";
	}

}