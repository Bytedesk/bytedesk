package com.bytedesk.core.rbac.user;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.bytedesk.core.utils.BdDateUtils;

/**
 * User entity for system authentication and authorization
 * Manages user accounts, roles, and organizational relationships
 * 
 * Database Table: bytedesk_core_user
 * Purpose: Stores user information, authentication details, and role assignments
 */
@Entity
@Data
@Accessors(chain = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "password", "currentOrganization", "userOrganizationRoles" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ UserEntityListener.class })
@Table(
    name = "bytedesk_core_user",
    indexes = {
        @Index(name = "idx_user_uid", columnList = "uuid")
    }
)
public class UserEntity extends BaseEntityNoOrg {

	private static final long serialVersionUID = 1L;

	/**
	 * User number for searching and adding friends
	 */
	// @Column(unique = true) // num + platform unique
	private String num;

	/**
	 * Username used in JWT authentication (required field)
	 */
	@NotBlank(message = "username is required")
	@Column(nullable = false)
	private String username;

	/**
	 * Display name of the user
	 */
	private String nickname;

	/**
	 * Encrypted password for user authentication
	 */
	private String password;

	/**
	 * User's email address for communication and verification
	 */
	@Email(message = "email format error")
	// @Column(unique = true) // email + platform unique
	private String email;

	/**
	 * Country code for the user (default: 86 for China)
	 */
	@Builder.Default
	private String country = "86";

    /**
     * User's mobile phone number
     */
    private String mobile;

	/**
	 * URL of the user's avatar image
	 */
	@Builder.Default
	private String avatar = AvatarConsts.getDefaultAvatarUrl();

	/**
	 * User description or bio
	 */
	@Builder.Default
	private String description = I18Consts.I18N_USER_DESCRIPTION;

	/**
	 * User's gender (MALE, FEMALE, UNKNOWN)
	 */
	@Builder.Default
	private String sex = Sex.UNKNOWN.name();

	/**
	 * Registration source of the user
	 * Examples: USERNAME, EMAIL, MOBILE, GITHUB, WECHAT, GOOGLE, DINGTALK, FEISHU,
	 * FACEBOOK, DOUYIN, LDAP, OIDC, OPENID, ADMIN, IMPORT, UNKNOWN
	 */
	@Builder.Default
	@Column(name = "register_source", length = 32)
	private String registerSource = RegisterSource.UNKNOWN.name();

	/**
	 * Whether the user account is enabled
	 */
	@Builder.Default
	@Column(name = "is_enabled")
	private boolean enabled = true;

	/**
	 * Whether the user has super administrator privileges
	 */
	@Builder.Default
	@Column(name = "is_super")
	private boolean superUser = false;

	/**
	 * Whether the user's email has been verified
	 */
	@Builder.Default
	@Column(name = "is_email_verified")
	private boolean emailVerified = false;

	/**
	 * Whether the user's mobile number has been verified
	 */
	@Builder.Default
	@Column(name = "is_mobile_verified")
	private boolean mobileVerified = false;

	/**
	 * Password last modified time for security compliance
	 * Used for tracking password changes and prompting users to update passwords
	 * Defaults to account creation time when user is created
	 */
	@Column(name = "password_modified_at")
	private ZonedDateTime passwordModifiedAt;

	// TODO: 一个用户可以属于多个组织

	/**
	 * User's current active organization
	 * Users can belong to multiple organizations but can only be active in one at a time
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@com.fasterxml.jackson.annotation.JsonManagedReference
	private OrganizationEntity currentOrganization;

	/**
	 * Current roles assigned to the user in the active organization
	 */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "bytedesk_core_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> currentRoles = new HashSet<>();

	/**
	 * User's roles across all organizations
	 * One user can belong to multiple organizations with different roles in each
	 */
	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@com.fasterxml.jackson.annotation.JsonManagedReference("user-orgRoles")
	private Set<UserOrganizationRoleEntity> userOrganizationRoles = new HashSet<>();

	// 添加方法以简化对用户组织和角色的管理
    public void addOrganizationRole(RoleEntity role) {
        if (role == null || role.getId() == null) {
            return;
        }
        
        OrganizationEntity organization = this.currentOrganization;
        if (organization == null) {
            return;
        }
        
        // 1. 处理用户组织角色关联
        UserOrganizationRoleEntity uor = userOrganizationRoles.stream()
            .filter(u -> u.getOrganization().getId().equals(organization.getId()))
            .findFirst()
            .orElseGet(() -> {
                UserOrganizationRoleEntity newUor = UserOrganizationRoleEntity.builder()
                    .user(this)
                    .organization(organization)
                    .build();
                userOrganizationRoles.add(newUor);
                return newUor;
            });
        
        // 2. 设置角色有效期
        uor.setStartDate(BdDateUtils.now());
        uor.setEndDate(BdDateUtils.now().plusYears(100));
        
        // 3. 检查是否已存在该角色ID
        Long roleId = role.getId();
        
        // 4. 处理UserOrganizationRoleEntity中的角色集合
        Boolean roleExists = uor.getRoles().stream()
            .anyMatch(r -> r.getId() != null && r.getId().equals(roleId));
        
        if (!roleExists) {
            // 直接使用传入的角色实例，不创建新的引用
            uor.getRoles().add(role);
        }
        
        // 5. 处理currentRoles集合
        Boolean currentRoleExists = currentRoles.stream()
            .anyMatch(r -> r.getId() != null && r.getId().equals(roleId));
            
        if (!currentRoleExists) {
            // 直接使用传入的角色实例，保持一致性
            currentRoles.add(role);
        }
    }

