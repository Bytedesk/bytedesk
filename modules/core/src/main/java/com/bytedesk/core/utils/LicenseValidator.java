/*
 * @Author: bytedesk.com
 * @Date: 2026-07-07
 * @Description: 许可证验签工具类 (RSA-4096 + SHA-256 签名)
 *   格式: Base64(payload):Base64(RSA_SIGNATURE)
 *   公钥嵌入代码是安全的——公钥只能验签，不能签发许可证。
 *   私钥由 generate_keys.sh 生成，独立存储，不进源码仓库。
 *
 *   Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.exception.LicenseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LicenseValidator {

    private LicenseValidator() {
        // utility class
    }

    /**
     * RSA 公钥 (PEM 格式)，由 generate_keys.sh 生成。
     * 此公钥可安全嵌入代码，泄露不会影响许可证的安全性（私钥才能签发）。
     */
    private static final String PUBLIC_KEY_PEM =
        "-----BEGIN PUBLIC KEY-----\n" +
        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEApdXrzYCZnw+sFlB3zhNE\n" +
        "/kh4uvLw1txR5RJoonLlYLgmK/b/ZFTux9fzUhe18AJkqOhjDSa+4rSL7bPcd0e8\n" +
        "OhxX/IEK5GIEXHjdDInLvF64NwKM5NtggT8pUkTyMZK21fQUKswwWve0ynHDbf+e\n" +
        "ThWGyOV8LHGe7MagcH5FEFnJAKae+VdPwfvPD/2bHaPCf4x/ROb8uLf3g5DKZOl2\n" +
        "E3vQ2zB48qu7xAuCCyQ1QSugdnVjhs8aZwapHHBUiH7tARI3SPHL5ecSTs3b9KW2\n" +
        "/BojsvsO6os0Vhpsd+6qlDZpcHC+FID0lRnsA21tdwaE2yPiy0DYs5IdJl63tD/W\n" +
        "XOOrxIUJRpNDA0MyDAzIh11TsTkxV+4is/QvzHwkEkHWlCsRYCnxD7gg7hPIGhoY\n" +
        "a3pknplsiCTsDoBJQOYTRJx49f/UqoC6R7FvvDTvAMSOc2guknKqqe6QMu/zEnn4\n" +
        "UwylDZTG8EFtyZUwWdeWXXsjaP/wirl61z9njkiiystJwtkaM9a/urLc8/fNcOA2\n" +
        "uqD16YLKBNKPmJd2mmThSVIffxRJdsmvOs0jnOji5a3u1uMIBnuRcjnj/V8Ht8Zd\n" +
        "qc+XB/r0yqaaR/K2Fmb2hKQDI/deeFCUKBxanM1KmAfDPlMi+zV/nGbl5yYfmXz1\n" +
        "2d1jnVa9SeGn1VM9RdMZsicCAwEAAQ==\n" +
        "-----END PUBLIC KEY-----";

    private static volatile PublicKey cachedPublicKey;

    /**
     * 加载 RSA 公钥 (懒加载 + 缓存)
     */
    private static PublicKey getPublicKey() {
        if (cachedPublicKey != null) {
            return cachedPublicKey;
        }
        synchronized (LicenseValidator.class) {
            if (cachedPublicKey != null) {
                return cachedPublicKey;
            }
            try {
                String keyContent = PUBLIC_KEY_PEM
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
                byte[] keyBytes = Base64.getDecoder().decode(keyContent);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                cachedPublicKey = keyFactory.generatePublic(spec);
                // log.info("RSA public key loaded successfully");
            } catch (Exception e) {
                log.error("Failed to load RSA public key", e);
                throw new RuntimeException("License public key initialization failed", e);
            }
            return cachedPublicKey;
        }
    }

    /**
     * 验证 RSA 签名
     */
    private static boolean verifySignature(byte[] payloadBytes, byte[] signatureBytes) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(getPublicKey());
            sig.update(payloadBytes);
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }

    // ============================================================
    // RSA 签名（仅服务端签发许可证时使用，需要私钥）
    // ============================================================

    private static volatile PrivateKey cachedPrivateKey;
    private static volatile String cachedPrivateKeyPath;

    /**
     * 私钥文件路径，优先级：
     *   1. bytedesk.license.private-key-path (application.properties)
     *   2. -Dlicense.private.key.path (JVM 系统属性)
     *   3. LICENSE_PRIVATE_KEY_PATH (环境变量)
     *   4. secrets/encrypt/license_private.pem (默认)
     */
    private static String getPrivateKeyPath() {
        // 1. Spring 配置文件
        BytedeskProperties props = BytedeskProperties.getInstance();
        if (props != null && props.getLicense() != null) {
            String path = props.getLicense().getPrivateKeyPath();
            if (path != null && !path.isBlank()) {
                return path;
            }
        }
        // 2. JVM 系统属性
        String path = System.getProperty("license.private.key.path");
        if (path != null && !path.isBlank()) {
            return path;
        }
        // 3. 环境变量
        path = System.getenv("LICENSE_PRIVATE_KEY_PATH");
        if (path != null && !path.isBlank()) {
            return path;
        }
        // 4. 默认路径
        return "secrets/encrypt/license_private.pem";
    }

    private static PrivateKey getPrivateKey() {
        String currentPath = getPrivateKeyPath();
        if (cachedPrivateKey != null && currentPath.equals(cachedPrivateKeyPath)) {
            return cachedPrivateKey;
        }
        synchronized (LicenseValidator.class) {
            if (cachedPrivateKey != null && currentPath.equals(cachedPrivateKeyPath)) {
                return cachedPrivateKey;
            }
            try {
                Path pemPath = Paths.get(currentPath);
                if (!Files.exists(pemPath)) {
                    log.warn("Private key file not found: {}", pemPath.toAbsolutePath());
                    throw new LicenseException("Private key file not found: " + pemPath.toAbsolutePath());
                }
                String pemContent = Files.readString(pemPath);
                String keyContent = pemContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
                byte[] keyBytes = Base64.getDecoder().decode(keyContent);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                cachedPrivateKey = keyFactory.generatePrivate(spec);
                cachedPrivateKeyPath = currentPath;
                log.info("RSA private key loaded from: {}", pemPath.toAbsolutePath());
            } catch (Exception e) {
                log.warn("Failed to load RSA private key: {}", e.getMessage());
                throw new LicenseException("License private key initialization failed", e);
            }
            return cachedPrivateKey;
        }
    }

    /**
     * 对许可证载荷进行 RSA 签名
     *
     * @param payload 许可证载荷文本
     * @return Base64 编码的签名
     */
    public static String signPayload(String payload) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(getPrivateKey());
            sig.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = sig.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.warn("License signing failed: {}", e.getMessage());
            throw new LicenseException("License signing failed: " + e.getMessage(), e);
        }
    }

    /**
     * 生成完整的许可证字符串（载荷 + 签名）
     *
     * @param payload 许可证载荷文本
     * @return Base64(payload):Base64(signature)
     */
    public static String signLicense(String payload) {
        String payloadB64 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signatureB64 = signPayload(payload);
        return payloadB64 + ":" + signatureB64;
    }

    // ============================================================
    // 启动时本地验签（替代远程在线验证）
    // ============================================================

    /**
     * 应用启动时验证许可证有效性（本地 RSA 验签，不依赖远程服务）。
     *
     * @param licenseKey 原始许可证字符串
     * @return LicenseInfo 验证成功返回许可证信息，失败返回 null
     */
    public static LicenseInfo validateOnStartup(String licenseKey) {
        if (licenseKey == null || licenseKey.isBlank()) {
            log.warn("========================================");
            log.warn("  ⚠ 未配置 licenseKey，跳过许可证验证");
            log.warn("  ⚠ No licenseKey configured, license validation skipped");
            log.warn("  请在配置文件中设置 bytedesk.licenseKey");
            log.warn("========================================");
            return null;
        }

        LicenseInfo info = validate(licenseKey);
        if (info != null && info.isValid()) {
            log.info("✅ 许可证本地验签通过: edition={}, expiry={}",
                    info.getEdition(), info.getExpiryDate());
        } else {
            log.error("========================================");
            log.error("  ❌ 许可证验签失败：签名无效或格式错误");
            log.error("  ❌ License validation FAILED: invalid signature or format");
            log.error("  Please get a valid license from https://www.weiyuai.cn/docs/zh-CN/docs/development/license");
            log.error("========================================");
        }
        return info;
    }

    /**
     * 验证许可证有效性 (RSA-4096 + SHA-256 签名)
     * 格式: Base64(payload):Base64(RSA_SIGNATURE)
     *
     * @param licenseKey 许可证字符串
     * @return LicenseInfo 验证成功返回许可证信息，失败返回 null
     */
    public static LicenseInfo validate(String licenseKey) {
        if (licenseKey == null || licenseKey.isBlank()) {
            log.warn("License key is empty");
            return null;
        }

        try {
            String[] parts = licenseKey.split(":", 2);
            if (parts.length != 2) {
                log.warn("Invalid license format: expected payload:signature");
                return null;
            }

            byte[] payloadBytes = Base64.getDecoder().decode(parts[0]);
            byte[] signatureBytes = Base64.getDecoder().decode(parts[1]);

            if (!verifySignature(payloadBytes, signatureBytes)) {
                log.warn("RSA signature verification FAILED for license");
                return null;
            }

            String payload = new String(payloadBytes, StandardCharsets.UTF_8);
            LicenseInfo info = parsePayload(payload);
            if (info != null) {
                info.setFormat("RSA_SIGNED");
                info.setValid(true);
            }
            // log.debug("RSA signature verified successfully");
            return info;
        } catch (Exception e) {
            log.error("License validation error", e);
            return null;
        }
    }

    /**
     * 解析载荷内容
     * 格式: type:date:edition:ips:domains:name:description[:timestamp]
     */
    static LicenseInfo parsePayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }

        String[] parts = payload.split(":", -1); // -1 to keep trailing empty strings
        if (parts.length < 3) {
            log.warn("Invalid payload format: expected at least 3 parts, got {}", parts.length);
            return null;
        }

        LicenseInfo info = new LicenseInfo();
        info.setUserType("p".equals(parts[0]) ? "paid" : "free");
        info.setExpiryDate(parts[1]);
        // 规范化 edition 为大写，兼容 generate_license.sh（大写）和 LicenseRestService（小写）两种来源
        info.setEdition(parts[2] != null ? parts[2].toUpperCase() : null);
        info.setServerIps(parts.length > 3 ? parts[3] : "");
        info.setServerDomains(parts.length > 4 ? parts[4] : "");
        info.setName(parts.length > 5 ? parts[5] : "");
        info.setDescription(parts.length > 6 ? parts[6] : "");

        return info;
    }

    /**
     * 许可证信息
     */
    @lombok.Getter
    @lombok.Setter
    public static class LicenseInfo {
        private String userType;       // "free" | "paid"
        private String expiryDate;     // "YYYY-MM-DD"
        private String edition;        // "COMMUNITY" | "ENTERPRISE" | "PLATFORM"
        private String serverIps;       // comma-separated
        private String serverDomains;   // comma-separated
        private String name;           // 被授权人
        private String description;    // 描述
        private String format;         // "RSA_SIGNED"
        private boolean valid;

        /**
         * 许可证是否已过期（按天粒度，过期日当天仍视为有效，与前端 isValidationExpired 对齐）。
         * expiryDate 为空或格式非法时视为已过期（fail-closed）。
         */
        public boolean isExpired() {
            if (expiryDate == null || expiryDate.isBlank()) {
                return true;
            }
            try {
                java.time.LocalDate expiry = java.time.LocalDate.parse(expiryDate);
                return java.time.LocalDate.now().isAfter(expiry);
            } catch (Exception e) {
                return true;
            }
        }
    }
}
