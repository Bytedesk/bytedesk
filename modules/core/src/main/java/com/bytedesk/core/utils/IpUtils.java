/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:17:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 21:56:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IpUtils {
    
    // 正则表达式用于匹配IPv4地址
    private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    // 正则表达式用于匹配IPv6地址
    private static final String IPV6_PATTERN = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}|([0-9a-f]{1,4}:){6}(:[0-9a-f]{1,4}){2}|([0-9a-f]{1,4}:){5}(:[0-9a-f]{1,4}){3}|([0-9a-f]{1,4}:){4}(:[0-9a-f]{1,4}){4}|([0-9a-f]{1,4}:){3}(:[0-9a-f]{1,4}){5}|([0-9a-f]{1,4}:){2}(:[0-9a-f]{1,4}){6}|([0-9a-f]{1,4}:)(:[0-9a-f]{1,4}){7}|(:(:[0-9a-f]{1,4}){7})|(::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";

    /**
     * 验证IP地址格式是否正确
     * 
     * @param ip 要验证的IP地址
     * @return 如果格式正确则返回true，否则返回false
     */
    public static boolean isValidIp(String ip) {
        // 验证IPv4地址
        if (Pattern.matches(IPV4_PATTERN, ip)) {
            return true;
        }
        // 验证IPv6地址
        if (Pattern.matches(IPV6_PATTERN, ip)) {
            return true;
        }
        return false;
    }

    /**
     * 获取客户端ip
     * 
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        return IpUtils.getClientIp(request);
    }

    
    /**
     * 获取访客来源ip
     * 
     * @return ip
     */
    @SuppressWarnings("null")
    public static String getClientIp(HttpServletRequest request) {

        String unknown = "unknown";
        String localhost = "127.0.0.1";
        String ipAddress = null;
        String ipv6 = "0:0:0:0:0:0:0:1";

        try {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || unknown.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals(localhost)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length() = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }

        return ipAddress.equals(ipv6) ? localhost : ipAddress;
    }

    // private String getClientIp(HttpServletRequest request) {
    //     String ip = request.getHeader("X-Forwarded-For");
    //     if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
    //         ip = request.getHeader("Proxy-Client-IP");
    //     }
    //     if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
    //         ip = request.getHeader("WL-Proxy-Client-IP");
    //     }
    //     if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
    //         ip = request.getRemoteAddr();
    //     }
    //     return ip;
    // }

    /**
     * 获取本机hostname
     * 
     * @return hostname
     */
    public static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "hostname";
    }

    /**
     * 获取服务器IP地址
     * 
     * @return
     */
    public static String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    // 将IPv4地址转换为长整型
    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        ByteBuffer buffer = ByteBuffer.wrap(octets);
        return buffer.getInt() & 0xFFFFFFFFL; // Ensure positive value
    }

    // 检查IP是否在指定的范围内
    public static boolean isIpInRange(InetAddress ip, InetAddress rangeStart, InetAddress rangeEnd) {
        long ipValue = ipToLong(ip);
        long rangeStartValue = ipToLong(rangeStart);
        long rangeEndValue = ipToLong(rangeEnd);

        return ipValue >= rangeStartValue && ipValue <= rangeEndValue;
    }

    public static boolean testIsIpInRange(String ip) {

        InetAddress ipToCheck;
        try {
            ipToCheck = InetAddress.getByName(ip);
            InetAddress rangeStart = InetAddress.getByName("192.168.1.1");
            InetAddress rangeEnd = InetAddress.getByName("192.168.1.254");

            if (isIpInRange(ipToCheck, rangeStart, rangeEnd)) {
                // log.info("The IP is within the specified range.");
                return true;
            } else {
                // log.info("The IP is not within the specified range.");
                return false;
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查IP是否匹配CIDR格式的IP段
     * 
     * @param ip 要检查的IP地址
     * @param cidr CIDR格式的IP段，如 "192.168.1.0/24"
     * @return 如果IP在CIDR范围内返回true，否则返回false
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        try {
            // 解析CIDR格式
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            // 验证IP地址格式
            if (!isValidIp(ip) || !isValidIp(networkAddress)) {
                return false;
            }
            
            // 获取网络地址和子网掩码
            InetAddress network = InetAddress.getByName(networkAddress);
            InetAddress ipToCheck = InetAddress.getByName(ip);
            
            // 计算子网掩码
            long mask = prefixLength == 0 ? 0 : 0xFFFFFFFFL << (32 - prefixLength);
            
            // 获取网络地址和IP地址的长整型值
            long networkLong = ipToLong(network);
            long ipLong = ipToLong(ipToCheck);
            
            // 应用子网掩码并比较
            return (networkLong & mask) == (ipLong & mask);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查IP是否匹配CIDR格式的IP段（支持IPv4和IPv6）
     * 
     * @param ip 要检查的IP地址
     * @param cidr CIDR格式的IP段
     * @return 如果IP在CIDR范围内返回true，否则返回false
     */
    public static boolean isIpInCidrRange(String ip, String cidr) {
        try {
            // 解析CIDR格式
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            // 验证IP地址格式
            if (!isValidIp(ip) || !isValidIp(networkAddress)) {
                return false;
            }
            
            // 获取网络地址和要检查的IP
            InetAddress network = InetAddress.getByName(networkAddress);
            InetAddress ipToCheck = InetAddress.getByName(ip);
            
            // 检查是否为IPv4
            if (network.getAddress().length == 4 && ipToCheck.getAddress().length == 4) {
                return isIpInCidr(ip, cidr);
            }
            
            // IPv6支持（简化版本）
            // 对于IPv6，我们使用更简单的方法：检查IP是否等于网络地址
            // 在实际应用中，可能需要更复杂的IPv6 CIDR计算
            return network.equals(ipToCheck);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证CIDR格式是否正确
     * 
     * @param cidr CIDR格式字符串，如 "192.168.1.0/24"
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidCidr(String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            String networkAddress = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            // 验证网络地址格式
            if (!isValidIp(networkAddress)) {
                return false;
            }
            
            // 验证前缀长度
            InetAddress network = InetAddress.getByName(networkAddress);
            int maxPrefixLength = network.getAddress().length * 8; // IPv4=32, IPv6=128
            
            return prefixLength >= 0 && prefixLength <= maxPrefixLength;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 测试CIDR功能的方法
     */
    public static void testCidrFunctionality() {
        // 测试IPv4 CIDR
        String cidr1 = "192.168.1.0/24";
        String testIp1 = "192.168.1.100";
        String testIp2 = "192.168.2.100";
        
        System.out.println("Testing CIDR functionality:");
        System.out.println("CIDR: " + cidr1);
        System.out.println("IP " + testIp1 + " in range: " + isIpInCidrRange(testIp1, cidr1));
        System.out.println("IP " + testIp2 + " in range: " + isIpInCidrRange(testIp2, cidr1));
        
        // 测试更小的子网
        String cidr2 = "10.0.0.0/8";
        String testIp3 = "10.1.2.3";
        System.out.println("CIDR: " + cidr2);
        System.out.println("IP " + testIp3 + " in range: " + isIpInCidrRange(testIp3, cidr2));
        
        // 测试精确匹配
        String cidr3 = "172.16.0.0/12";
        String testIp4 = "172.20.1.1";
        System.out.println("CIDR: " + cidr3);
        System.out.println("IP " + testIp4 + " in range: " + isIpInCidrRange(testIp4, cidr3));
    }

}
