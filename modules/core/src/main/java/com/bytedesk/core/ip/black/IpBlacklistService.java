/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:19:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 13:52:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.black;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpBlacklistService {

    private final IpBlacklistRepository ipBlacklistRepository;

    private final UidUtils uidUtils;

    public Optional<IpBlacklistEntity> findByIp(String ip) {
        return ipBlacklistRepository.findByIp(ip);
    }

    public void addToBlacklist(String ip, String ipLocation, LocalDateTime endTime, String reason) {
        IpBlacklistEntity blacklist = new IpBlacklistEntity();
        blacklist.setIp(ip);
        blacklist.setUid(uidUtils.getUid());
        blacklist.setStartTime(LocalDateTime.now());
        blacklist.setEndTime(endTime);
        blacklist.setReason(reason);
        blacklist.setIpLocation(ipLocation);
        
        save(blacklist);
    }

    public void save(IpBlacklistEntity blacklist) {
        ipBlacklistRepository.save(blacklist);
    }
}
