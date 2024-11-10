package com.bytedesk.core.rbac.user;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.NonNull;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;
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
@EqualsAndHashCode(callSuper = true, exclude = { "password", "currentOrganization", "userOrganizationRoles" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UserEntityListener.class })
@Table(name = "bytedesk_core_user", uniqueConstraints = {
		// num, username, email, mobile is unique combined with platform, not self
		@UniqueConstraint(columnNames = { "num", "platform" }),
		@UniqueConstraint(columnNames = { "username", "platform" }),
		@UniqueConstraint(columnNames = { "email", "platform" }),
		@UniqueConstraint(columnNames = { "mobile", "platform" }),
})
public class UserEntity extends BaseEntityNoOrg {

	private static final long serialVersionUID = 1L;

	// 用于搜索用户，添加好友
	// @Column(unique = true) // num + platform unique
	private String num;

	// used in auth jwt token, should not be null
	@NotBlank(message = "username is required")
	@Column(nullable = false)
	private String username;

	private String nickname;

	@JsonIgnore
	private String password;

	@Email(message = "email format error")
	// @Column(unique = true) // email + platform unique
	private String email;

	// TODO: including country
	// @Digits(message = "phone length error", fraction = 0, integer = 11)
	// @Column(unique = true) // mobile + platform unique
	private String mobile;

	@Builder.Default
	private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

	@Builder.Default
	private String description = I18Consts.I18N_USER_DESCRIPTION;

	@Builder.Default
	private String sex = Sex.UNKNOWN.name();

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
	private String platform = PlatformEnum.BYTEDESK.name();
	
	// 同一时刻，用户只能在一个组织下，用户可以切换组织
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private OrganizationEntity currentOrganization;

	// 一个用户可以属于多个组织，每个组织中可以多个角色
	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserOrganizationRoleEntity> userOrganizationRoles = new HashSet<>();

	// 添加方法以简化对用户组织和角色的管理
    public void addOrganizationRole(OrganizationEntity organization, RoleEntity role) {
        UserOrganizationRoleEntity uor = userOrganizationRoles.stream()
            .filter(u -> u.getOrganization().equals(organization))
            .findFirst()
            .orElseGet(() -> {
                UserOrganizationRoleEntity newUor = UserOrganizationRoleEntity.builder()
                    .user(this)
                    .organization(organization)
                    .build();
                userOrganizationRoles.add(newUor);
                return newUor;
            });
        uor.getRoles().add(role);
		// 角色有效期为30天
		// uor.startDate(LocalDateTime.now());
        // uor.endDate(LocalDateTime.now().plusDays(30));
        //
        if (this.currentOrganization == null) {
            this.currentOrganization = organization;
        }
    }

    public void removeOrganizationRole(@NonNull OrganizationEntity organization, RoleEntity role) {
        userOrganizationRoles.stream()
            .filter(u -> u.getOrganization().equals(organization))
            .findFirst()
            .ifPresent(uor -> uor.getRoles().remove(role));
    }

	// 遍历userOrganizationRoles，删除organization所对应的所有role
	public void removeOrganizationRoles(@NonNull OrganizationEntity organization) {
		// userOrganizationRoles.removeIf(uor -> uor.getOrganization().equals(organization));
		Iterator<UserOrganizationRoleEntity> iterator = userOrganizationRoles.iterator();
		while (iterator.hasNext()) {
			UserOrganizationRoleEntity uor = iterator.next();
			if (uor.getOrganization().equals(organization)) {
				// 遍历uor的roles，删除role
				uor.getRoles().clear();
			}
		}
	}
	
	// 添加方法以简化对用户组织和角色的管理
	// public void addOrganizationRole(OrganizationEntity organization, RoleEntity role) {
	// 	UserOrganizationRoleEntity uor = UserOrganizationRoleEntity.builder().user(this).organization(organization).role(role)
	// 			.build();
	// 	this.userOrganizationRoles.add(uor);
	// 	//
	// 	if (this.currentOrganization == null) {
	// 		this.currentOrganization = organization;
	// 	}
	// }

	// public void removeOrganizationRole(OrganizationEntity organization, RoleEntity role) {
	// 	UserOrganizationRoleEntity uor = UserOrganizationRoleEntity.builder().user(this).organization(organization).role(role)
	// 			.build();
	// 	if (this.userOrganizationRoles.contains(uor)) {
	// 		this.userOrganizationRoles.remove(uor);
	// 	}
	// }

	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
	// 用于兼容postgreSQL，否则会报错，[ERROR: column "extra" is of type json but expression is
	// of type character varying
	@JdbcTypeCode(SqlTypes.JSON)
	private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// return the first organization oid
	public String getOrgUid() {
		if (this.currentOrganization == null) {
			return BytedeskConsts.EMPTY_STRING;
		}
		return this.currentOrganization.getUid();
	}

	// 定义性别枚举
	public enum Sex {
		MALE,
		FEMALE,
		UNKNOWN // unknown
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