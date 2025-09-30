/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-03 10:14:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 15:13:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.bytedesk.core.rbac.user.UserDetailsImpl;

public class AuthToken implements Authentication {
    
    private static final long serialVersionUID = 1L;
    //
    private Boolean isAuthenticated;
    //
    private UserDetailsImpl userDetails;

    // Constructor to be used after successful authentication
    public AuthToken(UserDetailsImpl userDetails) {
        this.userDetails = userDetails;
        this.isAuthenticated = true;
    }

    @Override
    public String getName() {
        return this.userDetails.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCredentials'");
    }

    @Override
    public Object getDetails() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

}
