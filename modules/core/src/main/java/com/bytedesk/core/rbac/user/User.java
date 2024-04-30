package com.bytedesk.core.rbac.user;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.rbac.role.Role;
import com.bytedesk.core.utils.AuditModel;
import com.bytedesk.core.utils.StringSetConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
// import jakarta.validation.constraints.Digits;
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
@EntityListeners({ UserListener.class })
@Table(name = "core_user", uniqueConstraints = {
		// @UniqueConstraint(columnNames = "username"),
		// @UniqueConstraint(columnNames = "email")
})
public class User extends AuditModel {

	private static final long serialVersionUID = 3817197261L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "uuid", unique = true, nullable = false)
	private String uid;

	@Column(unique = true)
	private String num;

	// used in authjwtToken, should not be null
	@Column(unique = true, nullable = false)
	private String username;

	private String nickname;

	@JsonIgnore
	private String password;

	@Email(message = "email format error")
	@Column(unique = true)
	private String email;

	// TODO: including country
	// @Digits(message = "phone length error", fraction = 0, integer = 11)
	@Column(unique = true)
	private String mobile;

	@Builder.Default
	private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

	@Builder.Default
	private String description = BdConstants.DEFAULT_USER_DESCRIPTION;

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
	@ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
	
	@Builder.Default
	@Convert(converter = StringSetConverter.class)
	private Set<String> organizations = new HashSet<>();

	// return the first organization oid
	public String getOrgOid() {
		return this.organizations.isEmpty() ? null : this.organizations.iterator().next();
	}

	/** */
	public void addRole(Role role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            // role.getUsers().add(this);
        }
    }

	public void removeRole(Role role) {
		if (this.roles.contains(role)) {
			this.roles.remove(role);
			// role.getUsers().remove(this);
		}
	}
	
}