	public Set<String> getRoleUids() {
		OrganizationEntity organization = this.currentOrganization;
		if (organization == null) {
			return new HashSet<>();
		}
		return userOrganizationRoles.stream().filter(u -> u.getOrganization().equals(organization))
				.flatMap(uor -> uor.getRoles().stream()).map(r -> r.getUid()).collect(Collectors.toSet());
	}

	// 判断organization是否包含role
	public Boolean containsRole(RoleEntity role) {
		OrganizationEntity organization = this.currentOrganization;
		if (organization == null) {
			return false;
		}
		return userOrganizationRoles.stream().filter(u -> u.getOrganization().equals(organization)).findFirst()
				.map(uor -> uor.getRoles().contains(role)).orElse(false);
	}

    public void removeOrganizationRole(RoleEntity role) {
        if (role == null || role.getId() == null) {
            return;
        }
        
        OrganizationEntity organization = this.currentOrganization;
        if (organization == null) {
            return;
        }
        
        // 从userOrganizationRoles中移除
        userOrganizationRoles.stream()
            .filter(u -> u.getOrganization().getId().equals(organization.getId()))
            .findFirst()
            .ifPresent(uor -> {
                // 通过ID查找角色并移除
                RoleEntity roleToRemove = null;
                for (RoleEntity r : uor.getRoles()) {
                    if (r.getId().equals(role.getId())) {
                        roleToRemove = r;
                        break;
                    }
                }
                if (roleToRemove != null) {
                    uor.getRoles().remove(roleToRemove);
                }
            });
            
        // 同时从currentRoles中移除
        RoleEntity currentRoleToRemove = null;
        for (RoleEntity r : currentRoles) {
            if (r.getId().equals(role.getId())) {
                currentRoleToRemove = r;
                break;
            }
        }
        if (currentRoleToRemove != null) {
            currentRoles.remove(currentRoleToRemove);
        }
    }

	// 遍历userOrganizationRoles，删除organization所对应的除系统角色之外的所有role
	public void removeOrganizationRoles() {
		OrganizationEntity organization = this.currentOrganization;
		if (organization == null) {
			return;
		}
		
		// 找到当前组织关联
		Optional<UserOrganizationRoleEntity> uorOptional = userOrganizationRoles.stream()
		    .filter(u -> u.getOrganization().getId().equals(organization.getId()))
		    .findFirst();
		    
		if (uorOptional.isPresent()) {
		    UserOrganizationRoleEntity uor = uorOptional.get();
		    // 创建要移除的角色列表，避免ConcurrentModificationException
		    Set<RoleEntity> rolesToRemove = new HashSet<>();
		    
		    for (RoleEntity role : uor.getRoles()) {
		        if (!LevelEnum.PLATFORM.name().equals(role.getLevel())) {
		            rolesToRemove.add(role);
		        }
		    }
		    
		    // 移除非平台级别的角色
		    uor.getRoles().removeAll(rolesToRemove);
		    
		    // 同时从currentRoles中移除
		    currentRoles.removeAll(rolesToRemove);
		}
	}
	
	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
	private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// return the first organization oid
	public String getOrgUid() {
		if (this.currentOrganization == null) {
			return BytedeskConsts.EMPTY_STRING;
		}
		return this.currentOrganization.getUid();
	}

	/**
	 * Get formatted password modification time for display
	 * @return formatted date string
	 */
	public String getPasswordModifiedAtString() {
		if (this.passwordModifiedAt == null) {
			return BytedeskConsts.EMPTY_STRING;
		}
		return BdDateUtils.formatDatetimeToString(this.passwordModifiedAt);
	}

	// 定义性别枚举
	public enum Sex {
		MALE,
		FEMALE,
		UNKNOWN // unknown
	}

	// 注册来源枚举，便于统一使用
	public enum RegisterSource {
		USERNAME,
		EMAIL,
		MOBILE,
		GITHUB,
		WECHAT,
		GOOGLE,
		DINGTALK,
		FEISHU,
		FACEBOOK,
		DOUYIN,
		LDAP,
		OIDC,
		OPENID,
		ADMIN,
		IMPORT,
		UNKNOWN
	}

	public UserProtobuf toProtobuf() {
		return UserProtobuf.builder()
				.uid(this.getUid())
				.nickname(this.getNickname())
				.avatar(this.getAvatar())
				.type(UserTypeEnum.USER.name())
				.extra(this.getExtra())
				.build();
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