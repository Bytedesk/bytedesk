/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-12 15:28:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.split;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SplitRestService extends BaseRestServiceWithExcel<SplitEntity, SplitRequest, SplitResponse, SplitExcel> {

    private final SplitRepository splitRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<SplitEntity> queryByOrgEntity(SplitRequest request) {
        Pageable pageable = request.getPageable();
        Specification<SplitEntity> spec = SplitSpecification.search(request);
        return splitRepository.findAll(spec, pageable);
    }

    @Override
    public Page<SplitResponse> queryByOrg(SplitRequest request) {
        Page<SplitEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<SplitResponse> queryByUser(SplitRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "split", key = "#uid", unless="#result==null")
    @Override
    public Optional<SplitEntity> findByUid(String uid) {
        return splitRepository.findByUid(uid);
    }

    @Override
    public SplitResponse create(SplitRequest request) {
        // log.info("SplitRestService create: {}", request);
        SplitEntity entity = SplitEntity.builder()
            .uid(uidUtils.getUid())
            .name(request.getName())
            .content(request.getContent())
            .type(request.getType())
            .level(request.getLevel())
            .platform(request.getPlatform())
            .docId(request.getDocId())
            .typeUid(request.getTypeUid())
            .enabled(request.isEnabled())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .categoryUid(request.getCategoryUid())
            .kbUid(request.getKbUid())
            .userUid(request.getUserUid())
            .orgUid(request.getOrgUid())
            .build();
        // Error mapping a854a402-04c9-4018-84ee-f0313ad00f48 to java.lang.Long
        // Caused by: java.lang.NumberFormatException: For input string: "a854a402-04c9-4018-84ee-f0313ad00f48"
        // SplitEntity entity = modelMapper.map(request, SplitEntity.class);
        // entity.setUid(uidUtils.getUid());
        // entity.setOrgUid(request.getOrgUid());
        // log.info("SplitRestService create: {}", entity);
        // 
        SplitEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create split failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public SplitResponse update(SplitRequest request) {
        // 
        Optional<SplitEntity> optional = splitRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SplitEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setName(request.getName());
            entity.setContent(request.getContent());
            entity.setEnabled(request.isEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            // entity.setCategoryUid(request.getCategoryUid());
            // entity.setKbUid(request.getKbUid());
            // entity.setUserUid(request.getUserUid());
            // entity.setOrgUid(request.getOrgUid());
            // entity.setType(request.getType());
            // entity.setDocId(request.getDocId());
            //
            SplitEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update split failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Split not found");
        }
    }

    @Override
    public SplitEntity save(SplitEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    protected SplitEntity doSave(SplitEntity entity) {
        return splitRepository.save(entity);
    }

    @Override
    public SplitEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SplitEntity entity) {
        try {
            Optional<SplitEntity> latest = splitRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SplitEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return splitRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public void deleteByDocList(List<String> docIdList) {
        // 遍历 docIdList
        for (String docId : docIdList) {
            // 查找 docId 对应的所有 split
            Optional<SplitEntity> splitList = splitRepository.findByDocId(docId);
            // 删除
            splitList.ifPresent(split -> {
                split.setDeleted(true);
                save(split);
            });
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<SplitEntity> optional = splitRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Split not found");
        }
    }

    @Override
    public void delete(SplitRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SplitResponse convertToResponse(SplitEntity entity) {
        return modelMapper.map(entity, SplitResponse.class);
    }

    @Override
    public SplitExcel convertToExcel(SplitEntity split) {
        return modelMapper.map(split, SplitExcel.class);
    }
}
