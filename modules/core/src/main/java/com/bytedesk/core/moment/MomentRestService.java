/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:41:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.moment;

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
public class MomentRestService extends BaseRestServiceWithExport<MomentEntity, MomentRequest, MomentResponse, MomentExcel> {

    private final MomentRepository momentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<MomentEntity> queryByOrgEntity(MomentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MomentEntity> spec = MomentSpecification.search(request, authService);
        return momentRepository.findAll(spec, pageable);
    }

    @Override
    public Page<MomentResponse> queryByOrg(MomentRequest request) {
        Page<MomentEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MomentResponse> queryByUser(MomentRequest request) {
        UserEntity user = authService.getUser();
        
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public MomentResponse queryByUid(MomentRequest request) {
        Optional<MomentEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            MomentEntity entity = optional.get();
            return convertToResponse(entity);
        } else {
            throw new RuntimeException("Moment not found");
        }
    }

    @Cacheable(value = "moment", key = "#uid", unless="#result==null")
    @Override
    public Optional<MomentEntity> findByUid(String uid) {
        return momentRepository.findByUid(uid);
    }

    @Cacheable(value = "moment", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<MomentEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return momentRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return momentRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public MomentResponse create(MomentRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<MomentEntity> moment = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (moment.isPresent()) {
                return convertToResponse(moment.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MomentEntity entity = modelMapper.map(request, MomentEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MomentEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create moment failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public MomentResponse update(MomentRequest request) {
        Optional<MomentEntity> optional = momentRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MomentEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MomentEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update moment failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Moment not found");
        }
    }

    @Override
    protected MomentEntity doSave(MomentEntity entity) {
        return momentRepository.save(entity);
    }

    @Override
    public MomentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MomentEntity entity) {
        try {
            Optional<MomentEntity> latest = momentRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MomentEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return momentRepository.save(latestEntity);
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
        Optional<MomentEntity> optional = momentRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // momentRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Moment not found");
        }
    }

    @Override
    public void delete(MomentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MomentResponse convertToResponse(MomentEntity entity) {
        return modelMapper.map(entity, MomentResponse.class);
    }

    @Override
    public MomentExcel convertToExcel(MomentEntity entity) {
        return modelMapper.map(entity, MomentExcel.class);
    }
    
    // public void initMoments(String orgUid) {
    //     // log.info("initThreadMoment");
    //     for (String moment : MomentInitData.getAllMoments()) {
    //         MomentRequest momentRequest = MomentRequest.builder()
    //                 .uid(Utils.formatUid(orgUid, moment))
    //                 .name(moment)
    //                 .order(0)
    //                 .type(MomentTypeEnum.THREAD.name())
    //                 .level(LevelEnum.ORGANIZATION.name())
    //                 .platform(BytedeskConsts.PLATFORM_BYTEDESK)
    //                 .orgUid(orgUid)
    //                 .build();
    //         create(momentRequest);
    //     }
    // }

    @Override
    protected Specification<MomentEntity> createSpecification(MomentRequest request) {
        return MomentSpecification.search(request, authService);
    }

    @Override
    protected Page<MomentEntity> executePageQuery(Specification<MomentEntity> spec, Pageable pageable) {
        return momentRepository.findAll(spec, pageable);
    }
    
}
