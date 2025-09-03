/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 20:58:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

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
public class WebpageRestService
        extends BaseRestServiceWithExport<WebpageEntity, WebpageRequest, WebpageResponse, WebpageExcel> {

    private final WebpageRepository webpageRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    private final WebpageCrawlerService webpageCrawlerService;

    @Override
    protected Specification<WebpageEntity> createSpecification(WebpageRequest request) {
        return WebpageSpecification.search(request, authService);
    }

    @Override
    protected Page<WebpageEntity> executePageQuery(Specification<WebpageEntity> spec, Pageable pageable) {
        return webpageRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "webpage", key = "#uid", unless = "#result==null")
    @Override
    public Optional<WebpageEntity> findByUid(String uid) {
        return webpageRepository.findByUid(uid);
    }

    @Cacheable(value = "webpage", key = "#kbUid", unless = "#result==null")
    public List<WebpageEntity> findByKbUid(String kbUid) {
        return webpageRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    @Override
    public WebpageResponse create(WebpageRequest request) {

        WebpageEntity entity = modelMapper.map(request, WebpageEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }

        WebpageEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webpage failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WebpageResponse update(WebpageRequest request) {
        Optional<WebpageEntity> optional = webpageRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WebpageEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WebpageEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webpage failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Webpage not found");
        }
    }

    protected WebpageEntity doSave(WebpageEntity entity) {
        return webpageRepository.save(entity);
    }

    @Override
    public WebpageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            WebpageEntity entity) {
        try {
            Optional<WebpageEntity> latest = webpageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebpageEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setTitle(entity.getTitle());
                latestEntity.setUrl(entity.getUrl());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.getEnabled());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setElasticStatus(entity.getElasticStatus());

                return webpageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WebpageEntity> optional = webpageRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // webpageRepository.delete(optional.get());
        } else {
            throw new RuntimeException("Webpage not found");
        }
    }

    @Override
    public void delete(WebpageRequest request) {
        deleteByUid(request.getUid());
    }

    // deleteAll
    public void deleteAll(WebpageRequest request) {
        List<WebpageEntity> webpages = findByKbUid(request.getKbUid());
        for (WebpageEntity webpage : webpages) {
            webpage.setDeleted(true);
            save(webpage);
        }
    }

    // enable/disable webpage
    public WebpageResponse enable(WebpageRequest request) {
        Optional<WebpageEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WebpageEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            // 
            WebpageEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webpage failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Webpage not found");
        }
    }

    // crawl webpage content
    public WebpageResponse crawlContent(WebpageRequest request) {
        Optional<WebpageEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            WebpageEntity entity = optional.get();
            // 使用爬虫服务抓取内容并更新实体
            WebpageEntity updatedEntity = webpageCrawlerService.crawlAndUpdateContent(entity);
            return convertToResponse(updatedEntity);
        } else {
            throw new RuntimeException("Webpage not found");
        }
    }

    @Override
    public WebpageResponse convertToResponse(WebpageEntity entity) {
        return modelMapper.map(entity, WebpageResponse.class);
    }

    @Override
    public WebpageExcel convertToExcel(WebpageEntity webpage) {
        return modelMapper.map(webpage, WebpageExcel.class);
    }

}
