/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 10:00:00
 * @Description: 密码加密解密测试类
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码加密解密功能测试
 */
@Slf4j
public class PasswordCryptoUtilsTest {

    @Test
    @DisplayName("测试基础加密解密功能")
    void testBasicEncryptDecrypt() {
        String originalPassword = "mySecretPassword123";
        String salt = "randomSalt16char";
        
        try {
            // 加密
            String encrypted = PasswordCryptoUtils.encryptPassword(originalPassword, salt);
            assertNotNull(encrypted);
            assertNotEquals(originalPassword, encrypted);
            log.info("原始密码: {}", originalPassword);
            log.info("加密后: {}", encrypted);
            
            // 解密
            String decrypted = PasswordCryptoUtils.decryptPassword(encrypted, salt);
            assertNotNull(decrypted);
            assertEquals(originalPassword, decrypted);
            log.info("解密后: {}", decrypted);
            
        } catch (Exception e) {
            fail("加密解密测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试不同盐值产生不同加密结果")
    void testDifferentSaltProducesDifferentResult() {
        String password = "samePassword";
        String salt1 = "salt1";
        String salt2 = "salt2";
        
        try {
            String encrypted1 = PasswordCryptoUtils.encryptPassword(password, salt1);
            String encrypted2 = PasswordCryptoUtils.encryptPassword(password, salt2);
            
            assertNotEquals(encrypted1, encrypted2);
            log.info("使用盐值 '{}' 加密: {}", salt1, encrypted1);
            log.info("使用盐值 '{}' 加密: {}", salt2, encrypted2);
            
            // 验证各自能正确解密
            assertEquals(password, PasswordCryptoUtils.decryptPassword(encrypted1, salt1));
            assertEquals(password, PasswordCryptoUtils.decryptPassword(encrypted2, salt2));
            
        } catch (Exception e) {
            fail("不同盐值测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试错误盐值解密失败")
    void testWrongSaltDecryptionFails() {
        String password = "testPassword";
        String correctSalt = "correctSalt";
        String wrongSalt = "wrongSalt";
        
        try {
            String encrypted = PasswordCryptoUtils.encryptPassword(password, correctSalt);
            
            // 使用错误的盐值解密应该失败
            assertThrows(RuntimeException.class, () -> {
                PasswordCryptoUtils.decryptPassword(encrypted, wrongSalt);
            });
            
        } catch (Exception e) {
            fail("错误盐值测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试空参数验证")
    void testNullParameterValidation() {
        // 测试空密码
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.encryptPassword(null, "salt");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.encryptPassword("", "salt");
        });
        
        // 测试空盐值
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.encryptPassword("password", null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.encryptPassword("password", "");
        });
        
        // 测试解密空参数
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.decryptPassword(null, "salt");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordCryptoUtils.decryptPassword("encrypted", null);
        });
    }

    @Test
    @DisplayName("测试验证功能")
    void testValidationFunction() {
        String password = "validationTest";
        String salt = "validationSalt";
        
        boolean isValid = PasswordCryptoUtils.validateCrypto(password, salt);
        assertTrue(isValid);
        log.info("加密解密验证通过: {}", isValid);
    }

    @Test
    @DisplayName("测试复杂密码")
    void testComplexPassword() {
        String complexPassword = "MyC0mpl3x!P@ssw0rd#2025";
        String salt = "complexSalt123";
        
        try {
            String encrypted = PasswordCryptoUtils.encryptPassword(complexPassword, salt);
            String decrypted = PasswordCryptoUtils.decryptPassword(encrypted, salt);
            
            assertEquals(complexPassword, decrypted);
            log.info("复杂密码测试通过");
            
        } catch (Exception e) {
            fail("复杂密码测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试中文密码")
    void testChinesePassword() {
        String chinesePassword = "我的密码123";
        String salt = "chineseSalt";
        
        try {
            String encrypted = PasswordCryptoUtils.encryptPassword(chinesePassword, salt);
            String decrypted = PasswordCryptoUtils.decryptPassword(encrypted, salt);
            
            assertEquals(chinesePassword, decrypted);
            log.info("中文密码测试通过");
            
        } catch (Exception e) {
            fail("中文密码测试失败: " + e.getMessage());
        }
    }
}
