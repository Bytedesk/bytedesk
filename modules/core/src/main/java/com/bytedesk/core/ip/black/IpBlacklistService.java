/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:19:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 14:36:18
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
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpBlacklistService extends BaseRestService<IpBlacklistEntity, IpBlacklistRequest, IpBlacklistResponse> {

    private final IpBlacklistRepository ipBlacklistRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Optional<IpBlacklistEntity> findByIp(String ip) {
        return ipBlacklistRepository.findByIp(ip);
    }

    public void addToBlacklist(String ip, String ipLocation, LocalDateTime endTime, String reason, String userUid) {
        IpBlacklistEntity blacklist = new IpBlacklistEntity();
        blacklist.setIp(ip);
        blacklist.setUid(uidUtils.getUid());
        blacklist.setStartTime(LocalDateTime.now());
        blacklist.setEndTime(endTime);
        blacklist.setReason(reason);
        blacklist.setIpLocation(ipLocation);
        blacklist.setUserUid(userUid);
        save(blacklist);
    }



    public List<IpBlacklistEntity> findByEndTimeBefore(LocalDateTime dateTime) {
        return ipBlacklistRepository.findByEndTimeBefore(dateTime);
    }

    @Override
    public Page<IpBlacklistResponse> queryByOrg(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<IpBlacklistResponse> queryByUser(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<IpBlacklistEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public IpBlacklistResponse create(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public IpBlacklistResponse update(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public IpBlacklistEntity save(IpBlacklistEntity entity) {
        try {
            return ipBlacklistRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Save ip blacklist failed");
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<IpBlacklistEntity> ipBlacklist = findByUid(uid);
        if (ipBlacklist.isPresent()) {
            ipBlacklistRepository.delete(ipBlacklist.get());
        }
    }

    @Override
    public void delete(IpBlacklistRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpBlacklistEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpBlacklistResponse convertToResponse(IpBlacklistEntity entity) {
        return modelMapper.map(entity, IpBlacklistResponse.class);
    }

    
}
