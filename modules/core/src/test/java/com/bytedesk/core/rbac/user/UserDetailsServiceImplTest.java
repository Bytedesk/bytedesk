/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms of the license and automatically terminates your rights under the license.
 *   仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.rbac.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bytedesk.core.exception.UserDisabledException;
import com.bytedesk.core.utils.JwtSubject;

/**
 * https://github.com/Bytedesk/bytedesk/issues/28
 * https://github.com/Bytedesk/bytedesk/pull/27
 * 
 * 第三方登录（CAS/LDAP/OIDC 等）自动注册的用户，username 列可能存第三方账号（如学工号），
 * 而 JWT subject 的 username 字段对有 email 的用户取 email，
 * loadUserByUsername* 未命中时需回退按 email 查找，否则认证 401。
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private UserEntity buildCasUser() {
        // 模拟 CAS 自动注册用户：username=学工号，email=学工号@default-email-domain
        UserEntity user = new UserEntity();
        user.setUsername("20260001");
        user.setEmail("20260001@cas.local");
        user.setPlatform("BYTEDESK");
        return user;
    }

    @Test
    void loadUserByUsernameHitsByUsernameColumnWithoutEmailFallback() {
        UserEntity user = buildCasUser();
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("20260001");

        assertEquals("20260001", details.getUsername());
        // username 命中时不应触发 email 回退查询
        verify(userRepository).findByUsernameAndPlatformAndDeletedFalse("20260001", "BYTEDESK");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsernameFallsBackToEmailWhenUsernameMisses() {
        UserEntity user = buildCasUser();
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("20260001@cas.local");

        // 回退命中后，返回真实 DB username（学工号），而非 email
        assertEquals("20260001", details.getUsername());
    }

    @Test
    void loadUserByUsernameThrowsWhenBothUsernameAndEmailMiss() {
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("nobody@cas.local", "BYTEDESK"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndPlatformAndDeletedFalse("nobody@cas.local", "BYTEDESK"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nobody@cas.local"));
    }

    @Test
    void loadUserByUsernameThrowsWhenUserDisabled() {
        UserEntity user = buildCasUser();
        user.setEnabled(false);
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        assertThrows(UserDisabledException.class,
                () -> userDetailsService.loadUserByUsername("20260001"));
    }

    @Test
    void loadUserByUsernameAndPlatformFallsBackToEmail() {
        UserEntity user = buildCasUser();
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        UserDetailsImpl details = userDetailsService.loadUserByUsernameAndPlatform("20260001@cas.local",
                "BYTEDESK");

        assertEquals("20260001", details.getUsername());
    }

    @Test
    void loadUserByJwtSubjectFallsBackToEmailForCasUser() {
        // 复现 CAS 登录场景：subject username=email，DB username=学工号
        UserEntity user = buildCasUser();
        String subject = new JwtSubject("20260001@cas.local", "BYTEDESK").toJson();
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmailAndPlatformAndDeletedFalse("20260001@cas.local", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsernameAndPlatform(subject);

        assertEquals("20260001", details.getUsername());
        assertEquals("20260001@cas.local", ((UserDetailsImpl) details).getEmail());
    }

    @Test
    void loadUserByJwtSubjectHitsByUsernameColumn() {
        UserEntity user = buildCasUser();
        String subject = new JwtSubject("20260001", "BYTEDESK").toJson();
        when(userRepository.findByUsernameAndPlatformAndDeletedFalse("20260001", "BYTEDESK"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsernameAndPlatform(subject);

        assertEquals("20260001", details.getUsername());
        verify(userRepository).findByUsernameAndPlatformAndDeletedFalse("20260001", "BYTEDESK");
        verifyNoMoreInteractions(userRepository);
    }
}
