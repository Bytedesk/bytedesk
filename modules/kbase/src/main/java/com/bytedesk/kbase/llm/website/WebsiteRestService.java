/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 12:38:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.website;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebsiteRestService extends BaseRestServiceWithExcel<WebsiteEntity, WebsiteRequest, WebsiteResponse, WebsiteExcel> {

    private final WebsiteRepository websiteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<WebsiteEntity> queryByOrgEntity(WebsiteRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebsiteEntity> spec = WebsiteSpecification.search(request);
        return websiteRepository.findAll(spec, pageable);
    }

    @Override
    public Page<WebsiteResponse> queryByOrg(WebsiteRequest request) {
        Page<WebsiteEntity> page = queryByOrgEntity(request);
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
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    protected WebsiteEntity doSave(WebsiteEntity entity) {
        return websiteRepository.save(entity);
    }

    @Override
    public WebsiteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WebsiteEntity entity) {
        try {
            Optional<WebsiteEntity> latest = websiteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebsiteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setUrl(entity.getUrl());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.isEnabled());
                
                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setStatus(entity.getStatus());
                
                return websiteRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
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
    public WebsiteResponse convertToResponse(WebsiteEntity entity) {
        return modelMapper.map(entity, WebsiteResponse.class);
    }

    @Override
    public WebsiteExcel convertToExcel(WebsiteEntity website) {
        return modelMapper.map(website, WebsiteExcel.class);
    }
    
}
