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
            
            // 解码Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);

            log.debug("Base64解码后数据长度: {}", encryptedBytes.length);

            // 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 手动去除PKCS7填充
            byte[] unpaddedBytes = removePKCS7Padding(decryptedBytes);

            String decryptedPassword = new String(unpaddedBytes, StandardCharsets.UTF_8);

            if (decryptedPassword.isEmpty()) {
                throw new RuntimeException("解密失败，可能是密钥错误");
            }

            log.debug("解密成功，密码长度: {}", decryptedPassword.length());

            return decryptedPassword;
            
        } catch (Exception e) {
            log.error("密码解密失败", e);
            throw new RuntimeException("密码解密失败: " + e.getMessage());
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
            throw new RuntimeException("生成密钥失败: " + e.getMessage());
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

    /**
     * 去除PKCS7填充
     * @param paddedData 包含填充的数据
     * @return 去除填充后的数据
     */
    private static byte[] removePKCS7Padding(byte[] paddedData) {
        if (paddedData == null || paddedData.length == 0) {
            return paddedData;
        }

        // 获取填充长度（最后一个字节的值）
        int paddingLength = paddedData[paddedData.length - 1] & 0xFF;

        // 验证填充
        if (paddingLength < 1 || paddingLength > 16) {
            // 无效的填充长度，返回原数据
            log.warn("Invalid PKCS7 padding length: {}", paddingLength);
            return paddedData;
        }

        // 验证所有填充字节都正确
        for (int i = 1; i <= paddingLength; i++) {
            if ((paddedData[paddedData.length - i] & 0xFF) != paddingLength) {
                // 填充不正确，返回原数据
                log.warn("Invalid PKCS7 padding");
                return paddedData;
            }
        }

        // 去除填充
        byte[] unpadded = new byte[paddedData.length - paddingLength];
        System.arraycopy(paddedData, 0, unpadded, 0, unpadded.length);

        log.debug("Removed PKCS7 padding: {} -> {} bytes", paddedData.length, unpadded.length);

        return unpadded;
    }
}
