/*
 * @Author: jackning270580156@qq.com
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 10:31:15
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 10.1https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact:270580156q.com
 * Copyright (c) 2024 bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码哈希工具类
 * 用于验证前端传来的密码哈希
 */
@Slf4j
public class PasswordHashUtils {
    /**
     * 验证密码哈希
     * 使用SHA-256对密码进行哈希处理，与前端保持一致
     * @param password 原始密码
     * @param salt 盐值
     * @param expectedHash 期望的哈希值
     * @return 是否验证通过
     */
    public static boolean verifyPasswordHash(String password, String salt, String expectedHash) {
        try {
            if (password == null || salt == null || expectedHash == null) {
                return false;
            }

            // 使用与前端相同的算法：SHA-256
            String saltedPassword = password + salt;
            String actualHash = sha256(saltedPassword);
            
            log.debug("Password verification: password={}, salt={}, expectedHash={}, actualHash={}", 
                     password, salt, expectedHash, actualHash);
            
            return expectedHash.equals(actualHash);
        } catch (Exception e) {
            log.error("Error verifying password hash, e", e);
            return false;
        }
    }

    /**
     * 生成SHA-256    * @param input 输入字符串
     * @return SHA-256    */
    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));      
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        } catch (Exception e) {
            log.error("Error generating SHA-256 hash", e);
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    /**
     * 生成随机盐值
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        return result.toString();
    }
} 