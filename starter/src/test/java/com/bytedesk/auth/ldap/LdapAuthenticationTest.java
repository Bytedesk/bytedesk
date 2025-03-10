package com.bytedesk.auth.ldap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.bytedesk.core.rbac.auth.ldap.LdapUserService;

@SpringBootTest
@ActiveProfiles("test")
public class LdapAuthenticationTest {

    @Autowired
    private LdapUserService ldapUserService;

    @Test
    public void testAuthentication() {
        // 测试正确的用户名和密码
        assertTrue(ldapUserService.authenticate("admin", "admin123"));
        assertTrue(ldapUserService.authenticate("test", "test123"));

        // 测试错误的密码
        assertFalse(ldapUserService.authenticate("admin", "wrongpassword"));
        assertFalse(ldapUserService.authenticate("test", "wrongpassword"));

        // 测试不存在的用户
        assertFalse(ldapUserService.authenticate("nonexistent", "password"));
    }
} 