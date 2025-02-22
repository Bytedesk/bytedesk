/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:07:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 16:20:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.browse;

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
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorRestService;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class BrowseRestService extends BaseRestService<BrowseEntity, BrowseRequest, BrowseResponse> {

    private final BrowseRepository browseRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final VisitorRestService visitorRestService;

    @Override
    public Page<BrowseResponse> queryByOrg(BrowseRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<BrowseEntity> spec = BrowseSpecification.search(request);
        Page<BrowseEntity> page = browseRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<BrowseResponse> queryByUser(BrowseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "browse", key = "#uid")
    @Override
    public Optional<BrowseEntity> findByUid(String uid) {
        return browseRepository.findByUid(uid);
    }

    @Override
    public BrowseResponse create(BrowseRequest request) {
        
        BrowseEntity entity = modelMapper.map(request, BrowseEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        Optional<VisitorEntity> visitorEntity = visitorRestService.findByUid(request.getVisitorUid());
        if (visitorEntity.isPresent()) {
            entity.setVisitor(visitorEntity.get());
        }
        
        return convertToResponse(browseRepository.save(entity));
    }

    @Override
    public BrowseResponse update(BrowseRequest request) {
        
        Optional<BrowseEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            BrowseEntity entity = optional.get();
            modelMapper.map(request, entity);
            return convertToResponse(browseRepository.save(entity));
        }
        return null;
    }

    @Override
    public BrowseEntity save(BrowseEntity entity) {
        try {
            return browseRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<BrowseEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            browseRepository.delete(optional.get());
        }
    }

    @Override
    public void delete(BrowseRequest request) {
        deleteByUid(request.getUid());
    }

    public void deleteAll() {
        browseRepository.deleteAll();
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            BrowseEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public BrowseResponse convertToResponse(BrowseEntity entity) {
        return modelMapper.map(entity, BrowseResponse.class);
    }



}
