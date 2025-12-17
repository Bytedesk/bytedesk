/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-20 15:14:06
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

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRepository;
import com.bytedesk.kbase.utils.KbaseConvertUtils;
import com.bytedesk.kbase.utils.MarkdownRenderUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleRestService extends BaseRestServiceWithExport<ArticleEntity, ArticleRequest, ArticleResponse, ArticleExcel> {

    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;
    
    private final KbaseRepository kbaseRepository;

    @Override
    protected Specification<ArticleEntity> createSpecification(ArticleRequest request) {
        return ArticleSpecification.search(request, authService);
    }

    @Override
    protected Page<ArticleEntity> executePageQuery(Specification<ArticleEntity> spec, Pageable pageable) {
        return articleRepository.findAll(spec, pageable);
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
        // 
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
            entity.setCoverImageUrl(request.getCoverImageUrl());
            entity.setType(request.getType());
            entity.setContentHtml(request.getContentHtml());
            entity.setContentMarkdown(request.getContentMarkdown());
            entity.setCategoryUid(request.getCategoryUid());
            entity.setPublished(request.getPublished());
            entity.setTop(request.getTop());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
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
        ArticleResponse response = KbaseConvertUtils.convertToArticleResponse(entity);

        // For template rendering (ftl): ensure MARKDOWN articles have contentHtml available.
        String type = response.getType();
        boolean isMarkdown = type != null && ArticleTypeEnum.MARKDOWN.name().equalsIgnoreCase(type);
        if (isMarkdown && (response.getContentHtml() == null || response.getContentHtml().isBlank())
                && response.getContentMarkdown() != null && !response.getContentMarkdown().isBlank()) {
            response.setContentHtml(MarkdownRenderUtils.toHtml(response.getContentMarkdown()));
        }

        return response;
    }

    @Override
    public ArticleExcel convertToExcel(ArticleEntity article) {
        return modelMapper.map(article, ArticleExcel.class);
    }

    /**
     * 为指定组织初始化一篇默认的“如何使用帮助文档”文章
     * 去重逻辑在调用方（ArticleInitializer）中处理，这里仅负责创建
     */
    public void initDefaultArticleForOrg(String orgUid) {
        try {
            // 使用与 initKbase 一致的固定UID，确保绑定到该组织的HELPCENTER知识库
            String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_HELPCENTER_UID);
            Optional<KbaseEntity> kbOpt = kbaseRepository.findByUid(kbUid);
            if (kbOpt.isEmpty()) {
                log.warn("initDefaultArticleForOrg: HELPCENTER Kbase not found by uid={} for orgUid={}", kbUid, orgUid);
                return;
            }
            KbaseEntity kb = kbOpt.get();

            // 构造默认文章内容
            String title = "如何使用帮助文档";
            String summary = "在管理后台 -> 知识库 -> 帮助文档 中添加和管理文章";
            String md = "# 如何使用帮助文档\n\n" +
                        "您可以在【管理后台】 -> 【知识库】 -> 【帮助文档】中：\n\n" +
                        "- 新建、编辑、发布文章\n" +
                        "- 支持 Markdown 与富文本\n" +
                        "- 配置是否公开访问与置顶展示\n\n" +
                        "建议：为常见问题创建分类与标签，便于搜索与维护。";

            ArticleEntity entity = ArticleEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .title(title)
                .summary(summary)
                .contentMarkdown(md)
                .type(ArticleTypeEnum.MARKDOWN.name())
                .published(true)
                .kbase(kb)
                .build();

            save(entity);
            log.info("Initialized default guide article for orgUid={} in kbase uid={}", orgUid, kb.getUid());
        } catch (Exception e) {
            log.error("initDefaultArticleForOrg failed for orgUid={}: {}", orgUid, e.getMessage(), e);
        }
    }


}
