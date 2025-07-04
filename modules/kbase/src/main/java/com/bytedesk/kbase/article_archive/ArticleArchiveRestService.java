/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 09:24:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article_archive;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.kbase.utils.KbaseConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArticleArchiveRestService extends BaseRestService<ArticleArchiveEntity, ArticleArchiveRequest, ArticleArchiveResponse> {

    private final ArticleArchiveRepository article_archiveRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<ArticleArchiveResponse> queryByOrg(ArticleArchiveRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ArticleArchiveEntity> spec = ArticleArchiveSpecification.search(request);
        Page<ArticleArchiveEntity> page = article_archiveRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ArticleArchiveResponse> queryByUser(ArticleArchiveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        String userUid = user.getUid();
        //
        request.setUserUid(userUid);
        // 
        return queryByOrg(request);
    }

    @Override
    public ArticleArchiveResponse queryByUid(ArticleArchiveRequest request) {
        Optional<ArticleArchiveEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    @Cacheable(value = "article_archive", key="#uid", unless = "#result == null")
    @Override
    public Optional<ArticleArchiveEntity> findByUid(String uid) {
        return article_archiveRepository.findByUid(uid);
    }

    @Override
    public ArticleArchiveResponse initVisitor(ArticleArchiveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }

        ArticleArchiveEntity entity = modelMapper.map(request, ArticleArchiveEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
        entity.setUser(JSON.toJSONString(userProtobuf));
        entity.setOrgUid(user.getOrgUid());
        //
        ArticleArchiveEntity savedArticleArchive = save(entity);
        if (savedArticleArchive == null) {
            throw new RuntimeException("article_archive save failed");
        }
        // 
        return convertToResponse(savedArticleArchive);
    }

    @Override
    public ArticleArchiveResponse update(ArticleArchiveRequest request) {

        Optional<ArticleArchiveEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ArticleArchiveEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setSummary(request.getSummary());
            entity.setContentHtml(request.getContentHtml());
            entity.setContentMarkdown(request.getContentMarkdown());
            entity.setCategoryUid(request.getCategoryUid());
            //
            ArticleArchiveEntity savedArticleArchive = save(entity);
            if (savedArticleArchive == null) {
                throw new RuntimeException("article_archive save failed");
            }
            //
            return convertToResponse(savedArticleArchive);
            
        } else {
            throw new RuntimeException("article_archive not found");
        }
    }

    @Override
    public ArticleArchiveEntity save(ArticleArchiveEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected ArticleArchiveEntity doSave(ArticleArchiveEntity entity) {
        return article_archiveRepository.save(entity);
    }

    @Override
    public ArticleArchiveEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ArticleArchiveEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<ArticleArchiveEntity> latest = article_archiveRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ArticleArchiveEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return article_archiveRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ArticleArchiveEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(ArticleArchiveRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public ArticleArchiveResponse convertToResponse(ArticleArchiveEntity entity) {
        return KbaseConvertUtils.convertToArticleArchiveResponse(entity);
    }

    
}
