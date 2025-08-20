/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:22:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.material;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MaterialRestService extends BaseRestServiceWithExport<MaterialEntity, MaterialRequest, MaterialResponse, MaterialExcel> {

    private final MaterialRepository materialRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<MaterialEntity> createSpecification(MaterialRequest request) {
        return MaterialSpecification.search(request, authService);
    }

    @Override
    protected Page<MaterialEntity> executePageQuery(Specification<MaterialEntity> spec, Pageable pageable) {
        return materialRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "material", key = "#uid", unless="#result==null")
    @Override
    public Optional<MaterialEntity> findByUid(String uid) {
        return materialRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return materialRepository.existsByUid(uid);
    }

    @Override
    public MaterialResponse create(MaterialRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MaterialEntity entity = modelMapper.map(request, MaterialEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MaterialEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create material failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public MaterialResponse update(MaterialRequest request) {
        Optional<MaterialEntity> optional = materialRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MaterialEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MaterialEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update material failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Material not found");
        }
    }

    @Override
    protected MaterialEntity doSave(MaterialEntity entity) {
        return materialRepository.save(entity);
    }

    @Override
    public MaterialEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MaterialEntity entity) {
        try {
            Optional<MaterialEntity> latest = materialRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MaterialEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return materialRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MaterialEntity> optional = materialRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // materialRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Material not found");
        }
    }

    @Override
    public void delete(MaterialRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MaterialResponse convertToResponse(MaterialEntity entity) {
        return modelMapper.map(entity, MaterialResponse.class);
    }

    @Override
    public MaterialExcel convertToExcel(MaterialEntity entity) {
        return modelMapper.map(entity, MaterialExcel.class);
    }
    
    public void initMaterials(String orgUid) {
        // log.info("initThreadMaterial");
        for (String material : MaterialInitData.getAllMaterials()) {
            MaterialRequest materialRequest = MaterialRequest.builder()
                    .uid(Utils.formatUid(orgUid, material))
                    .name(material)
                    .order(0)
                    .type(MaterialTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(materialRequest);
        }
    }
    
}
