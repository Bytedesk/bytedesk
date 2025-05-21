package com.bytedesk.core.rbac.auth.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.stereotype.Service;

@Service
public class LdapUserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public Boolean authenticate(String username, String password) {
        Filter filter = new EqualsFilter("uid", username);
        return ldapTemplate.authenticate("", filter.encode(), password);
    }
} 