/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 13:28:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-20 14:18:12
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
