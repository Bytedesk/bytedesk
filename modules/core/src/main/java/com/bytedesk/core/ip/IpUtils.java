/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-05 14:17:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-15 16:26:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    private IpUtils() {
    }
    
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

}
