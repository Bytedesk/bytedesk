/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:09:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.complaint;

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
public class ComplaintRestService extends BaseRestServiceWithExport<ComplaintEntity, ComplaintRequest, ComplaintResponse, ComplaintExcel> {

    private final ComplaintRepository complaintRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<ComplaintEntity> createSpecification(ComplaintRequest request) {
        return ComplaintSpecification.search(request, authService);
    }

    @Override
    protected Page<ComplaintEntity> executePageQuery(Specification<ComplaintEntity> spec, Pageable pageable) {
        return complaintRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "complaint", key = "#uid", unless="#result==null")
    @Override
    public Optional<ComplaintEntity> findByUid(String uid) {
        return complaintRepository.findByUid(uid);
    }

    // @Cacheable(value = "complaint", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    // public Optional<ComplaintEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
    //     return complaintRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    // }

    public Boolean existsByUid(String uid) {
        return complaintRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ComplaintResponse create(ComplaintRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<ComplaintEntity> complaint = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (complaint.isPresent()) {
        //         return convertToResponse(complaint.get());
        //     }
        // }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        ComplaintEntity entity = modelMapper.map(request, ComplaintEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ComplaintEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create complaint failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ComplaintResponse update(ComplaintRequest request) {
        Optional<ComplaintEntity> optional = complaintRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ComplaintEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            ComplaintEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update complaint failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Complaint not found");
        }
    }

    @Override
    protected ComplaintEntity doSave(ComplaintEntity entity) {
        return complaintRepository.save(entity);
    }

    @Override
    public ComplaintEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ComplaintEntity entity) {
        try {
            Optional<ComplaintEntity> latest = complaintRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ComplaintEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return complaintRepository.save(latestEntity);
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
        Optional<ComplaintEntity> optional = complaintRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // complaintRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Complaint not found");
        }
    }

    @Override
    public void delete(ComplaintRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ComplaintResponse convertToResponse(ComplaintEntity entity) {
        return modelMapper.map(entity, ComplaintResponse.class);
    }

    @Override
    public ComplaintExcel convertToExcel(ComplaintEntity entity) {
        return modelMapper.map(entity, ComplaintExcel.class);
    }
    
    public void initComplaints(String orgUid) {
        // log.info("initThreadComplaint");
        for (String complaint : ComplaintInitData.getAllComplaints()) {
            ComplaintRequest complaintRequest = ComplaintRequest.builder()
                    .uid(Utils.formatUid(orgUid, complaint))
                    // .name(complaint)
                    .type(ComplaintTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(complaintRequest);
        }
    }

    
    
}
