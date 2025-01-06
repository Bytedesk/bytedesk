/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-06 22:43:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArticleService extends BaseRestService<ArticleEntity, ArticleRequest, ArticleResponse> {

    private final ArticleRepository FaqRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<ArticleResponse> queryByOrg(ArticleRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<ArticleEntity> spec = ArticleSpecification.search(request);

        Page<ArticleEntity> page = FaqRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<ArticleResponse> queryByUser(ArticleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "article", key="#uid", unless = "#result == null")
    @Override
    public Optional<ArticleEntity> findByUid(String uid) {
        return FaqRepository.findByUid(uid);
    }

    @Override
    public ArticleResponse create(ArticleRequest request) {

        ArticleEntity entity = modelMapper.map(request, ArticleEntity.class);
        entity.setUid(uidUtils.getCacheSerialUid());
        // 
        UserEntity user = authService.getUser();
        UserProtobuf userProtobuf = ConvertUtils.convertToUserProtobuf(user);
        entity.setUser(JSON.toJSONString(userProtobuf));
        // 
        entity.setOrgUid(user.getOrgUid());
        //
        // category
        // Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
        // if (categoryOptional.isPresent()) {
        //     entity.setCategory(categoryOptional.get());
        // }
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
            entity.setContentHtml(request.getContentHtml());
            entity.setContentMarkdown(request.getContentMarkdown());
            // entity.setCategoryUid(request.getCategoryUid());
            // 
            // category
            // Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
            // if (categoryOptional.isPresent()) {
            //     entity.setCategory(categoryOptional.get());
            // }
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
            return FaqRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ArticleEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ArticleResponse convertToResponse(ArticleEntity entity) {
        ArticleResponse articleResponse = modelMapper.map(entity, ArticleResponse.class);
        articleResponse.setUser(JSON.parseObject(entity.getUser(), UserProtobuf.class));
        return articleResponse;
    }

}
