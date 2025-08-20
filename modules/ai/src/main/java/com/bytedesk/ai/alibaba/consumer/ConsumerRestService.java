/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:40:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.consumer;

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
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ConsumerRestService extends BaseRestServiceWithExcel<ConsumerEntity, ConsumerRequest, ConsumerResponse, ConsumerExcel> {

    private final ConsumerRepository consumerRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<ConsumerEntity> createSpecification(ConsumerRequest request) {
        return ConsumerSpecification.search(request);
    }

    @Override
    protected Page<ConsumerEntity> executePageQuery(Specification<ConsumerEntity> spec, Pageable pageable) {
        return consumerRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "consumer", key = "#uid", unless="#result==null")
    @Override
    public Optional<ConsumerEntity> findByUid(String uid) {
        return consumerRepository.findByUid(uid);
    }

    @Cacheable(value = "consumer", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ConsumerEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return consumerRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return consumerRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ConsumerResponse create(ConsumerRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ConsumerEntity> consumer = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (consumer.isPresent()) {
                return convertToResponse(consumer.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        ConsumerEntity entity = modelMapper.map(request, ConsumerEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ConsumerEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create consumer failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ConsumerResponse update(ConsumerRequest request) {
        Optional<ConsumerEntity> optional = consumerRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ConsumerEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ConsumerEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update consumer failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Consumer not found");
        }
    }

    @Override
    protected ConsumerEntity doSave(ConsumerEntity entity) {
        return consumerRepository.save(entity);
    }

    @Override
    public ConsumerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ConsumerEntity entity) {
        try {
            Optional<ConsumerEntity> latest = consumerRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ConsumerEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return consumerRepository.save(latestEntity);
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
        Optional<ConsumerEntity> optional = consumerRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // consumerRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Consumer not found");
        }
    }

    @Override
    public void delete(ConsumerRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ConsumerResponse convertToResponse(ConsumerEntity entity) {
        return modelMapper.map(entity, ConsumerResponse.class);
    }

    @Override
    public ConsumerExcel convertToExcel(ConsumerEntity entity) {
        return modelMapper.map(entity, ConsumerExcel.class);
    }
    
    public void initConsumers(String orgUid) {
        // log.info("initThreadConsumer");
        // for (String consumer : ConsumerInitData.getAllConsumers()) {
        //     ConsumerRequest consumerRequest = ConsumerRequest.builder()
        //             .uid(Utils.formatUid(orgUid, consumer))
        //             .name(consumer)
        //             .order(0)
        //             .type(ConsumerTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(consumerRequest);
        // }
    }
    
}
