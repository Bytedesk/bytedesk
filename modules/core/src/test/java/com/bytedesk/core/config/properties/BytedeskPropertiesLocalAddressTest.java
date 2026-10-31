/*
 * @Author: bytedesk.com
 * @Date: 2026-10-29
 * @Description: 本地地址判定单元测试：按地址解析分类，不做字符串前缀匹配（规划 5.4）。
 *
 *   Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.config.properties;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * validateServerIpOrDomain 的本地地址豁免分支通过 isLocalAddress 判定，
 * 这里直接测试私有静态方法，覆盖 IPv4/IPv6/回环/公网/畸形输入。
 */
public class BytedeskPropertiesLocalAddressTest {

    private static boolean isLocal(String hostname) throws Exception {
        Method method = BytedeskProperties.class.getDeclaredMethod("isLocalAddress", String.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(null, hostname);
    }

    // ============ IPv4 回环/内网/链路本地 ============

    @Test
    void testLoopbackIpv4() throws Exception {
        assertTrue(isLocal("127.0.0.1"));
        assertTrue(isLocal("127.1.2.3"));
        assertTrue(isLocal("127.255.255.255"));
    }

    @Test
    void testPrivateIpv4() throws Exception {
        assertTrue(isLocal("10.0.0.1"));
        assertTrue(isLocal("10.255.255.255"));
        assertTrue(isLocal("172.16.0.1"));
        assertTrue(isLocal("172.31.255.255"));
        assertTrue(isLocal("192.168.1.100"));
    }

    /**
     * 核心：原实现 startsWith("172.") 把 172.0.0.0/15、172.217.x.x 等公网段误判为本地，
     * 修复后仅 172.16.0.0 – 172.31.255.255 视为本地。
     */
    @Test
    void testPublicIpv4NotMisclassified() throws Exception {
        assertFalse(isLocal("172.217.14.206"));
        assertFalse(isLocal("172.32.0.1"));
        assertFalse(isLocal("172.15.255.255"));
        assertFalse(isLocal("8.8.8.8"));
        assertFalse(isLocal("1.1.1.1"));
        assertFalse(isLocal("192.169.1.1"));
    }

    @Test
    void testLinkLocalAndThisNetwork() throws Exception {
        assertTrue(isLocal("169.254.1.1"));
        assertTrue(isLocal("0.0.0.0"));
        assertFalse(isLocal("169.253.1.1"));
    }

    // ============ IPv6 ============

    @Test
    void testLoopbackIpv6() throws Exception {
        assertTrue(isLocal("::1"));
        assertTrue(isLocal("[::1]"));
        assertTrue(isLocal("[::1]:8080"));
    }

    @Test
    void testLocalScopeIpv6() throws Exception {
        assertTrue(isLocal("fe80::1"));
        assertTrue(isLocal("fe80::1%eth0"));
        assertTrue(isLocal("fc00::1"));
        assertTrue(isLocal("fd12:3456:789a::1"));
    }

    @Test
    void testPublicIpv6NotLocal() throws Exception {
        assertFalse(isLocal("2001:db8::1"));
        assertFalse(isLocal("2607:f8b0:4005:80a::200e"));
        assertFalse(isLocal("::ffff:8.8.8.8"));
    }

    @Test
    void testIpv4MappedLoopback() throws Exception {
        assertTrue(isLocal("::ffff:127.0.0.1"));
        assertTrue(isLocal("::ffff:192.168.1.1"));
    }

    // ============ 主机名与畸形输入 ============

    @Test
    void testHostnameHandling() throws Exception {
        assertTrue(isLocal("localhost"));
        assertTrue(isLocal("LOCALHOST"));
        assertTrue(isLocal(" localhost "));
        // 非本地域名不豁免
        assertFalse(isLocal("example.com"));
        assertFalse(isLocal("sub.localhost.example.com"));
    }

    @Test
    void testMalformedInputFailsClosed() throws Exception {
        assertFalse(isLocal(null));
        assertFalse(isLocal(""));
        assertFalse(isLocal("   "));
        assertFalse(isLocal("999.999.999.999"));
        assertFalse(isLocal("172.16"));
        assertFalse(isLocal("172"));
        assertFalse(isLocal("not-an-ip"));
    }
}
