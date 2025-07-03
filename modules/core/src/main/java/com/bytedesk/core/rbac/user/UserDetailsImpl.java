/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:05:34
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
import org.springframework.security.core.userdetails.UserDetails;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.role.RoleEntity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

// import java.time.ZonedDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;

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
    private String country;
    private String sex;
    // 
    private Boolean enabled;
    private Boolean superUser;
    private Boolean emailVerified;
    private Boolean mobileVerified;
    private String platform;
    private String client;
    private String device;
    // 
    private String orgUid;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt; 
    // 
    private OrganizationEntity currentOrganization;
    private Set<UserOrganizationRoleEntity> userOrganizationRoles;
    private Set<RoleEntity> currentRoles;
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
            String description,
            String country,
            String sex,
            // 
            Boolean enabled,
            Boolean superUser,
            Boolean emailVerified,
            Boolean mobileVerified,
            String platform,
            String client,
            String device,
            String orgUid,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            // 
            Collection<? extends GrantedAuthority> authorities,
            OrganizationEntity currentOrganization,
            Set<RoleEntity> currentRoles,
            Set<UserOrganizationRoleEntity> userOrganizationRoles
            ) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.description = description;
        this.country = country;
        this.sex = sex;
        // 
        this.enabled = enabled;
        this.superUser = superUser;
        this.emailVerified = emailVerified;
        this.mobileVerified = mobileVerified;
        this.platform = platform;
        this.client = client;
        this.device = device;
        this.orgUid = orgUid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        // 
        this.authorities = authorities;
        this.currentOrganization = currentOrganization;
        this.currentRoles = currentRoles;
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
                user.getDescription(),
                user.getCountry(),
                user.getSex(),
                // 
                user.isEnabled(),
                user.isSuperUser(),
                user.isEmailVerified(),
                user.isMobileVerified(),
                user.getPlatform(),
                null, // client，存token时单独设置
                null, // device，存token时单独设置
                user.getOrgUid(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                //
                authorities,
                user.getCurrentOrganization(),
                user.getCurrentRoles(),
                user.getUserOrganizationRoles());
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
