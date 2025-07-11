/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-27 10:27:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.base.LlmProviderConfigDefault;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.article.ArticleRequest;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.utils.KbaseConvertUtils;
import com.bytedesk.core.utils.LlmConfigUtils;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

@Service
@AllArgsConstructor
public class KbaseRestService extends BaseRestService<KbaseEntity, KbaseRequest, KbaseResponse> {

    private final KbaseRepository kbaseRepository;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryRestService;

    private final ArticleRestService articleRestService;

    private final AuthService authService;

    private final Environment environment;

    @Override
    public Page<KbaseResponse> queryByOrg(KbaseRequest request) {
        Pageable pageable = request.getPageable();
        Specification<KbaseEntity> spec = KbaseSpecification.search(request);
        Page<KbaseEntity> page = kbaseRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<KbaseResponse> queryByUser(KbaseRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    // query detail
    public KbaseResponse queryByUid(KbaseRequest request) {
        Optional<KbaseEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        return null;
    }

    @Cacheable(value = "kb", key = "#uid", unless = "#result==null")
    @Override
    public Optional<KbaseEntity> findByUid(@NonNull String uid) {
        return kbaseRepository.findByUid(uid);
    }

    public Boolean existsByUid(@NonNull String uid) {
        return kbaseRepository.existsByUid(uid);
    }

    @Override
    public KbaseResponse create(KbaseRequest request) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        //
        KbaseEntity entity = KbaseEntity.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getUid());
        }
        //
        UserEntity user = authService.getCurrentUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        }
        //
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());
        entity.setHeadline(request.getHeadline());
        entity.setDescriptionHtml(request.getDescriptionHtml());
        entity.setFooterHtml(request.getFooterHtml());
        entity.setLanguage(request.getLanguage());
        entity.setLevel(request.getLevel());
        entity.setOrgUid(request.getOrgUid());
        entity.setAgentUid(request.getAgentUid());
        //
        // Get default model config if not provided
        if (!StringUtils.hasText(request.getEmbeddingProvider()) || !StringUtils.hasText(request.getEmbeddingModel())) {
            LlmProviderConfigDefault modelConfig = getLlmProviderConfigDefault();
            entity.setEmbeddingProvider(request.getEmbeddingProvider() != null ? request.getEmbeddingProvider() : modelConfig.getDefaultEmbeddingProvider());
            entity.setEmbeddingModel(request.getEmbeddingModel() != null ? request.getEmbeddingModel() : modelConfig.getDefaultEmbeddingModel());
        } else {
            entity.setEmbeddingProvider(request.getEmbeddingProvider());
            entity.setEmbeddingModel(request.getEmbeddingModel());
        }
        //
        KbaseEntity savedKb = save(entity);
        if (savedKb == null) {
            throw new RuntimeException("knowledge_base not saved");
        }

        return convertToResponse(savedKb);
    }

    @Override
    public KbaseResponse update(KbaseRequest request) {

        Optional<KbaseEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            KbaseEntity entity = optional.get();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setType(request.getType());
            entity.setHeadline(request.getHeadline());
            entity.setDescriptionHtml(request.getDescriptionHtml());
            entity.setFooterHtml(request.getFooterHtml());
            entity.setLanguage(request.getLanguage());
            //
            entity.setEmbeddingProvider(request.getEmbeddingProvider());
            entity.setEmbeddingModel(request.getEmbeddingModel());
            // 
            KbaseEntity savedKb = save(entity);
            if (savedKb == null) {
                throw new RuntimeException("knowledge_base not saved");
            }
            //
            return convertToResponse(savedKb);
        } else {
            throw new RuntimeException("knowledge_base not found");
        }
    }

    public List<KbaseEntity> findByLevelAndType(LevelEnum level, KbaseTypeEnum type) {
        return kbaseRepository.findByLevelAndTypeAndDeleted(level.name(), type.name(), false);
    }

    public List<KbaseEntity> findByLevelAndTypeAndOrgUid(LevelEnum level, KbaseTypeEnum type, String orgUid) {
        return kbaseRepository.findByLevelAndTypeAndOrgUidAndDeleted(level.name(), type.name(), orgUid, false);
    }

    public List<KbaseEntity> findByLevelAndTypeAndAgentUid(LevelEnum level, KbaseTypeEnum type, String agentUid) {
        return kbaseRepository.findByLevelAndTypeAndAgentUidAndDeleted(level.name(), type.name(), agentUid, false);
    }

    @CachePut(value = "kbase", key = "#entity.uid")
    @Override
    protected KbaseEntity doSave(KbaseEntity entity) {
        return kbaseRepository.save(entity);
    }

    @CacheEvict(value = "kbase", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<KbaseEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(KbaseRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public KbaseEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            KbaseEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<KbaseEntity> latest = kbaseRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                KbaseEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return kbaseRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public KbaseResponse convertToResponse(KbaseEntity entity) {
        return KbaseConvertUtils.convertToKbaseResponse(entity);
    }

    public Page<CategoryResponse> getCategories(KbaseEntity kbaseEntity) {
        //
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setPageNumber(0);
        categoryRequest.setPageSize(50);
        categoryRequest.setType(KbaseTypeEnum.HELPCENTER.name());
        categoryRequest.setKbUid(kbaseEntity.getUid());
        categoryRequest.setOrgUid(kbaseEntity.getOrgUid());
        //
        return categoryRestService.queryByOrg(categoryRequest);
    }

    public Page<ArticleResponse> getArticles(KbaseEntity kbaseEntity) {
        //
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setPageNumber(0);
        articleRequest.setPageSize(50);
        articleRequest.setKbUid(kbaseEntity.getUid());
        articleRequest.setOrgUid(kbaseEntity.getOrgUid());
        //
        return articleRestService.queryByOrg(articleRequest);
    }

    public Page<ArticleResponse> getArticlesByCategory(KbaseEntity kbaseEntity, String categoryUid) {
        //
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setPageNumber(0);
        articleRequest.setPageSize(50);
        articleRequest.setCategoryUid(categoryUid);
        articleRequest.setKbUid(kbaseEntity.getUid());
        articleRequest.setOrgUid(kbaseEntity.getOrgUid());
        //
        return articleRestService.queryByOrg(articleRequest);
    }

    // 初始化知识库
    public void initKbase(String orgUid) {

        // 初始化帮助文档知识库
        KbaseRequest kownledgebaseRequestHelpdoc = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_HELPCENTER_UID))
                .type(KbaseTypeEnum.HELPCENTER.name())
                .name(KbaseConsts.KB_HELPCENTER_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestHelpdoc);

        // 初始化内部知识库 NOTEBASE
        KbaseRequest kownledgebaseRequestNotebase = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_NOTEBASE_UID))
                .type(KbaseTypeEnum.NOTEBASE.name())
                .name(KbaseConsts.KB_NOTEBASE_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestNotebase);

        // 初始化LLM知识库
        KbaseRequest kownledgebaseRequestLlm = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID))
                .type(KbaseTypeEnum.LLM.name())
                .name(KbaseConsts.KB_LLM_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestLlm);

        // 初始化关键词知识库
        KbaseRequest kownledgebaseRequestKeyword = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_KEYWORD_UID))
                .type(KbaseTypeEnum.KEYWORD.name())
                .name(KbaseConsts.KB_KEYWORD_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestKeyword);

        // 初始化自动回复知识库
        KbaseRequest kownledgebaseRequestAutoReply = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_AUTOREPLY_UID))
                .type(KbaseTypeEnum.AUTOREPLY.name())
                .name(KbaseConsts.KB_AUTOREPLY_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestAutoReply);

        // 初始化快捷回复知识库
        KbaseRequest kownledgebaseRequestQuickReply = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                .type(KbaseTypeEnum.QUICKREPLY.name())
                .name(KbaseConsts.KB_QUICKREPLY_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestQuickReply);

        // 初始化敏感词/屏蔽词知识库
        KbaseRequest kownledgebaseRequestTaboo = KbaseRequest.builder()
                .uid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_TABOO_UID))
                .type(KbaseTypeEnum.TABOO.name())
                .name(KbaseConsts.KB_TABOO_NAME)
                .descriptionHtml(KbaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .orgUid(orgUid)
                .build();
        create(kownledgebaseRequestTaboo);
    }

    public LlmProviderConfigDefault getLlmProviderConfigDefault() {
        return LlmConfigUtils.getLlmProviderConfigDefault(environment);
    }

}
