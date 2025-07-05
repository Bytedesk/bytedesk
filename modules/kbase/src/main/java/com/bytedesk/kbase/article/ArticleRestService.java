/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:23:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRepository;
import com.bytedesk.kbase.utils.KbaseConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArticleRestService extends BaseRestServiceWithExcel<ArticleEntity, ArticleRequest, ArticleResponse, ArticleExcel> {

    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    // 循环依赖
    // private final KbaseRestService kbaseRestService;
    private final KbaseRepository kbaseRepository;

    @Override
    public Page<ArticleEntity> queryByOrgEntity(ArticleRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ArticleEntity> spec = ArticleSpecification.search(request);
        return articleRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ArticleResponse> queryByOrg(ArticleRequest request) {
        Page<ArticleEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ArticleResponse> queryByUser(ArticleRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        String userUid = user.getUid();
        request.setUserUid(userUid);
        // 
        return queryByOrg(request);
    }

    @Override
    public ArticleResponse queryByUid(ArticleRequest request) {
        Optional<ArticleEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    @Cacheable(value = "article", key="#uid", unless = "#result == null")
    @Override
    public Optional<ArticleEntity> findByUid(String uid) {
        return articleRepository.findByUid(uid);
    }

    public List<ArticleEntity> findByKbUid(String kbUid) {
        return articleRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    @Override
    public ArticleResponse create(ArticleRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }

        ArticleEntity entity = modelMapper.map(request, ArticleEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
        entity.setUser(userProtobuf.toJson());
        entity.setOrgUid(user.getOrgUid());
        //
        Optional<KbaseEntity> kbase = kbaseRepository.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        ArticleEntity savedArticle = save(entity);
        if (savedArticle == null) {
            throw new RuntimeException("article save failed");
        }
        // 
        return convertToResponse(savedArticle);
    }

    @Override
    public ArticleResponse update(ArticleRequest request) {

        Optional<ArticleEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ArticleEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setSummary(request.getSummary());
            entity.setContentHtml(request.getContentHtml());
            entity.setContentMarkdown(request.getContentMarkdown());
            entity.setCategoryUid(request.getCategoryUid());
            //
            ArticleEntity savedArticle = save(entity);
            if (savedArticle == null) {
                throw new RuntimeException("article save failed");
            }
            //
            return convertToResponse(savedArticle);
            
        } else {
            throw new RuntimeException("article not found");
        }
    }

    @Override
    public ArticleEntity save(ArticleEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @CachePut(value = "article", key = "#entity.uid")
    @Override
    protected ArticleEntity doSave(ArticleEntity entity) {
        return articleRepository.save(entity);
    }

    @CacheEvict(value = "article", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<ArticleEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(ArticleRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public ArticleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ArticleEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<ArticleEntity> latest = articleRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ArticleEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return articleRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public ArticleResponse convertToResponse(ArticleEntity entity) {
        return KbaseConvertUtils.convertToArticleResponse(entity);
    }

    

    @Override
    public ArticleExcel convertToExcel(ArticleEntity article) {
        return modelMapper.map(article, ArticleExcel.class);
    }

    

}
