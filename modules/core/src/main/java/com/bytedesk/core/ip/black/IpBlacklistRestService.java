/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:19:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:13:37
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpBlacklistRestService extends BaseRestServiceWithExcel<IpBlacklistEntity, IpBlacklistRequest, IpBlacklistResponse, IpBlacklistExcel> {

    private final IpBlacklistRepository ipBlacklistRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private static final int BLOCK_HOURS = 24;

    private final AuthService authService;

    @Override
    public Page<IpBlacklistEntity> queryByOrgEntity(IpBlacklistRequest request) {
        Pageable pageable = request.getPageable();
        Specification<IpBlacklistEntity> spec = IpBlacklistSpecification.search(request);
        return ipBlacklistRepository.findAll(spec, pageable);
    }

    @Override
    public Page<IpBlacklistResponse> queryByOrg(IpBlacklistRequest request) {
        Page<IpBlacklistEntity> ipBlacklistPage = queryByOrgEntity(request);
        return ipBlacklistPage.map(this::convertToResponse);
    }

    @Override
    public Page<IpBlacklistResponse> queryByUser(IpBlacklistRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "ipBlacklist", key = "#uid", unless = "#result == null")
    @Override
    public Optional<IpBlacklistEntity> findByUid(String uid) {
        return ipBlacklistRepository.findByUid(uid);
    }

    @Cacheable(value = "ipBlacklist", key = "#ip", unless = "#result == null")
    public Optional<IpBlacklistEntity> findByIp(String ip) {
        return ipBlacklistRepository.findByIp(ip);
    }

    public List<IpBlacklistEntity> findByEndTimeBefore(LocalDateTime dateTime) {
        return ipBlacklistRepository.findByEndTimeBefore(dateTime);
    }

    // api高频调用，自动添加系统黑名单
    public void addToBlacklistSystem(String ip) {
        //
        String ipLocation = ipService.getIpLocation(ip);
        LocalDateTime endTime = LocalDateTime.now().plusHours(BLOCK_HOURS);
        // 
        IpBlacklistRequest request = IpBlacklistRequest.builder()
                .ip(ip)
                .ipLocation(ipLocation)
                .endTime(endTime)
                .reason("Exceeded maximum request rate")
                .userUid("system")
                .build();
        create(request);
    }

    // 用户拉黑，同时拉黑ip
    public void addToBlacklist(String ip, 
        String ipLocation, 
        LocalDateTime endTime, 
        String reason, 
        String blackUid, 
        String blackNickname,
        String userUid, 
        String userNickname,
        String orgUid) {
        IpBlacklistRequest request = IpBlacklistRequest.builder()
                .ip(ip)
                .ipLocation(ipLocation)
                .endTime(endTime)
                .reason(reason)
                .blackUid(blackUid)
                .blackNickname(blackNickname)
                .userUid(userUid)
                .userNickname(userNickname)
                .build();
        request.setOrgUid(orgUid);
        create(request);
    }

    @Override
    public IpBlacklistResponse create(IpBlacklistRequest request) {
        // 判断ip是否在黑名单中
        Optional<IpBlacklistEntity> ipBlacklist = findByIp(request.getIp());
        if (ipBlacklist.isPresent()) {
            return convertToResponse(ipBlacklist.get());
        }
        //
        IpBlacklistEntity entity = modelMapper.map(request, IpBlacklistEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        IpBlacklistEntity savedBlacklist = save(entity);
        if (savedBlacklist == null) {
            throw new RuntimeException("Create ip blacklist failed");
        }
        return convertToResponse(savedBlacklist);
    }

    @Override
    public IpBlacklistResponse update(IpBlacklistRequest request) {
        if (request.getUid() == null) {
            throw new RuntimeException("Ip blacklist uid is required");
        }
        //
        Optional<IpBlacklistEntity> ipBlacklist = findByUid(request.getUid());
        if (ipBlacklist.isPresent()) {
            IpBlacklistEntity entity = ipBlacklist.get();
            modelMapper.map(request, entity);
            // 
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("Ip blacklist not found");
        }
    }

    @Override
    public IpBlacklistEntity save(IpBlacklistEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected IpBlacklistEntity doSave(IpBlacklistEntity entity) {
        return ipBlacklistRepository.save(entity);
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
    public IpBlacklistEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpBlacklistEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpBlacklistResponse convertToResponse(IpBlacklistEntity entity) {
        return modelMapper.map(entity, IpBlacklistResponse.class);
    }

    @Override
    public IpBlacklistResponse queryByUid(IpBlacklistRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public IpBlacklistExcel convertToExcel(IpBlacklistEntity entity) {
        return modelMapper.map(entity, IpBlacklistExcel.class);
    }

}
