/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:19:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 12:16:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_black;

import java.time.ZonedDateTime;
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
import com.bytedesk.core.black.BlackEntity;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpBlackRestService extends BaseRestServiceWithExcel<IpBlackEntity, IpBlackRequest, IpBlackResponse, IpBlackExcel> {

    private final IpBlackRepository ipBlackRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private static final int BLOCK_HOURS = 24;

    private final AuthService authService;

    @Override
    public Page<IpBlackEntity> queryByOrgEntity(IpBlackRequest request) {
        Pageable pageable = request.getPageable();
        Specification<IpBlackEntity> spec = IpBlackSpecification.search(request);
        return ipBlackRepository.findAll(spec, pageable);
    }

    @Override
    public Page<IpBlackResponse> queryByOrg(IpBlackRequest request) {
        Page<IpBlackEntity> ipBlackPage = queryByOrgEntity(request);
        return ipBlackPage.map(this::convertToResponse);
    }

    @Override
    public Page<IpBlackResponse> queryByUser(IpBlackRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Override
    public IpBlackResponse queryByUid(IpBlackRequest request) {
        Optional<IpBlackEntity> ipBlack = findByUid(request.getUid());
        if (ipBlack.isPresent()) {
            return convertToResponse(ipBlack.get());
        } else {
            throw new RuntimeException("Ip blacklist not found");
        }
    }

    @Cacheable(value = "ipBlack", key = "#uid", unless = "#result == null")
    @Override
    public Optional<IpBlackEntity> findByUid(String uid) {
        return ipBlackRepository.findByUidAndDeletedFalse(uid);
    }

    @Cacheable(value = "ipBlack", key = "#ip", unless = "#result == null")
    public Optional<IpBlackEntity> findByIp(String ip) {
        return ipBlackRepository.findByIpAndDeletedFalse(ip);
    }

    public List<IpBlackEntity> findByEndTimeBefore(ZonedDateTime dateTime) {
        return ipBlackRepository.findByEndTimeBeforeAndDeletedFalse(dateTime);
    }

    // api高频调用，自动添加系统黑名单
    public void addToBlackSystem(String ip) {
        //
        String ipLocation = ipService.getIpLocation(ip);
        ZonedDateTime endTime = BdDateUtils.now().plusHours(BLOCK_HOURS);
        // 
        IpBlackRequest request = IpBlackRequest.builder()
                .ip(ip)
                .ipLocation(ipLocation)
                .endTime(endTime)
                .reason("Exceeded maximum request rate")
                .userUid("system")
                .build();
        create(request);
    }

    // 用户拉黑，同时拉黑ip
    public void addToBlack(String ip, String ipLocation, BlackEntity blackEntity) {
        IpBlackRequest request = IpBlackRequest.builder()
                .ip(ip)
                .ipLocation(ipLocation)
                .endTime(blackEntity.getEndTime())
                .reason(blackEntity.getReason())
                .blackUid(blackEntity.getBlackUid())
                .blackNickname(blackEntity.getBlackNickname())
                .userUid(blackEntity.getUserUid())
                .userNickname(blackEntity.getUserNickname())
                .orgUid(blackEntity.getOrgUid())
                .build();
        create(request);
    }

    public void deleteByIp(String ip) {
        Optional<IpBlackEntity> ipBlackOptional = findByIp(ip);
        if (ipBlackOptional.isPresent()) {
            IpBlackEntity ipBlack = ipBlackOptional.get();
            // ipBlackRepository.delete(ipBlack);
            ipBlack.setDeleted(true);
            save(ipBlack);
        }
    }
    
    @Override
    public IpBlackResponse create(IpBlackRequest request) {
        // 判断ip是否在黑名单中
        Optional<IpBlackEntity> ipBlack = findByIp(request.getIp());
        if (ipBlack.isPresent()) {
            return convertToResponse(ipBlack.get());
        }
        //
        IpBlackEntity entity = modelMapper.map(request, IpBlackEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        IpBlackEntity savedBlack = save(entity);
        if (savedBlack == null) {
            throw new RuntimeException("Create ip blacklist failed");
        }
        return convertToResponse(savedBlack);
    }

    @Override
    public IpBlackResponse update(IpBlackRequest request) {
        if (request.getUid() == null) {
            throw new RuntimeException("Ip blacklist uid is required");
        }
        //
        Optional<IpBlackEntity> ipBlack = findByUid(request.getUid());
        if (ipBlack.isPresent()) {
            IpBlackEntity entity = ipBlack.get();
            modelMapper.map(request, entity);
            // 
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("Ip blacklist not found");
        }
    }

    @Override
    protected IpBlackEntity doSave(IpBlackEntity entity) {
        return ipBlackRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<IpBlackEntity> ipBlackOptional = findByUid(uid);
        if (ipBlackOptional.isPresent()) {
            // ipBlackRepository.delete(ipBlack.get());
            IpBlackEntity ipBlack = ipBlackOptional.get();
            ipBlack.setDeleted(true);
            save(ipBlack);
        }
    }

    @Override
    public void delete(IpBlackRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public IpBlackEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpBlackEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpBlackResponse convertToResponse(IpBlackEntity entity) {
        return modelMapper.map(entity, IpBlackResponse.class);
    }

    @Override
    public IpBlackExcel convertToExcel(IpBlackEntity entity) {
        return modelMapper.map(entity, IpBlackExcel.class);
    }

}
