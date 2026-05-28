/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.sms_push;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SmsPushRestService extends BaseRestServiceWithExport<SmsPushEntity, SmsPushRequest, SmsPushResponse, SmsPushExcel> {

    private final SmsPushRepository sms_pushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<SmsPushEntity> createSpecification(SmsPushRequest request) {
        return SmsPushSpecification.search(request, authService);
    }

    @Override
    protected Page<SmsPushEntity> executePageQuery(Specification<SmsPushEntity> spec, Pageable pageable) {
        return sms_pushRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "sms_push", key = "#uid", unless="#result==null")
    @Override
    public Optional<SmsPushEntity> findByUid(String uid) {
        return sms_pushRepository.findByUid(uid);
    }

    @Cacheable(value = "sms_push", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SmsPushEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return sms_pushRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return sms_pushRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SmsPushResponse create(SmsPushRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SmsPushEntity> sms_push = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (sms_push.isPresent()) {
                return convertToResponse(sms_push.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        SmsPushEntity entity = modelMapper.map(request, SmsPushEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        SmsPushEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SmsPushResponse update(SmsPushRequest request) {
        Optional<SmsPushEntity> optional = sms_pushRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SmsPushEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            SmsPushEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected SmsPushEntity doSave(SmsPushEntity entity) {
        return sms_pushRepository.save(entity);
    }

    @Override
    public SmsPushEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SmsPushEntity entity) {
        try {
            Optional<SmsPushEntity> latest = sms_pushRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SmsPushEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return sms_pushRepository.save(latestEntity);
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
        Optional<SmsPushEntity> optional = sms_pushRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // sms_pushRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(SmsPushRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SmsPushResponse convertToResponse(SmsPushEntity entity) {
        return modelMapper.map(entity, SmsPushResponse.class);
    }

    @Override
    public SmsPushExcel convertToExcel(SmsPushEntity entity) {
        return modelMapper.map(entity, SmsPushExcel.class);
    }
    
    public void initSmsPushs(String orgUid) {
        // log.info("initThreadSmsPush");
        // for (String sms_push : SmsPushInitData.getAllSmsPushs()) {
        //     SmsPushRequest sms_pushRequest = SmsPushRequest.builder()
        //             .uid(Utils.formatUid(orgUid, sms_push))
        //             .name(sms_push)
        //             .order(0)
        //             .type(SmsPushTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(sms_pushRequest);
        // }
    }

    
    
}
