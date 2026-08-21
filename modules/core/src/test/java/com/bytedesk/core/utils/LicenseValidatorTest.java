/*
 * @Author: bytedesk.com
 * @Date: 2026-08-21
 * @Description: LicenseValidator 单元测试：验签、防篡改、过期判断、载荷解析。
 *
 *   Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.bytedesk.core.utils.LicenseValidator.LicenseInfo;

import static org.junit.jupiter.api.Assertions.*;

public class LicenseValidatorTest {

    private static String privateKeyPath;

    @BeforeAll
    static void locatePrivateKey() {
        privateKeyPath = locatePrivateKeyFile();
        if (privateKeyPath != null) {
            System.setProperty("license.private.key.path", privateKeyPath);
        }
    }

    /**
     * 从当前工作目录向上查找 secrets/encrypt/license_private.pem（仅在需要签发的用例中使用）。
     */
    private static String locatePrivateKeyFile() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        while (dir != null) {
            Path key = dir.resolve("secrets/encrypt/license_private.pem");
            if (Files.exists(key)) {
                return key.toString();
            }
            dir = dir.getParent();
        }
        String prop = System.getProperty("license.private.key.path");
        if (prop != null && Files.exists(Paths.get(prop))) {
            return prop;
        }
        return null;
    }

    private static boolean hasPrivateKey() {
        return privateKeyPath != null;
    }

    // ============================================================
    // 验签 + 防篡改（需要私钥签发，私钥缺失时跳过）
    // ============================================================

    @Test
    void testSignAndValidateRoundTrip() {
        Assumptions.assumeTrue(hasPrivateKey(), "私钥缺失，跳过签发/验签回环测试");

        String license = LicenseValidator.signLicense("p:2126-01-01:enterprise:::test:desc");
        assertNotNull(license);
        assertTrue(license.contains(":"));

        LicenseInfo info = LicenseValidator.validate(license);
        assertNotNull(info);
        assertTrue(info.isValid());
        assertEquals("paid", info.getUserType());
        assertEquals("2126-01-01", info.getExpiryDate());
        assertEquals("ENTERPRISE", info.getEdition());
        assertEquals("RSA_SIGNED", info.getFormat());
    }

    @Test
    void testTamperedSignatureFails() {
        Assumptions.assumeTrue(hasPrivateKey(), "私钥缺失，跳过防篡改测试");

        String license = LicenseValidator.signLicense("f:2126-01-01:PLATFORM:::name:desc");
        String tampered = license.substring(0, license.length() - 1)
                + (license.endsWith("A") ? "B" : "A");

        LicenseInfo info = LicenseValidator.validate(tampered);
        assertTrue(info == null || !info.isValid(), "篡改签名应验签失败");
    }

    @Test
    void testTamperedPayloadFails() {
        Assumptions.assumeTrue(hasPrivateKey(), "私钥缺失，跳过防篡改测试");

        String license = LicenseValidator.signLicense("f:2126-01-01:PLATFORM:::name:desc");
        int colon = license.indexOf(':');
        String payloadB64 = license.substring(0, colon);
        String signature = license.substring(colon + 1);
        char first = payloadB64.charAt(0);
        String tamperedPayload = (first == 'A' ? 'B' : 'A') + payloadB64.substring(1);

        LicenseInfo info = LicenseValidator.validate(tamperedPayload + ":" + signature);
        assertTrue(info == null || !info.isValid(), "篡改载荷应验签失败");
    }

    @Test
    void testInvalidFormatRejected() {
        assertNull(LicenseValidator.validate(""));
        assertNull(LicenseValidator.validate("not-a-license"));
        assertNull(LicenseValidator.validate("onlyOnePart"));
    }

    // ============================================================
    // 过期判断（无需私钥）
    // ============================================================

    @Test
    void testIsExpiredBlankOrInvalid() {
        LicenseInfo info = new LicenseInfo();
        assertTrue(info.isExpired(), "空 expiryDate 应视为已过期");
        info.setExpiryDate("   ");
        assertTrue(info.isExpired(), "空白 expiryDate 应视为已过期");
        info.setExpiryDate("not-a-date");
        assertTrue(info.isExpired(), "非法日期应视为已过期");
    }

    @Test
    void testIsExpiredPastDate() {
        LicenseInfo info = new LicenseInfo();
        info.setExpiryDate("2020-01-01");
        assertTrue(info.isExpired());
    }

    @Test
    void testIsExpiredFutureDate() {
        LicenseInfo info = new LicenseInfo();
        info.setExpiryDate("2999-01-01");
        assertFalse(info.isExpired());
    }

    @Test
    void testIsExpiredTodayIsNotExpired() {
        LicenseInfo info = new LicenseInfo();
        info.setExpiryDate(java.time.LocalDate.now().toString());
        assertFalse(info.isExpired(), "到期日当天应视为仍然有效（与前端按天比较语义一致）");
    }

    // ============================================================
    // 载荷解析（无需私钥）
    // ============================================================

    @Test
    void testParsePayloadNormalizesEdition() {
        LicenseInfo info = LicenseValidator.parsePayload("p:2026-08-21:enterprise:::");
        assertNotNull(info);
        assertEquals("paid", info.getUserType());
        assertEquals("2026-08-21", info.getExpiryDate());
        assertEquals("ENTERPRISE", info.getEdition());

        info = LicenseValidator.parsePayload("f:2026-08-21:community");
        assertNotNull(info);
        assertEquals("free", info.getUserType());
        assertEquals("COMMUNITY", info.getEdition());

        info = LicenseValidator.parsePayload("p:2026-08-21:PLATFORM:1.2.3.4:example.com:name:desc");
        assertNotNull(info);
        assertEquals("PLATFORM", info.getEdition());
        assertEquals("1.2.3.4", info.getServerIps());
        assertEquals("example.com", info.getServerDomains());
        assertEquals("name", info.getName());
        assertEquals("desc", info.getDescription());
    }

    @Test
    void testParsePayloadTooShortReturnsNull() {
        assertNull(LicenseValidator.parsePayload("only-one-part"));
        assertNull(LicenseValidator.parsePayload(""));
        assertNull(LicenseValidator.parsePayload(null));
    }
}
