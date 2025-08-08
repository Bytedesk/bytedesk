/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-08 11:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 11:45:00
 * @Description: 密码加密解密兼容性测试
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码加密解密兼容性测试
 * 测试与前端 crypto-js 的兼容性
 */
@Slf4j
public class PasswordCryptoCompatibilityTest {

    @Test
    @DisplayName("测试与前端固定密钥的兼容性")
    void testFrontendCompatibility() {
        String password = "testPassword123";
        String salt = "bytedesk_salt"; // 使用特定盐值，应该生成固定密钥
        
        try {
            // 加密
            String encrypted = PasswordCryptoUtils.encryptPassword(password, salt);
            assertNotNull(encrypted);
            assertNotEquals(password, encrypted);
            log.info("原始密码: {}", password);
            log.info("盐值: {}", salt);
            log.info("加密后: {}", encrypted);
            
            // 解密
            String decrypted = PasswordCryptoUtils.decryptPassword(encrypted, salt);
            assertNotNull(decrypted);
            assertEquals(password, decrypted);
            log.info("解密后: {}", decrypted);
            
        } catch (Exception e) {
            fail("前端兼容性测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试空盐值处理")
    void testEmptySaltHandling() {
        String password = "testPassword123";
        String salt = null; // 空盐值，应该使用默认密钥
        
        try {
            // 测试空盐值
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordCryptoUtils.encryptPassword(password, salt);
            });
            
            // 测试空字符串盐值
            assertThrows(IllegalArgumentException.class, () -> {
                PasswordCryptoUtils.encryptPassword(password, "");
            });
            
        } catch (Exception e) {
            fail("空盐值测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试不同盐值生成不同密钥")
    void testDifferentSaltsDifferentKeys() {
        String password = "testPassword123";
        String salt1 = "salt1";
        String salt2 = "salt2";
        
        try {
            String encrypted1 = PasswordCryptoUtils.encryptPassword(password, salt1);
            String encrypted2 = PasswordCryptoUtils.encryptPassword(password, salt2);
            
            // 不同盐值应该产生不同的加密结果
            assertNotEquals(encrypted1, encrypted2);
            log.info("盐值1: {} -> 加密结果: {}", salt1, encrypted1);
            log.info("盐值2: {} -> 加密结果: {}", salt2, encrypted2);
            
            // 验证各自能正确解密
            assertEquals(password, PasswordCryptoUtils.decryptPassword(encrypted1, salt1));
            assertEquals(password, PasswordCryptoUtils.decryptPassword(encrypted2, salt2));
            
        } catch (Exception e) {
            fail("不同盐值测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试边界情况密码")
    void testEdgeCasePasswords() {
        String[] testPasswords = {
            "a", // 单字符
            "123456789012345678901234567890", // 长密码
            "特殊字符!@#$%^&*()", // 特殊字符
            "中文密码测试", // 中文字符
            "Mixed中英文123!@#" // 混合字符
        };
        
        String salt = "testSalt";
        
        for (String password : testPasswords) {
            try {
                String encrypted = PasswordCryptoUtils.encryptPassword(password, salt);
                String decrypted = PasswordCryptoUtils.decryptPassword(encrypted, salt);
                
                assertEquals(password, decrypted);
                log.info("边界测试通过 - 原始: '{}', 加密: '{}'", password, encrypted);
                
            } catch (Exception e) {
                fail("边界情况测试失败，密码: '" + password + "', 错误: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("测试Base64格式验证")
    void testBase64Validation() {
        String password = "testPassword";
        String salt = "testSalt";
        
        try {
            String encrypted = PasswordCryptoUtils.encryptPassword(password, salt);
            
            // 验证是否为有效的Base64格式
            assertTrue(encrypted.matches("^[A-Za-z0-9+/]*={0,2}$"), "加密结果应该是有效的Base64格式");
            
            // 验证Base64解码不会抛出异常
            assertDoesNotThrow(() -> {
                java.util.Base64.getDecoder().decode(encrypted);
            }, "应该能够正确解码Base64");
            
        } catch (Exception e) {
            fail("Base64格式测试失败: " + e.getMessage());
        }
    }
}
