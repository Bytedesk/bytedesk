/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 10:53:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.ip.black.IpBlacklistEntity;
import com.bytedesk.core.ip.black.IpBlacklistRepository;
import com.bytedesk.core.ip.black.IpBlacklistService;
import com.bytedesk.core.ip.white.IpWhitelistRepository;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Service
@Transactional
@AllArgsConstructor
public class IpAccessService {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    
    private final IpAccessRepository ipAccessRepository;
    
    private final IpBlacklistRepository blacklistRepository;
    
    private final IpWhitelistRepository whitelistRepository;

    private final IpBlacklistService ipBlacklistService;

    private final UidUtils uidUtils;
    
    public boolean isIpBlocked(String ip) {
        // 检查是否在白名单中
        if (whitelistRepository.existsByIp(ip)) {
            return false;
        }
        
        // 检查是否在黑名单中且未过期
        IpBlacklistEntity blacklist = blacklistRepository.findByIp(ip);
        if (blacklist != null && blacklist.getEndTime().isAfter(LocalDateTime.now())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 记录访问
     * @param ip 访问ip
     * @param endpoint 访问端点
     * @param params 访问参数
     */
    public void recordAccess(String ip, String endpoint, String params) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        
        // 获取最近一分钟的访问记录
        IpAccessEntity access = ipAccessRepository.findFirstByIpAndEndpointAndAccessTimeAfter(ip, endpoint, oneMinuteAgo);
        // 如果访问记录不存在，则创建新的访问记录
        if (access == null) {
            access = new IpAccessEntity();
            access.setIp(ip);
            access.setUid(uidUtils.getUid());
            access.setEndpoint(endpoint);
            access.setParams(params);
            access.setAccessTime(now);
            access.setAccessCount(1);
        } else {
            access.setAccessCount(access.getAccessCount() + 1);
        }
        access.setLastAccessTime(now);
        // 保存访问记录
        ipAccessRepository.save(access);
        // 检查是否需要加入黑名单
        if (access.getAccessCount() > MAX_REQUESTS_PER_MINUTE) {
            ipBlacklistService.addToBlacklist(ip);
        }
    }
    
    
} 