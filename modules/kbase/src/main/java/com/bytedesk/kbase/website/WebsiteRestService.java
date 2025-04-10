/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-09 22:23:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.website;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebsiteRestService extends BaseRestService<WebsiteEntity, WebsiteRequest, WebsiteResponse> {

    private final WebsiteRepository websiteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<WebsiteResponse> queryByOrg(WebsiteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebsiteEntity> spec = WebsiteSpecification.search(request);
        Page<WebsiteEntity> page = websiteRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WebsiteResponse> queryByUser(WebsiteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "website", key = "#uid", unless="#result==null")
    @Override
    public Optional<WebsiteEntity> findByUid(String uid) {
        return websiteRepository.findByUid(uid);
    }

    @Override
    public WebsiteResponse create(WebsiteRequest request) {
        
        WebsiteEntity entity = modelMapper.map(request, WebsiteEntity.class);
        entity.setUid(uidUtils.getUid());

        WebsiteEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create website failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WebsiteResponse update(WebsiteRequest request) {
        Optional<WebsiteEntity> optional = websiteRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WebsiteEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WebsiteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update website failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Website not found");
        }
    }

    @Override
    public WebsiteEntity save(WebsiteEntity entity) {
        try {
            return websiteRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WebsiteEntity> optional = websiteRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // websiteRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Website not found");
        }
    }

    @Override
    public void delete(WebsiteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WebsiteEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public WebsiteResponse convertToResponse(WebsiteEntity entity) {
        return modelMapper.map(entity, WebsiteResponse.class);
    }

    public Page<WebsiteEntity> queryByOrgEntity(WebsiteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebsiteEntity> spec = WebsiteSpecification.search(request);
        return websiteRepository.findAll(spec, pageable);
    }

    public WebsiteExcel convertToExcel(WebsiteEntity website) {
        return modelMapper.map(website, WebsiteExcel.class);
    }
    
}
