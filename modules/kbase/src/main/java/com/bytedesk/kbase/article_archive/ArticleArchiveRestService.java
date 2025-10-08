/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:48:44
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

    private final ArticleArchiveRepository articleArchiveRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<ArticleArchiveEntity> createSpecification(ArticleArchiveRequest request) {
        return ArticleArchiveSpecification.search(request, authService);
    }

    @Override
    protected Page<ArticleArchiveEntity> executePageQuery(Specification<ArticleArchiveEntity> spec, Pageable pageable) {
        return articleArchiveRepository.findAll(spec, pageable);
    }


    @Cacheable(value = "article_archive", key="#uid", unless = "#result == null")
    @Override
    public Optional<ArticleArchiveEntity> findByUid(String uid) {
        return articleArchiveRepository.findByUid(uid);
    }

    @Override
    public ArticleArchiveResponse create(ArticleArchiveRequest request) {
        UserEntity user = authService.getUser();
        

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
    protected ArticleArchiveEntity doSave(ArticleArchiveEntity entity) {
        return articleArchiveRepository.save(entity);
    }

    @Override
    public ArticleArchiveEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ArticleArchiveEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<ArticleArchiveEntity> latest = articleArchiveRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ArticleArchiveEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return articleArchiveRepository.save(latestEntity);
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
