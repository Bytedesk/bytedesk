/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 15:13:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.opinion;

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
public class OpinionRestService extends BaseRestServiceWithExport<OpinionEntity, OpinionRequest, OpinionResponse, OpinionExcel> {

    private final OpinionRepository opinionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<OpinionEntity> createSpecification(OpinionRequest request) {
        return OpinionSpecification.search(request, authService);
    }

    @Override
    protected Page<OpinionEntity> executePageQuery(Specification<OpinionEntity> spec, Pageable pageable) {
        return opinionRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "opinion", key = "#uid", unless="#result==null")
    @Override
    public Optional<OpinionEntity> findByUid(String uid) {
        return opinionRepository.findByUid(uid);
    }

    @Cacheable(value = "opinion", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<OpinionEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return opinionRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return opinionRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public OpinionResponse create(OpinionRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<OpinionEntity> opinion = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (opinion.isPresent()) {
                return convertToResponse(opinion.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        OpinionEntity entity = modelMapper.map(request, OpinionEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        OpinionEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create opinion failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public OpinionResponse update(OpinionRequest request) {
        Optional<OpinionEntity> optional = opinionRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            OpinionEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            OpinionEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update opinion failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Opinion not found");
        }
    }

    @Override
    protected OpinionEntity doSave(OpinionEntity entity) {
        return opinionRepository.save(entity);
    }

    @Override
    public OpinionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OpinionEntity entity) {
        try {
            Optional<OpinionEntity> latest = opinionRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                OpinionEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return opinionRepository.save(latestEntity);
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
        Optional<OpinionEntity> optional = opinionRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // opinionRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Opinion not found");
        }
    }

    @Override
    public void delete(OpinionRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public OpinionResponse convertToResponse(OpinionEntity entity) {
        return modelMapper.map(entity, OpinionResponse.class);
    }

    @Override
    public OpinionExcel convertToExcel(OpinionEntity entity) {
        return modelMapper.map(entity, OpinionExcel.class);
    }
    
    public void initOpinions(String orgUid) {
        // log.info("initThreadOpinion");
        // for (String opinion : OpinionInitData.getAllOpinions()) {
        //     OpinionRequest opinionRequest = OpinionRequest.builder()
        //             .uid(Utils.formatUid(orgUid, opinion))
        //             .name(opinion)
        //             .order(0)
        //             .type(OpinionTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(opinionRequest);
        // }
    }

    
    
}
