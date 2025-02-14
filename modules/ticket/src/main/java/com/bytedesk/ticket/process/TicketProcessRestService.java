/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 11:17:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TicketProcessRestService extends BaseRestService<TicketProcessEntity, TicketProcessRequest, TicketProcessResponse> {

    private final TicketProcessRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<TicketProcessResponse> queryByOrg(TicketProcessRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<TicketProcessEntity> spec = TicketProcessSpecification.search(request);
        Page<TicketProcessEntity> page = tagRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TicketProcessResponse> queryByUser(TicketProcessRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketProcessEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    @Override
    public TicketProcessResponse create(TicketProcessRequest request) {
        
        TicketProcessEntity entity = modelMapper.map(request, TicketProcessEntity.class);
        entity.setUid(uidUtils.getUid());

        TicketProcessEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public TicketProcessResponse update(TicketProcessRequest request) {
        Optional<TicketProcessEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TicketProcessEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TicketProcessEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TicketProcess not found");
        }
    }

    @Override
    public TicketProcessEntity save(TicketProcessEntity entity) {
        try {
            return tagRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TicketProcessEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("TicketProcess not found");
        }
    }

    @Override
    public void delete(TicketProcessRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketProcessEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TicketProcessResponse convertToResponse(TicketProcessEntity entity) {
        return modelMapper.map(entity, TicketProcessResponse.class);
    }
    
}
