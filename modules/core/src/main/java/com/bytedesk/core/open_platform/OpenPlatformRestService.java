/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 15:19:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.open_platform;

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
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OpenPlatformRestService extends BaseRestServiceWithExport<OpenPlatformEntity, OpenPlatformRequest, OpenPlatformResponse, OpenPlatformExcel> {

    private final OpenPlatformRepository openPlatformRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<OpenPlatformEntity> createSpecification(OpenPlatformRequest request) {
        return OpenPlatformSpecification.search(request, authService);
    }

    @Override
    protected Page<OpenPlatformEntity> executePageQuery(Specification<OpenPlatformEntity> spec, Pageable pageable) {
        return openPlatformRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "openPlatform", key = "#uid", unless="#result==null")
    @Override
    public Optional<OpenPlatformEntity> findByUid(String uid) {
        return openPlatformRepository.findByUid(uid);
    }

    @Cacheable(value = "openPlatform", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<OpenPlatformEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return openPlatformRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return openPlatformRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public OpenPlatformResponse create(OpenPlatformRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<OpenPlatformEntity> openPlatform = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (openPlatform.isPresent()) {
                return convertToResponse(openPlatform.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        OpenPlatformEntity entity = modelMapper.map(request, OpenPlatformEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OpenPlatformEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create openPlatform failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public OpenPlatformResponse update(OpenPlatformRequest request) {
        Optional<OpenPlatformEntity> optional = openPlatformRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OpenPlatformEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            OpenPlatformEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update openPlatform failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("OpenPlatform not found");
        }
    }

    @Override
    protected OpenPlatformEntity doSave(OpenPlatformEntity entity) {
        return openPlatformRepository.save(entity);
    }

    @Override
    public OpenPlatformEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OpenPlatformEntity entity) {
        try {
            Optional<OpenPlatformEntity> latest = openPlatformRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OpenPlatformEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return openPlatformRepository.save(latestEntity);
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
        Optional<OpenPlatformEntity> optional = openPlatformRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // openPlatformRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("OpenPlatform not found");
        }
    }

    @Override
    public void delete(OpenPlatformRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OpenPlatformResponse convertToResponse(OpenPlatformEntity entity) {
        return modelMapper.map(entity, OpenPlatformResponse.class);
    }

    @Override
    public OpenPlatformExcel convertToExcel(OpenPlatformEntity entity) {
        return modelMapper.map(entity, OpenPlatformExcel.class);
    }
    
    public void initOpenPlatforms(String orgUid) {
        // log.info("initThreadOpenPlatform");
        // for (String openPlatform : OpenPlatformInitData.getAllOpenPlatforms()) {
        //     OpenPlatformRequest openPlatformRequest = OpenPlatformRequest.builder()
        //             .uid(Utils.formatUid(orgUid, openPlatform))
        //             .name(openPlatform)
        //             .order(0)
        //             .type(OpenPlatformTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(openPlatformRequest);
        // }
    }

    
    
}
