/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-02 11:05:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.host;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class HostRestService extends BaseRestServiceWithExcel<HostEntity, HostRequest, HostResponse, HostExcel> {

    private final HostRepository hostRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<HostEntity> queryByOrgEntity(HostRequest request) {
        Pageable pageable = request.getPageable();
        Specification<HostEntity> spec = HostSpecification.search(request);
        return hostRepository.findAll(spec, pageable);
    }

    @Override
    public Page<HostResponse> queryByOrg(HostRequest request) {
        Page<HostEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<HostResponse> queryByUser(HostRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public HostResponse queryByUid(HostRequest request) {
        Optional<HostEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            HostEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Host not found");
        }
    }

    @Cacheable(value = "host", key = "#uid", unless="#result==null")
    @Override
    public Optional<HostEntity> findByUid(String uid) {
        return hostRepository.findByUid(uid);
    }

    @Cacheable(value = "host", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<HostEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return hostRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return hostRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public HostResponse create(HostRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<HostEntity> host = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (host.isPresent()) {
                return convertToResponse(host.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        HostEntity entity = modelMapper.map(request, HostEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        HostEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create host failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public HostResponse update(HostRequest request) {
        Optional<HostEntity> optional = hostRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            HostEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            HostEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update host failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Host not found");
        }
    }

    @Override
    protected HostEntity doSave(HostEntity entity) {
        return hostRepository.save(entity);
    }

    @Override
    public HostEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, HostEntity entity) {
        try {
            Optional<HostEntity> latest = hostRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                HostEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return hostRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<HostEntity> optional = hostRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // hostRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Host not found");
        }
    }

    @Override
    public void delete(HostRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public HostResponse convertToResponse(HostEntity entity) {
        return modelMapper.map(entity, HostResponse.class);
    }

    @Override
    public HostExcel convertToExcel(HostEntity entity) {
        return modelMapper.map(entity, HostExcel.class);
    }
    
    public void initHosts(String orgUid) {
        // log.info("initThreadHost");
        for (String host : HostInitData.getAllHosts()) {
            HostRequest hostRequest = HostRequest.builder()
                    .uid(Utils.formatUid(orgUid, host))
                    .name(host)
                    .order(0)
                    .type(HostTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(hostRequest);
        }
    }
    
}
