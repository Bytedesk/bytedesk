package com.bytedesk.starter.config;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

import com.bytedesk.core.auth.AuthEntryPoint;
import com.bytedesk.core.auth.AuthJwtTokenFilter;
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
            // .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll())
            // https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin().disable()))
            .authenticationProvider(authenticationProvider())
            // .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()))
            .addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // .httpBasic(withDefaults())
        // .formLogin(withDefaults());
        //
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthJwtTokenFilter authJwtTokenFilter() {
        return new AuthJwtTokenFilter();
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

    // https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // @Bean
    // public WebSecurityCustomizer webSecurityCustomizer() {
    // return (web) -> web.ignoring()
    // // .requestMatchers(HttpMethod.OPTIONS, "**", "/**", "/stomp/**")
    // .requestMatchers("**")
    // ;
    // }

}
