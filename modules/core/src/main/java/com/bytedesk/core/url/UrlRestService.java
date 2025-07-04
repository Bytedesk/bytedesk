/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:21:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.url;

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
public class UrlRestService extends BaseRestService<UrlEntity, UrlRequest, UrlResponse> {

    private final UrlRepository urlRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<UrlResponse> queryByOrg(UrlRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<UrlEntity> spec = UrlSpecification.search(request);
        Page<UrlEntity> page = urlRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<UrlResponse> queryByUser(UrlRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "url", key = "#uid", unless="#result==null")
    @Override
    public Optional<UrlEntity> findByUid(String uid) {
        return urlRepository.findByUid(uid);
    }

    @Cacheable(value = "url", key = "#url", unless="#result==null")
    public Optional<UrlEntity> findByUrl(String url) {
        return urlRepository.findByUrl(url);
    }

    @Cacheable(value = "url", key = "#shortUrl", unless="#result==null")
    public Optional<UrlEntity> findByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }

    @Override
    public UrlResponse create(UrlRequest request) {
        
        UrlEntity entity = modelMapper.map(request, UrlEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        String shortUrl = UrlUtils.to62url(request.getUrl());
        entity.setShortUrl(shortUrl);

        UrlEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public UrlResponse update(UrlRequest request) {
        Optional<UrlEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            UrlEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setUrl(request.getUrl());
            // 
            String shortUrl = UrlUtils.to62url(request.getUrl());
            entity.setShortUrl(shortUrl);
            // 
            UrlEntity savedEntity = save(entity);
            if (savedEntity == null) {  
                throw new RuntimeException("Update tag failed");
            }

            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Url not found");
        }
    }

    @Override
    public UrlEntity save(UrlEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected UrlEntity doSave(UrlEntity entity) {
        return urlRepository.save(entity);
    }

    @Override
    public UrlEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UrlEntity entity) {
        try {
            Optional<UrlEntity> latest = urlRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                UrlEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setUrl(entity.getUrl());
                latestEntity.setShortUrl(entity.getShortUrl());
                return urlRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<UrlEntity> optional = urlRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Url not found");
        }
    }

    @Override
    public void delete(UrlRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public UrlResponse convertToResponse(UrlEntity entity) {
        return modelMapper.map(entity, UrlResponse.class);
    }

    @Override
    public UrlResponse queryByUid(UrlRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
    // public Boolean existsByPlatform(PlatformEnum platform) {
    //     return tagRepository.existsByPlatform(platform.name());
    // }
    
}
