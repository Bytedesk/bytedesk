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
package com.bytedesk.starter.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.starter.service.JasyptService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Jasypt 加密解密接口
 * 使用说明: 不再需要使用命令行脚本，直接调用 API 完成加密/解密
 * 
 * 示例：
 * POST /jasypt/encrypt?plaintext=r8FqfdbWUaN3
 * GET /jasypt/decrypt?ciphertext=YOUR_ENCRYPTED_TEXT
 */
@Slf4j
@Tag(name = "jasypt - 加密解密")
@RestController
@RequestMapping("/jasypt")
@RequiredArgsConstructor
public class JasyptController {

    private final JasyptService jasyptService;

    /**
     * 加密字符串
     * 
     * @param body 请求体包含：plaintext（明文）和 password（可选，不提供则使用应用启动密钥）
     * @return 加密结果
     * 
     * 示例：POST /jasypt/encrypt
     * {
     *   "plaintext": "r8FqfdbWUaN3",
     *   "password": "NLHp3u3Usr/EeXBJITPIOlqWWCMNaolG3dtpEBllZpA="
     * }
     */
    @PostMapping("/encrypt")
    @Operation(summary = "加密字符串", description = "将明文加密为 BASE64 密文。使用 JSON body 方式，无需 URL encode。如果提供 password 参数，将使用该密码加密；否则使用应用启动时的密钥。")
    public ResponseEntity<?> encrypt(@RequestBody Map<String, String> body) {
        // 仅支持 request body 方式，避免 URL encode 问题
        String textToEncrypt = body != null ? body.get("plaintext") : null;
        String passwordToUse = body != null ? body.get("password") : null;

        if (textToEncrypt == null || textToEncrypt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(JsonResult.error("plaintext parameter is required"));
        }

        try {
            String encrypted;
            String withEncFormat;
            
            if (passwordToUse != null && !passwordToUse.isEmpty()) {
                // 使用指定的密码加密（只加密一次）
                encrypted = jasyptService.encryptWithPassword(textToEncrypt, passwordToUse);
                withEncFormat = "ENC(" + encrypted + ")";
            } else {
                // 使用应用配置的密钥加密（只加密一次）
                encrypted = jasyptService.encrypt(textToEncrypt);
                withEncFormat = "ENC(" + encrypted + ")";
            }

            Map<String, Object> data = new HashMap<>();
            data.put("plaintext", textToEncrypt);
            data.put("ciphertext", encrypted);
            data.put("encFormat", withEncFormat);
            data.put("algorithm", "PBEWITHHMACSHA512ANDAES_256");
            if (passwordToUse != null && !passwordToUse.isEmpty()) {
                data.put("encryptionMode", "custom password");
            } else {
                data.put("encryptionMode", "application default key");
            }

            log.info("Encryption successful for plaintext: {}", textToEncrypt);
            return ResponseEntity.ok(JsonResult.success("Encryption successful", data));
        } catch (Exception e) {
            log.error("Encryption error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(JsonResult.error("Encryption error: " + e.getMessage()));
        }
    }

    /**
     * 解密字符串
     * 
     * @param body 请求体包含：ciphertext（密文）和 password（可选，不提供则使用应用启动密钥）
     * @return 解密结果
     * 
     * 示例：POST /jasypt/decrypt
     * {
     *   "ciphertext": "YOUR_ENCRYPTED_TEXT",
     *   "password": "NLHp3u3Usr/EeXBJITPIOlqWWCMNaolG3dtpEBllZpA="
     * }
     */
    @PostMapping("/decrypt")
    @Operation(summary = "解密字符串", description = "将 BASE64 密文解密为明文。使用 JSON body 方式，无需 URL encode。如果提供 password 参数，将使用该密码解密；否则使用应用启动时的密钥。")
    public ResponseEntity<?> decrypt(@RequestBody Map<String, String> body) {
        // 仅支持 request body 方式，避免 URL encode 问题
        String textToDecrypt = body != null ? body.get("ciphertext") : null;
        String passwordToUse = body != null ? body.get("password") : null;

        if (textToDecrypt == null || textToDecrypt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(JsonResult.error("ciphertext parameter is required"));
        }

        try {
            String decrypted;
            
            if (passwordToUse != null && !passwordToUse.isEmpty()) {
                // 使用指定的密码解密
                decrypted = jasyptService.decryptWithPassword(textToDecrypt, passwordToUse);
            } else {
                // 使用应用配置的密钥解密
                decrypted = jasyptService.decrypt(textToDecrypt);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("ciphertext", textToDecrypt);
            data.put("plaintext", decrypted);
            if (passwordToUse != null && !passwordToUse.isEmpty()) {
                data.put("decryptionMode", "custom password");
            } else {
                data.put("decryptionMode", "application default key");
            }

            log.info("Decryption successful");
            return ResponseEntity.ok(JsonResult.success("Decryption successful", data));
        } catch (Exception e) {
            log.error("Decryption error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(JsonResult.error("Decryption error: " + e.getMessage()));
        }
    }

    /**
     * 获取加密工具信息和使用说明
     * 
     * @return 工具信息
     * 
     * 示例：GET /jasypt/info
     */
    @GetMapping("/info")
    @Operation(summary = "获取加密工具信息")
    public ResponseEntity<?> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Jasypt Encryption Tool");
        info.put("version", "3.0.5");
        info.put("description", "Replace CLI encryption with REST API");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("encrypt", "POST /jasypt/encrypt - 使用 JSON body 加密明文");
        endpoints.put("decrypt", "POST /jasypt/decrypt - 使用 JSON body 解密密文");
        endpoints.put("info", "GET /jasypt/info - 查看此信息");
        info.put("endpoints", endpoints);

        Map<String, String> notes = new HashMap<>();
        notes.put("format", "加密后的密文应使用 ENC(ciphertext) 格式在 application.properties 中配置");
        notes.put("request_method", "所有加密/解密操作均使用 POST 请求，通过 JSON body 传递参数（避免 URL encode 问题）");
        notes.put("response_format", "加密响应包含: plaintext, ciphertext, encFormat, algorithm, encryptionMode");
        notes.put("response_format_decrypt", "解密响应包含: ciphertext, plaintext, decryptionMode");
        notes.put("error_handling", "任何错误都将返回 400/500 状态码和错误消息");
        info.put("notes", notes);

        return ResponseEntity.ok(JsonResult.success("Jasypt Tool Info", info));
    }
}
