/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-16 18:19:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-09 12:43:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
@Description("Web Security Configuration - Web安全配置类，配置JWT认证、CORS、CSRF等安全策略")
public class WebSecurityConfig {

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
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    // 系统状态监控接口允许匿名访问
                    // .requestMatchers("/api/system/**").permitAll()
                    // SSE 端点特殊处理，在授权检查前确保有效认证
                    // .requestMatchers("/api/v1/agent/message/sse").authenticated()
                    .requestMatchers("/api/**").authenticated()
                    // .requestMatchers("/actuator/**").authenticated() // monitor endpoints
                    .anyRequest().permitAll())
            // https://docs.spring.io/spring-security/reference/servlet/channels/websocket.html
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin().disable())
                // .xssProtection(xss -> xss.enable())
                // .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"))
            )
            // 添加自定义的 SSE 异常处理入口点
            // .exceptionHandling(exceptionHandling -> 
            //     exceptionHandling
            //         .defaultAuthenticationEntryPointFor(
            //             new SSEAuthenticationEntryPoint(), 
            //             new AntPathRequestMatcher("/api/v1/agent/message/sse")
            //         )
            //         // 保持现有的认证入口点配置
            //         // ...existing code...
            // )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //
        return http.build();
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
        // 直接创建 UserDetailsServiceImpl 实例，避免Spring Security的警告
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(new UserDetailsServiceImpl());
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
