/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-10 12:25:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 12:38:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

@Configuration
@ConditionalOnProperty(name = "spring.ldap.enabled", havingValue = "true", matchIfMissing = false)
public class LdapSecurityConfig extends GlobalAuthenticationConfigurerAdapter {

    @Value("${spring.ldap.urls}")
    private String ldapUrls;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider ldapAuthenticationProvider() {
        LdapAuthenticationProvider provider = new LdapAuthenticationProvider(bindAuthenticator());
        provider.setUserDetailsContextMapper(new LdapUserDetailsMapper());
        return provider;
    }

    @Bean
    public BindAuthenticator bindAuthenticator() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource());
        authenticator.setUserSearch(userSearch());
        return authenticator;
    }

    @Bean
    public FilterBasedLdapUserSearch userSearch() {
        return new FilterBasedLdapUserSearch(
            ldapBase,
            "(uid={0})",
            contextSource());
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrls);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        contextSource.afterPropertiesSet(); // 重要：初始化上下文源
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
} 