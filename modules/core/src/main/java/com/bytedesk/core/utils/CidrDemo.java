/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-20 10:00:00
 * @Description: CIDR功能演示
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

public class CidrDemo {
    
    public static void main(String[] args) {
        System.out.println("=== CIDR 功能演示 ===");
        
        // 测试CIDR验证
        System.out.println("\n1. CIDR格式验证:");
        String[] testCidrs = {
            "192.168.1.0/24",
            "10.0.0.0/8", 
            "172.16.0.0/12",
            "0.0.0.0/0",
            "255.255.255.255/32",
            "192.168.1.0",  // 无效
            "192.168.1.0/33" // 无效
        };
        
        for (String cidr : testCidrs) {
            System.out.println(cidr + " -> " + IpUtils.isValidCidr(cidr));
        }
        
        // 测试IP范围匹配
        System.out.println("\n2. IP范围匹配测试:");
        
        // 测试 /24 子网
        String cidr24 = "192.168.1.0/24";
        String[] testIps24 = {
            "192.168.1.1",
            "192.168.1.100", 
            "192.168.1.254",
            "192.168.2.1"  // 不在范围内
        };
        
        System.out.println("测试子网: " + cidr24);
        for (String ip : testIps24) {
            System.out.println("  " + ip + " -> " + IpUtils.isIpInCidrRange(ip, cidr24));
        }
        
        // 测试 /8 子网
        String cidr8 = "10.0.0.0/8";
        String[] testIps8 = {
            "10.1.2.3",
            "10.255.255.255",
            "11.1.2.3"  // 不在范围内
        };
        
        System.out.println("\n测试子网: " + cidr8);
        for (String ip : testIps8) {
            System.out.println("  " + ip + " -> " + IpUtils.isIpInCidrRange(ip, cidr8));
        }
        
        // 测试精确匹配
        String cidr32 = "192.168.1.1/32";
        System.out.println("\n测试精确匹配: " + cidr32);
        System.out.println("  192.168.1.1 -> " + IpUtils.isIpInCidrRange("192.168.1.1", cidr32));
        System.out.println("  192.168.1.2 -> " + IpUtils.isIpInCidrRange("192.168.1.2", cidr32));
        
        // 测试全匹配
        String cidr0 = "0.0.0.0/0";
        System.out.println("\n测试全匹配: " + cidr0);
        System.out.println("  192.168.1.1 -> " + IpUtils.isIpInCidrRange("192.168.1.1", cidr0));
        System.out.println("  10.1.2.3 -> " + IpUtils.isIpInCidrRange("10.1.2.3", cidr0));
        
        System.out.println("\n=== 演示完成 ===");
    }
} 