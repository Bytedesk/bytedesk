/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:22:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WebsiteRestService
        extends BaseRestServiceWithExport<WebsiteEntity, WebsiteRequest, WebsiteResponse, WebsiteExcel> {

    private final WebsiteRepository websiteRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    @Override
    protected Specification<WebsiteEntity> createSpecification(WebsiteRequest request) {
        return WebsiteSpecification.search(request);
    }

    @Override
    protected Page<WebsiteEntity> executePageQuery(Specification<WebsiteEntity> spec, Pageable pageable) {
        return websiteRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "website", key = "#uid", unless = "#result==null")
    @Override
    public Optional<WebsiteEntity> findByUid(String uid) {
        return websiteRepository.findByUid(uid);
    }

    @Cacheable(value = "website", key = "#kbUid", unless = "#result==null")
    public List<WebsiteEntity> findByKbUid(String kbUid) {
        return websiteRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    @Override
    public WebsiteResponse create(WebsiteRequest request) {

        WebsiteEntity entity = modelMapper.map(request, WebsiteEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }

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
        } else {
            throw new RuntimeException("Website not found");
        }
    }

    protected WebsiteEntity doSave(WebsiteEntity entity) {
        return websiteRepository.save(entity);
    }

    @Override
    public WebsiteEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            WebsiteEntity entity) {
        try {
            Optional<WebsiteEntity> latest = websiteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebsiteEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setUrl(entity.getUrl());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.getEnabled());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setElasticStatus(entity.getElasticStatus());

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
        } else {
            throw new RuntimeException("Website not found");
        }
    }

    @Override
    public void delete(WebsiteRequest request) {
        deleteByUid(request.getUid());
    }

    // deleteAll
    public void deleteAll(WebsiteRequest request) {
        List<WebsiteEntity> websites = findByKbUid(request.getKbUid());
        for (WebsiteEntity website : websites) {
            website.setDeleted(true);
            save(website);
        }
    }

    // enable/disable website
    public WebsiteResponse enable(WebsiteRequest request) {
        Optional<WebsiteEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WebsiteEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            // 
            WebsiteEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update website failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Website not found");
        }
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
