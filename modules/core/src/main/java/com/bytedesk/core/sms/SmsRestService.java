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
package com.bytedesk.core.sms;

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
public class SmsRestService extends BaseRestServiceWithExport<SmsEntity, SmsRequest, SmsResponse, SmsExcel> {

    private final SmsRepository smsRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<SmsEntity> createSpecification(SmsRequest request) {
        return SmsSpecification.search(request, authService);
    }

    @Override
    protected Page<SmsEntity> executePageQuery(Specification<SmsEntity> spec, Pageable pageable) {
        return smsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "sms", key = "#uid", unless="#result==null")
    @Override
    public Optional<SmsEntity> findByUid(String uid) {
        return smsRepository.findByUid(uid);
    }

    @Cacheable(value = "sms", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SmsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return smsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return smsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SmsResponse create(SmsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SmsEntity> sms = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (sms.isPresent()) {
                return convertToResponse(sms.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        SmsEntity entity = modelMapper.map(request, SmsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        SmsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create sms failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SmsResponse update(SmsRequest request) {
        Optional<SmsEntity> optional = smsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SmsEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            SmsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update sms failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Sms not found");
        }
    }

    @Override
    protected SmsEntity doSave(SmsEntity entity) {
        return smsRepository.save(entity);
    }

    @Override
    public SmsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SmsEntity entity) {
        try {
            Optional<SmsEntity> latest = smsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SmsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return smsRepository.save(latestEntity);
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
        Optional<SmsEntity> optional = smsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // smsRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Sms not found");
        }
    }

    @Override
    public void delete(SmsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SmsResponse convertToResponse(SmsEntity entity) {
        return modelMapper.map(entity, SmsResponse.class);
    }

    @Override
    public SmsExcel convertToExcel(SmsEntity entity) {
        return modelMapper.map(entity, SmsExcel.class);
    }
    
    public void initSmss(String orgUid) {
        // log.info("initThreadSms");
        // for (String sms : SmsInitData.getAllSmss()) {
        //     SmsRequest smsRequest = SmsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, sms))
        //             .name(sms)
        //             .order(0)
        //             .type(SmsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(smsRequest);
        // }
    }

    
    
}
