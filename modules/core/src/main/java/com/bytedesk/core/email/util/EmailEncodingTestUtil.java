/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-01 16:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-01 16:00:00
 * @Description: 邮件编码测试工具类
 */
package com.bytedesk.core.email.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮件编码测试工具类
 * 用于测试和验证邮件编码解码功能
 */
@Slf4j
public class EmailEncodingTestUtil {

    /**
     * 测试MIME编码解码功能
     */
    public static void testMimeDecoding() {
        log.info("=== 开始测试MIME编码解码功能 ===");
        
        // 测试Base64编码的UTF-8字符串
        String[] testCases = {
            "=?UTF-8?B?6Zi/6YeM6YKu566x?=",  // "微语AI"的Base64编码
            "=?utf-8?B?5b6u5L+h5Zui6Zif?=",  // "微信公众平台"的Base64编码
            "=?UTF-8?B?d2VpeXU=?=",          // "weiyu"的Base64编码
            "=?UTF-8?Q?Test_Subject?=",      // Quoted-printable编码
            "=?UTF-8?B?5L2g5aW9?=",          // "你好"的Base64编码
            "=?UTF-8?B?6K+36L6T5YWl?=",      // "欢迎"的Base64编码
            "=?UTF-8?B?YnVpbGRlci5jb20=?=",  // "builder.com"的Base64编码
            "=?UTF-8?B?5L2g5aW95L2g5aW9?=",  // "你好你好"的Base64编码
        };
        
        for (String testCase : testCases) {
            String decoded = EmailEncodingUtil.decodeMimeString(testCase);
            log.info("编码: {} -> 解码: {}", testCase, decoded);
        }
        
        // 测试复合地址格式
        String[] addressTestCases = {
            "=?UTF-8?B?6Zi/6YeM6YKu566x?= <no-reply@mailsupport.aliyun.com>",
            "=?utf-8?B?5b6u5L+h5Zui6Zif?= <weixinmphelper@tencent.com>",
            "=?UTF-8?B?d2VpeXU=?= <weiyu@bytedesk.com>",
            "Test User <test@example.com>",
            "=?UTF-8?B?5L2g5aW9?= <hello@world.com>",
        };
        
        log.info("=== 测试地址解析 ===");
        for (String testCase : addressTestCases) {
            String email = EmailEncodingUtil.extractEmailFromString(testCase);
            String name = EmailEncodingUtil.extractNameFromString(testCase);
            log.info("地址: {} -> 邮箱: {}, 姓名: {}", testCase, email, name);
        }
        
        // 测试gb18030编码（新发现的问题）
        String[] gb18030TestCases = {
            "=?gb18030?B?d2VpeXU=?=",        // "weiyu"的gb18030编码
            "=?GB18030?B?d2VpeXU=?=",        // 大写GB18030
            "=?gb18030?B?d2VpeXU=?= <weiyu@bytedesk.com>",
            "=?gb18030?Q?weiyu?= <weiyu@bytedesk.com>",
        };
        
        log.info("=== 测试GB18030编码解码 ===");
        for (String testCase : gb18030TestCases) {
            String decoded = EmailEncodingUtil.decodeMimeString(testCase);
            log.info("GB18030编码: {} -> 解码: {}", testCase, decoded);
        }
        
        log.info("=== MIME编码解码测试完成 ===");
    }
    
    /**
     * 手动解码Base64编码的UTF-8字符串（用于验证）
     */
    public static String manualDecodeBase64(String encoded) {
        try {
            // 移除MIME编码格式 =?UTF-8?B?...?=
            String base64Part = encoded.replaceAll("=\\?UTF-8\\?B\\?(.+?)\\?=", "$1");
            base64Part = base64Part.replaceAll("=\\?utf-8\\?B\\?(.+?)\\?=", "$1");
            
            // Base64解码
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Part);
            
            // 转换为UTF-8字符串
            return new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            log.error("手动解码失败: {}", e.getMessage());
            return encoded;
        }
    }
    
    /**
     * 验证解码结果
     */
    public static void verifyDecoding() {
        log.info("=== 验证解码结果 ===");
        
        String[] testCases = {
            "=?UTF-8?B?6Zi/6YeM6YKu566x?=",  // 应该解码为 "微语AI"
            "=?utf-8?B?5b6u5L+h5Zui6Zif?=",  // 应该解码为 "微信公众平台"
            "=?UTF-8?B?d2VpeXU=?=",          // 应该解码为 "weiyu"
        };
        
        for (String testCase : testCases) {
            String autoDecoded = EmailEncodingUtil.decodeMimeString(testCase);
            String manualDecoded = manualDecodeBase64(testCase);
            
            log.info("原始: {}", testCase);
            log.info("自动解码: {}", autoDecoded);
            log.info("手动解码: {}", manualDecoded);
            log.info("结果一致: {}", autoDecoded.equals(manualDecoded));
            log.info("---");
        }
    }
    
    /**
     * 主方法，用于独立测试
     */
    public static void main(String[] args) {
        testMimeDecoding();
        verifyDecoding();
    }
} 