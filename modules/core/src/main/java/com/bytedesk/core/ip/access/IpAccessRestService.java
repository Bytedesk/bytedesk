/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 16:24:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.access;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.black.IpBlacklistEntity;
import com.bytedesk.core.ip.black.IpBlacklistRestService;
import com.bytedesk.core.ip.white.IpWhitelistRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.base.BaseRestService;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class IpAccessRestService extends BaseRestService<IpAccessEntity, IpAccessRequest, IpAccessResponse> {
    
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    
    private final IpAccessRepository ipAccessRepository;
        
    private final IpWhitelistRepository whitelistRepository;

    private final IpBlacklistRestService ipBlacklistService;

    private final IpService ipService;

    private final UidUtils uidUtils;
    
    public boolean isIpBlocked(String ip) {
        // 检查是否在白名单中
        if (whitelistRepository.existsByIp(ip)) {
            return false;
        }
        
        // 检查是否在黑名单中且未过期
        Optional<IpBlacklistEntity> blacklist = ipBlacklistService.findByIp(ip);
        if (blacklist.isPresent() && blacklist.get().getEndTime().isAfter(LocalDateTime.now())) {
            return true;
        }
        
        return false;
    }
    
    public void recordAccess(String ip, String endpoint, String params) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        
        // 获取最近一分钟的访问记录
        IpAccessEntity access = ipAccessRepository.findFirstByIpAndEndpointAndAccessTimeAfter(ip, endpoint, oneMinuteAgo);
        // 如果访问记录不存在，则创建新的访问记录
        if (access == null) {
            String ipLocation = ipService.getIpLocation(ip);
            // 
            access = new IpAccessEntity();
            access.setIp(ip);
            access.setIpLocation(ipLocation);
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
            // 
            ipBlacklistService.addToBlacklistSystem(ip);
        }
    }

    @Override
    public Page<IpAccessResponse> queryByOrg(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<IpAccessResponse> queryByUser(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<IpAccessEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public IpAccessResponse create(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public IpAccessResponse update(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public IpAccessEntity save(IpAccessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(IpAccessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpAccessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpAccessResponse convertToResponse(IpAccessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }
    

    
    
    
} 