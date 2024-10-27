/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-16 18:19:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:03:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

import com.bytedesk.core.rbac.auth.AuthEntryPoint;
import com.bytedesk.core.rbac.auth.AuthTokenFilter;
import com.bytedesk.core.rbac.user.UserDetailsServiceImpl;

/**
 * https://github.com/pengjinning/spring-boot-3-jwt-security
 * https://github.com/pengjinning/spring-boot-spring-security-jwt-authentication
 * https://dev.to/jean_claude_van_debug/spring-security-mutliple-authentication-providers-new-spring-boot-3-e1j
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // based on token, don't need session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        // .requestMatchers("/actuator/**").authenticated() // monitor endpoints
                        .anyRequest().permitAll())
                // https://docs.spring.io/spring-security/reference/servlet/channels/websocket.html
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin().disable()))
                .authenticationProvider(authenticationProvider())
                // .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()))
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * https://wankhedeshubham.medium.com/spring-boot-security-with-userdetailsservice-and-custom-authentication-provider-3df3a188993f
     * 
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
