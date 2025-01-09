/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:11:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bytedesk.core.rbac.organization.OrganizationEntity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;
// import java.util.stream.Collectors;

import com.bytedesk.core.utils.ConvertUtils;

@Slf4j
@Data
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    private String mobile;
    private String email;
    private String password;
    private String description;
    // 
    private boolean enabled;
    private boolean superUser;
    private boolean emailVerified;
    private boolean mobileVerified;
    private String platform;
    // 
    private OrganizationEntity currentOrganization;
    private Set<UserOrganizationRoleEntity> userOrganizationRoles;
    // private Set<Role> roles;
    // private Set<String> organizations;
    Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(
            Long id, 
            String uid, 
            String username, 
            String nickname, 
            String avatar, 
            String mobile,
            String email, 
            String password,
            Collection<? extends GrantedAuthority> authorities,
            OrganizationEntity currentOrganization,
            Set<UserOrganizationRoleEntity> userOrganizationRoles,
            String description,
            boolean superUser,
            boolean emailVerified,
            boolean mobileVerified,
            String platform,
            boolean enabled) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.description = description;
        // 
        this.enabled = enabled;
        this.superUser = superUser;
        this.emailVerified = emailVerified;
        this.mobileVerified = mobileVerified;
        this.platform = platform;
        // 
        this.authorities = authorities;
        this.currentOrganization = currentOrganization;
        this.userOrganizationRoles = userOrganizationRoles;
    }

    /**
     * {@UserRestController#testSuperAuthority}
     */
    public static UserDetailsImpl build(UserEntity user) {
        //
        Set<GrantedAuthority> authorities = ConvertUtils.filterUserGrantedAuthorities(user);
        // log.info("authorities: {}", authorities);
        // 
        return new UserDetailsImpl(user.getId(),
                user.getUid(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getMobile(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getCurrentOrganization(),
                user.getUserOrganizationRoles(),
                user.getDescription(),
                user.isSuperUser(),
                user.isEmailVerified(),
                user.isMobileVerified(),
                user.getPlatform(),
                user.isEnabled());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
