/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 22:18:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 11:45:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.white;

import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpWhitelistRestService extends BaseRestService<IpWhitelistEntity, IpWhitelistRequest, IpWhitelistResponse> {

    private final IpWhitelistRepository ipWhitelistRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<IpWhitelistResponse> queryByOrg(IpWhitelistRequest request) {
        Pageable pageable = request.getPageable();
        Specification<IpWhitelistEntity> spec = IpWhitelistSpecification.search(request);
        Page<IpWhitelistEntity> ipWhitelistPage = ipWhitelistRepository.findAll(spec, pageable);
        return ipWhitelistPage.map(this::convertToResponse);
    }

    @Cacheable(value = "ipWhitelist", key = "#ip", unless = "#result == null")
    public Optional<IpWhitelistEntity> findByIp(String ip) {
        return ipWhitelistRepository.findByIpAndDeletedFalse(ip);
    }

    @Override
    public Page<IpWhitelistResponse> queryByUser(IpWhitelistRequest request) {
        // 获取当前用户
        UserEntity currentUser = authService.getUser();
        if (currentUser == null) {
            throw new RuntimeException("user is null");
        }
        request.setUserUid(currentUser.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public Optional<IpWhitelistEntity> findByUid(String uid) {
        return ipWhitelistRepository.findByUid(uid);
    }

    @Override
    public IpWhitelistResponse create(IpWhitelistRequest request) {
        Optional<IpWhitelistEntity> ipWhitelist = findByIp(request.getIp());
        if (ipWhitelist.isPresent()) {
            throw new RuntimeException("ipWhitelist already exists");
        }
        IpWhitelistEntity ipWhitelistEntity = modelMapper.map(request, IpWhitelistEntity.class);
        ipWhitelistEntity.setUid(uidUtils.getUid());
        // 保存
        IpWhitelistEntity savedIpWhitelistEntity = ipWhitelistRepository.save(ipWhitelistEntity);
        if (savedIpWhitelistEntity == null) {
            throw new RuntimeException("ipWhitelist save failed");
        }
        return convertToResponse(savedIpWhitelistEntity);
    }

    @Override
    public IpWhitelistResponse update(IpWhitelistRequest request) {
        Optional<IpWhitelistEntity> ipWhitelist = findByUid(request.getUid());
        if (!ipWhitelist.isPresent()) {
            throw new RuntimeException("ipWhitelist is not present");
        }
        IpWhitelistEntity ipWhitelistEntity = modelMapper.map(request, IpWhitelistEntity.class);
        IpWhitelistEntity savedIpWhitelistEntity = ipWhitelistRepository.save(ipWhitelistEntity);
        if (savedIpWhitelistEntity == null) {
            throw new RuntimeException("ipWhitelist save failed");
        }
        return convertToResponse(savedIpWhitelistEntity);
    }

    public long count() { 
        return ipWhitelistRepository.count();
    }

    @Override
    protected IpWhitelistEntity doSave(IpWhitelistEntity entity) {
        return ipWhitelistRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<IpWhitelistEntity> ipWhitelist = findByUid(uid);
        if (ipWhitelist.isPresent()) {
            ipWhitelistRepository.delete(ipWhitelist.get());
        }
    }

    @Override
    public void delete(IpWhitelistRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public IpWhitelistEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpWhitelistEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpWhitelistResponse convertToResponse(IpWhitelistEntity entity) {
        return modelMapper.map(entity, IpWhitelistResponse.class);
    }

    @Override
    public IpWhitelistResponse queryByUid(IpWhitelistRequest request) {
        Optional<IpWhitelistEntity> ipWhitelist = findByUid(request.getUid());
        if (ipWhitelist.isPresent()) {
            return convertToResponse(ipWhitelist.get());
        } else {
            throw new RuntimeException("Ip whitelist not found");
        }
    }
    
}
