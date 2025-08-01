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
package com.bytedesk.core.ip_white;

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
public class IpWhiteRestService extends BaseRestService<IpWhiteEntity, IpWhiteRequest, IpWhiteResponse> {

    private final IpWhiteRepository ipWhiteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<IpWhiteResponse> queryByOrg(IpWhiteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<IpWhiteEntity> spec = IpWhiteSpecification.search(request);
        Page<IpWhiteEntity> ipWhitePage = ipWhiteRepository.findAll(spec, pageable);
        return ipWhitePage.map(this::convertToResponse);
    }

    @Cacheable(value = "ipWhite", key = "#ip", unless = "#result == null")
    public Optional<IpWhiteEntity> findByIp(String ip) {
        return ipWhiteRepository.findByIpAndDeletedFalse(ip);
    }

    @Override
    public Page<IpWhiteResponse> queryByUser(IpWhiteRequest request) {
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
    public Optional<IpWhiteEntity> findByUid(String uid) {
        return ipWhiteRepository.findByUid(uid);
    }

    @Override
    public IpWhiteResponse create(IpWhiteRequest request) {
        Optional<IpWhiteEntity> ipWhite = findByIp(request.getIp());
        if (ipWhite.isPresent()) {
            throw new RuntimeException("ipWhite already exists");
        }
        IpWhiteEntity ipWhiteEntity = modelMapper.map(request, IpWhiteEntity.class);
        ipWhiteEntity.setUid(uidUtils.getUid());
        // 保存
        IpWhiteEntity savedIpWhiteEntity = ipWhiteRepository.save(ipWhiteEntity);
        if (savedIpWhiteEntity == null) {
            throw new RuntimeException("ipWhite save failed");
        }
        return convertToResponse(savedIpWhiteEntity);
    }

    @Override
    public IpWhiteResponse update(IpWhiteRequest request) {
        Optional<IpWhiteEntity> ipWhite = findByUid(request.getUid());
        if (!ipWhite.isPresent()) {
            throw new RuntimeException("ipWhite is not present");
        }
        IpWhiteEntity ipWhiteEntity = modelMapper.map(request, IpWhiteEntity.class);
        IpWhiteEntity savedIpWhiteEntity = ipWhiteRepository.save(ipWhiteEntity);
        if (savedIpWhiteEntity == null) {
            throw new RuntimeException("ipWhite save failed");
        }
        return convertToResponse(savedIpWhiteEntity);
    }

    public long count() { 
        return ipWhiteRepository.count();
    }

    @Override
    protected IpWhiteEntity doSave(IpWhiteEntity entity) {
        return ipWhiteRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<IpWhiteEntity> ipWhite = findByUid(uid);
        if (ipWhite.isPresent()) {
            ipWhiteRepository.delete(ipWhite.get());
        }
    }

    @Override
    public void delete(IpWhiteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public IpWhiteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            IpWhiteEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public IpWhiteResponse convertToResponse(IpWhiteEntity entity) {
        return modelMapper.map(entity, IpWhiteResponse.class);
    }

    @Override
    public IpWhiteResponse queryByUid(IpWhiteRequest request) {
        Optional<IpWhiteEntity> ipWhite = findByUid(request.getUid());
        if (ipWhite.isPresent()) {
            return convertToResponse(ipWhite.get());
        } else {
            throw new RuntimeException("Ip whitelist not found");
        }
    }
    
}
