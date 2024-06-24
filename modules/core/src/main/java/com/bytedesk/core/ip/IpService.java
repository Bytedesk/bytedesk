/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 13:28:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-25 06:46:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.util.regex.Pattern;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 */
@Slf4j
@Service
@AllArgsConstructor
public class IpService {

    private final Searcher searcher;

    private final UidUtils uidUtils;

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
    public String getIp(HttpServletRequest request) {
        return IpUtils.clientIp(request);
    }

    /**
     * location: "国家|区域|省份|城市|ISP"
     * location: "中国|0|湖北省|武汉市|联通"
     * 
     * @param ip
     * @return
     */
    public String getIpLocation(String ip) {
        // java.lang.Exception: invalid ip address `[0:0:0:0:0:0:0:1]` // replace localhost with 127.0.0.1
        // 首先验证IP格式是否正确
        if (!isValidIp(ip)) {
            log.error("Invalid IP address format: " + ip);
            return "0|0|0|内网IP|内网IP";
        }
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("failed to search(%s): %s\n", ip, e);
        }
        return null;
    }

    public String getIpLocation(HttpServletRequest request) {
        String ip = getIp(request);
        return getIpLocation(ip);
    }

    // TODO: 昵称国际化：英语、中文、繁体、日文
    public String createVisitorNickname(HttpServletRequest request) {

        String location = getIpLocation(request);
        // TODO: 修改昵称后缀数字为从1~递增
        String randomId = uidUtils.getCacheSerialUid();

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String[] locals = location.split("\\|");
        // log.info("locals {}", (Object[]) locals); // Cast to Object[] to confirm the non-varargs invocation
        if (locals.length > 2) {
            if (locals[2].equals("0")) {
                return "Local" + randomId;
            }
            return locals[2] + randomId;
        }

        return "Visitor";
    }

}
