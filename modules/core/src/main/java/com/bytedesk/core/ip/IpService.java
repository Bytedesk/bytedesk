/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-16 13:28:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 22:11:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.ip.ip2region.Ip2RegionLocationProvider;
import com.bytedesk.core.uid.UidUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 */
@Slf4j
@Service
public class IpService {

    private final Map<String, IpLocationProvider> providers;

    private final IpProperties ipProperties;

    private final UidUtils uidUtils;

    public IpService(List<IpLocationProvider> providers, IpProperties ipProperties, UidUtils uidUtils) {
        this.ipProperties = ipProperties;
        this.uidUtils = uidUtils;
        this.providers = new LinkedHashMap<>();
        for (IpLocationProvider provider : providers) {
            this.providers.put(provider.getName().toLowerCase(Locale.ROOT), provider);
        }
    }

    /**
     * location: "国家|区域|省份|城市|ISP"
     * location: "中国|0|湖北省|武汉市|联通"
     * 
     * @param ip
     * @return
     */
    public String getIpLocation(String ip) {
        return getIpLocationDetail(ip).getLocation();
    }

    public String getIpLocation(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        return getIpLocation(ip);
    }

    public IpLocationResult getIpLocationDetail(String ip) {
        return getIpLocationDetail(ip, null);
    }

    public IpLocationResult getIpLocationDetail(String ip, String providerName) {
        String resolvedProviderName = resolveProviderName(providerName);
        if (!IpUtils.isValidIp(ip)) {
            log.error("Invalid IP address format: {}", ip);
            return IpLocationResult.local(resolvedProviderName, ip);
        }

        IpLocationResult result = locateByProvider(ip, resolvedProviderName);
        if (result != null) {
            return result;
        }

        if (ipProperties.isFallbackEnabled()) {
            for (IpLocationProvider provider : providers.values()) {
                if (provider.getName().equalsIgnoreCase(resolvedProviderName)) {
                    continue;
                }
                result = safeLocate(provider, ip);
                if (result != null) {
                    return result;
                }
            }
        }

        return IpLocationResult.local(resolvedProviderName, ip);
    }

    public String getConfiguredProvider() {
        return resolveProviderName(null);
    }

    private IpLocationResult locateByProvider(String ip, String providerName) {
        IpLocationProvider provider = providers.get(providerName.toLowerCase(Locale.ROOT));
        if (provider == null) {
            log.warn("Unknown ip provider: {}", providerName);
            return null;
        }
        return safeLocate(provider, ip);
    }

    private IpLocationResult safeLocate(IpLocationProvider provider, String ip) {
        if (provider == null || !provider.isAvailable()) {
            return null;
        }
        return provider.locate(ip);
    }

    private String resolveProviderName(String providerName) {
        String configuredProvider = StringUtils.hasText(providerName) ? providerName : ipProperties.getProvider();
        return StringUtils.hasText(configuredProvider) ? configuredProvider.trim().toLowerCase(Locale.ROOT)
                : Ip2RegionLocationProvider.PROVIDER_NAME;
    }

    // TODO: cache区分org
    @Cacheable(value = "ip", key = "#ip+ '-' + #orgUid")
    public Boolean isBlocked(String ip, String orgUid) {
        // TODO: 暂时不验证
        return false;
    }

    // TODO: 昵称国际化：英语、中文、繁体、日文
    public String createVisitorNickname(HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        String location = getIpLocation(ip);
        // uidUtils.getUid(); // TODO: 修改昵称后缀数字为从1~递增
        String randomId = uidUtils.getUid(); //"[" + ip + "]"; 

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String[] locals = location.split("\\|");
        log.info("ip {} location {} locals {}", ip, location, (Object[]) locals); // Cast to Object[] to confirm the
        // non-varargs invocation
        if (locals.length > 2) {
            if (locals[2].equals("0")) {
                return "Local" + randomId;
            }
            return locals[2] + randomId;
        }

        return "Visitor";
    }

}
