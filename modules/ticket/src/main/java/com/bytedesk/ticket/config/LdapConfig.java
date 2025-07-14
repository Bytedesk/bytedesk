/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-30 15:47:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-30 15:48:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.config;

import org.flowable.ldap.LDAPConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Description("LDAP Configuration - LDAP directory service configuration for user authentication and group management")
public class LdapConfig {

    @Bean
    public LDAPConfiguration ldapConfiguration() {
        LDAPConfiguration ldapConfig = new LDAPConfiguration();
        
        // 基本配置
        ldapConfig.setServer("ldap://ldap.bytedesk.com:389");
        ldapConfig.setBaseDn("dc=bytedesk,dc=com");
        ldapConfig.setUserBaseDn("ou=users");
        ldapConfig.setGroupBaseDn("ou=groups");
        
        // 查询配置
        ldapConfig.setQueryUserByUserId("(&(objectClass=inetOrgPerson)(uid={0}))");
        ldapConfig.setQueryGroupsForUser("(&(objectClass=groupOfNames)(member={0}))");
        
        // 属性映射
        ldapConfig.setUserIdAttribute("uid");
        ldapConfig.setUserFirstNameAttribute("givenName");
        ldapConfig.setUserLastNameAttribute("sn");
        ldapConfig.setUserEmailAttribute("mail");
        
        ldapConfig.setGroupIdAttribute("cn");
        ldapConfig.setGroupNameAttribute("cn");
        
        // 认证配置
        ldapConfig.setSecurityAuthentication("simple");
        // ldapConfig.setManagerDn("cn=admin,dc=bytedesk,dc=com");
        // ldapConfig.setManagerPassword("admin_password");
        
        // 缓存配置
        ldapConfig.setGroupCacheSize(500);
        ldapConfig.setGroupCacheExpirationTime(3600000); // 1小时
        
        return ldapConfig;
    }

    // @Bean
    // public LDAPGroupCache ldapGroupCache(LDAPConfiguration ldapConfiguration) {
    //     return new LDAPGroupCache(ldapConfiguration);
    // }

    // @Bean
    // public LDAPIdentityServiceImpl ldapIdentityService(
    //         LDAPConfiguration ldapConfiguration,
    //         LDAPGroupCache ldapGroupCache) {
    //     return new LDAPIdentityServiceImpl(ldapConfiguration, ldapGroupCache);
    // }
} 