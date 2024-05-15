/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-13 11:47:22
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    private String mobile;
    private String email;
    private String description;
    // 
    private boolean superUser;
    private boolean emailVerified;
    private boolean mobileVerified;
    private String platform;
    // 
    @JsonIgnore
    private String password;
    private Set<Role> roles;
    private Set<String> organizations;
    //
    private boolean enabled;
    Collection<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(Long id, String uid, String username, String nickname, String avatar, String mobile,
            String email, String password,
            Collection<? extends GrantedAuthority> authorities, Set<Role> roles, Set<String> organizations,
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
        this.authorities = authorities;
        this.roles = roles;
        this.organizations = organizations;
        this.description = description;
        // 
        this.superUser = superUser;
        this.emailVerified = emailVerified;
        this.mobileVerified = mobileVerified;
        this.platform = platform;
        // 
        this.enabled = enabled;
    }

    //
    public static UserDetailsImpl build(User user) {
        //
        // Set<GrantedAuthority> authorities = user.getRoles().stream()
        // .map(role -> new SimpleGrantedAuthority(role.getValue()))
        // .collect(Collectors.toSet());
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(TypeConsts.ROLE_PREFIX + authority.getValue())))
                .collect(Collectors.toSet());

        return new UserDetailsImpl(user.getId(),
                user.getUid(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getMobile(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getRoles(),
                user.getOrganizations(),
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
        // return true;
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        // return true;
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // return true;
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        // return true;
        return enabled;
    }
}
