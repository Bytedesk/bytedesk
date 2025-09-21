/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 10:00:00
 * @Description: CIDR功能测试
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.ip.IpUtils;

import static org.junit.jupiter.api.Assertions.*;

public class IpUtilsCidrTest {

    @Test
    public void testCidrValidation() {
        // 测试有效的CIDR格式
        assertTrue(IpUtils.isValidCidr("192.168.1.0/24"));
        assertTrue(IpUtils.isValidCidr("10.0.0.0/8"));
        assertTrue(IpUtils.isValidCidr("172.16.0.0/12"));
        assertTrue(IpUtils.isValidCidr("0.0.0.0/0"));
        assertTrue(IpUtils.isValidCidr("255.255.255.255/32"));
        
        // 测试无效的CIDR格式
        assertFalse(IpUtils.isValidCidr("192.168.1.0"));
        assertFalse(IpUtils.isValidCidr("192.168.1.0/"));
        assertFalse(IpUtils.isValidCidr("/24"));
        assertFalse(IpUtils.isValidCidr("192.168.1.0/33"));
        assertFalse(IpUtils.isValidCidr("256.168.1.0/24"));
    }

    @Test
    public void testIpInCidrRange() {
        // 测试 /24 子网
        assertTrue(IpUtils.isIpInCidrRange("192.168.1.100", "192.168.1.0/24"));
        assertTrue(IpUtils.isIpInCidrRange("192.168.1.1", "192.168.1.0/24"));
        assertTrue(IpUtils.isIpInCidrRange("192.168.1.254", "192.168.1.0/24"));
        assertFalse(IpUtils.isIpInCidrRange("192.168.2.100", "192.168.1.0/24"));
        
        // 测试 /8 子网
        assertTrue(IpUtils.isIpInCidrRange("10.1.2.3", "10.0.0.0/8"));
        assertTrue(IpUtils.isIpInCidrRange("10.255.255.255", "10.0.0.0/8"));
        assertFalse(IpUtils.isIpInCidrRange("11.1.2.3", "10.0.0.0/8"));
        
        // 测试 /16 子网
        assertTrue(IpUtils.isIpInCidrRange("172.16.1.1", "172.16.0.0/16"));
        assertTrue(IpUtils.isIpInCidrRange("172.16.255.255", "172.16.0.0/16"));
        assertFalse(IpUtils.isIpInCidrRange("172.17.1.1", "172.16.0.0/16"));
        
        // 测试 /32 子网（精确匹配）
        assertTrue(IpUtils.isIpInCidrRange("192.168.1.1", "192.168.1.1/32"));
        assertFalse(IpUtils.isIpInCidrRange("192.168.1.2", "192.168.1.1/32"));
        
        // 测试 /0 子网（匹配所有）
        assertTrue(IpUtils.isIpInCidrRange("192.168.1.1", "0.0.0.0/0"));
        assertTrue(IpUtils.isIpInCidrRange("10.1.2.3", "0.0.0.0/0"));
    }

    @Test
    public void testEdgeCases() {
        // 测试边界情况
        assertFalse(IpUtils.isIpInCidrRange("invalid.ip", "192.168.1.0/24"));
        assertFalse(IpUtils.isIpInCidrRange("192.168.1.1", "invalid.cidr"));
        assertFalse(IpUtils.isIpInCidrRange(null, "192.168.1.0/24"));
        assertFalse(IpUtils.isIpInCidrRange("192.168.1.1", null));
    }
} 