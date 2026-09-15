/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 10:00:00
 * @Description: 密码加密解密工具类
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import com.bytedesk.core.constant.I18Consts;

import lombok.extern.slf4j.Slf4j;

/**
 * 密码加密解密工具类
 * 与前端JavaScript实现保持一致的AES加密解密
 * 
 * 修复说明：
 * - 统一前后端密钥生成方式，解决 BadPaddingException 问题
 * - 对于特定盐值 "bytedesk_salt"，使用固定密钥 "bytedesk_license"（与前端保持一致）
 * - 对于其他盐值，使用SHA-256哈希生成16字节密钥（128位AES）
 * - 加密算法：AES/ECB/PKCS5Padding
 */
@Slf4j
public class PasswordCryptoUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 使用AES加密密码
     * @param password 原始密码
     * @param salt 盐值，用作加密密钥的一部分
     * @return 加密后的密码（Base64编码）
     */
    public static String encryptPassword(String password, String salt) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("盐值不能为空");
        }

        try {
            // 使用盐值生成固定长度的密钥（与前端保持一致）
            String key = generateKeyFromSalt(salt);
            
            // Avoid logging secrets (salt/key). Keep only safe diagnostics.
            log.debug("加密参数: password长度={}, salt长度={}", password.length(), salt.length());
            
            // 创建密钥规范
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            
            // 创建加密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            
            // 加密
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            
            // 返回Base64编码的结果
            String result = Base64.getEncoder().encodeToString(encryptedBytes);
            
            log.debug("加密完成，结果长度: {}", result.length());
            
            return result;
            
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败: " + e.getMessage());
        }
    }

    /**
     * 使用AES解密密码
     * @param encryptedPassword 加密后的密码（Base64编码）
     * @param salt 盐值，用作解密密钥的一部分
     * @return 解密后的原始密码
     */
    public static String decryptPassword(String encryptedPassword, String salt) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            throw new IllegalArgumentException("加密密码不能为空");
        }
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("盐值不能为空");
        }

        // P5：解密前对密文做格式预检，非法载荷尽早拒绝，
        // 避免后续产生看似底层加密故障的告警噪音
        byte[] encryptedBytes;
        try {
            encryptedBytes = Base64.getDecoder().decode(encryptedPassword);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid hash login payload: base64 decode failed");
            throw new IllegalArgumentException("非法 hash 登录载荷（Base64 解码失败）", e);
        }
        if (encryptedBytes.length == 0 || encryptedBytes.length % 16 != 0) {
            log.warn("Invalid hash login payload: not AES block aligned, encryptedLength={}", encryptedBytes.length);
            throw new IllegalArgumentException("非法 hash 登录载荷（密文长度非 AES 块对齐）");
        }

        try {
            // 使用盐值生成固定长度的密钥（与前端保持一致）
            String key = generateKeyFromSalt(salt);

            // Avoid logging secrets (encrypted payload/salt/key). Keep only safe diagnostics.
            log.debug("解密参数: encryptedPassword长度={}, salt长度={}", encryptedPassword.length(), salt.length());

            // 创建密钥规范
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 创建解密器
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            log.debug("Base64解码后数据长度: {}", encryptedBytes.length);

            // 解密：AES/ECB/PKCS5Padding 模式下 doFinal 已完成去填充，
            // 不再做二次 removePKCS7Padding（旧实现对明文末字节的误判是
            // "Invalid PKCS7 padding length" 告警噪音的来源）
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String decryptedPassword = new String(decryptedBytes, StandardCharsets.UTF_8);

            if (decryptedPassword.isEmpty()) {
                throw new RuntimeException(I18Consts.I18N_PASSWORD_DECRYPT_KEY_INVALID);
            }

            log.debug("解密成功，密码长度: {}", decryptedPassword.length());

            return decryptedPassword;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new RuntimeException(I18Consts.I18N_PASSWORD_DECRYPT_FAILED);
        }
    }

    /**
     * 从盐值生成密钥
     * 根据盐值确定密钥生成方式，与前端JavaScript实现保持一致
     * @param salt 盐值
     * @return 密钥字符串
     */
    private static String generateKeyFromSalt(String salt) {
        try {
            // 如果盐值是特定的值，使用固定密钥（与前端 crypto-js 保持一致）
            if ("bytedesk_salt".equals(salt) || salt == null || salt.isEmpty()) {
                return "bytedesk_license"; // 与前端保持一致的16字节密钥
            }
            
            // 对于其他盐值，使用SHA-256哈希生成密钥
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(salt.getBytes(StandardCharsets.UTF_8));
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // 取前16个字符作为密钥（128位AES，与前端保持一致）
            return hexString.toString().substring(0, 16);
            
        } catch (Exception e) {
            throw new RuntimeException(I18Consts.I18N_PASSWORD_KEY_GENERATE_FAILED);
        }
    }

    /**
     * 验证加密解密功能
     * @param originalPassword 原始密码
     * @param salt 盐值
     * @return 是否验证成功
     */
    public static boolean validateCrypto(String originalPassword, String salt) {
        try {
            String encrypted = encryptPassword(originalPassword, salt);
            String decrypted = decryptPassword(encrypted, salt);
            return originalPassword.equals(decrypted);
        } catch (Exception e) {
            log.error("加密解密验证失败", e);
            return false;
        }
    }
}
