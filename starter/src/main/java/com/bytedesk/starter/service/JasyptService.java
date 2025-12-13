/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.service;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.starter.config.properties.JasyptSupportProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jasypt 加密服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JasyptService {

    private final StringEncryptor stringEncryptor;
    private final JasyptSupportProperties jasyptProperties;

    /**
     * 使用应用配置的密钥加密字符串
     * @param plaintext 明文
     * @return 密文（BASE64编码）
     */
    public String encrypt(String plaintext) {
        try {
            String encrypted = stringEncryptor.encrypt(plaintext);
            log.info("Encrypt successful");
            return encrypted;
        } catch (Exception e) {
            log.error("Encrypt failed: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    /**
     * 使用应用配置的密钥解密字符串
     * @param ciphertext 密文（BASE64编码）
     * @return 明文
     */
    public String decrypt(String ciphertext) {
        try {
            String decrypted = stringEncryptor.decrypt(ciphertext);
            log.info("Decrypt successful");
            return decrypted;
        } catch (Exception e) {
            log.error("Decrypt failed: {}", e.getMessage(), e);
            throw new RuntimeException("Decryption failed: " + e.getMessage());
        }
    }

    /**
     * 使用指定的密码加密字符串（使用配置中的算法等参数）
     * @param plaintext 明文
     * @param password 密码
     * @return 密文（BASE64编码）
     */
    public String encryptWithPassword(String plaintext, String password) {
        try {
            StringEncryptor encryptor = createEncryptor(password);
            String encrypted = encryptor.encrypt(plaintext);
            log.info("Encrypt with custom password successful");
            return encrypted;
        } catch (Exception e) {
            log.error("Encrypt with custom password failed: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    /**
     * 使用指定的密码解密字符串（使用配置中的算法等参数）
     * @param ciphertext 密文（BASE64编码）
     * @param password 密码
     * @return 明文
     */
    public String decryptWithPassword(String ciphertext, String password) {
        try {
            StringEncryptor decryptor = createEncryptor(password);
            String decrypted = decryptor.decrypt(ciphertext);
            log.info("Decrypt with custom password successful");
            return decrypted;
        } catch (Exception e) {
            log.error("Decrypt with custom password failed: {}", e.getMessage(), e);
            throw new RuntimeException("Decryption failed: " + e.getMessage());
        }
    }

    /**
     * 包装成 ENC() 格式用于 properties 文件
     * @param plaintext 明文
     * @return ENC(密文) 格式
     */
    public String encryptWithEncFormat(String plaintext) {
        // 创建一次加密器，不重复加密
        try {
            String encrypted = stringEncryptor.encrypt(plaintext);
            log.info("Encrypt and wrap with ENC format successful");
            return "ENC(" + encrypted + ")";
        } catch (Exception e) {
            log.error("Encrypt and wrap with ENC format failed: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    /**
     * 使用指定密码包装成 ENC() 格式用于 properties 文件
     * @param plaintext 明文
     * @param password 密码
     * @return ENC(密文) 格式
     */
    public String encryptWithPasswordAndEncFormat(String plaintext, String password) {
        // 创建一次加密器，用同一个实例加密
        try {
            StringEncryptor encryptor = createEncryptor(password);
            String encrypted = encryptor.encrypt(plaintext);
            log.info("Encrypt with custom password and ENC format successful");
            return "ENC(" + encrypted + ")";
        } catch (Exception e) {
            log.error("Encrypt with custom password and ENC format failed: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    /**
     * 创建根据配置初始化的加密器
     * @param password 密码
     * @return 初始化好的 PooledPBEStringEncryptor
     */
    private StringEncryptor createEncryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        
        // 设置密码
        config.setPassword(password);
        
        // 设置算法
        String algorithm = jasyptProperties.getAlgorithm();
        if (StringUtils.hasText(algorithm)) {
            config.setAlgorithm(algorithm);
        } else {
            config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        }
        
        // 设置输出格式
        String stringOutputType = jasyptProperties.getStringOutputType();
        if (StringUtils.hasText(stringOutputType)) {
            config.setStringOutputType(stringOutputType);
        }
        
        // 设置密钥获取迭代次数
        if (jasyptProperties.getKeyObtentionIterations() > 0) {
            config.setKeyObtentionIterations(String.valueOf(jasyptProperties.getKeyObtentionIterations()));
        }
        
        // 设置 IV 生成器
        String ivGeneratorClassName = jasyptProperties.getIvGeneratorClassName();
        if (StringUtils.hasText(ivGeneratorClassName)) {
            config.setIvGeneratorClassName(ivGeneratorClassName);
        }
        
        // 设置 Salt 生成器
        String saltGeneratorClassName = jasyptProperties.getSaltGeneratorClassName();
        if (StringUtils.hasText(saltGeneratorClassName)) {
            config.setSaltGeneratorClassName(saltGeneratorClassName);
        }
        
        // 设置线程池大小
        if (jasyptProperties.getPoolSize() > 0) {
            config.setPoolSize(String.valueOf(jasyptProperties.getPoolSize()));
        }
        
        // 设置 Provider（如果配置了）
        String providerClassName = jasyptProperties.getProviderClassName();
        if (StringUtils.hasText(providerClassName)) {
            config.setProviderClassName(providerClassName);
        }
        
        // 创建并配置加密器
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        
        log.debug("Created encryptor with algorithm: {}, stringOutputType: {}, keyObtentionIterations: {}", 
            algorithm, stringOutputType, jasyptProperties.getKeyObtentionIterations());
        
        return encryptor;
    }
}
