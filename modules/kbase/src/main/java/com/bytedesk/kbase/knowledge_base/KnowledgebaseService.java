/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-30 15:57:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.List;
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
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.article.ArticleRequest;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.ArticleService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KnowledgebaseService extends BaseService<KnowledgebaseEntity, KnowledgebaseRequest, KnowledgebaseResponse> {

    private final KnowledgebaseRepository knowledgebaseRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    private final ArticleService articleService;

    @Override
    public Page<KnowledgebaseResponse> queryByOrg(KnowledgebaseRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<KnowledgebaseEntity> spec = KnowledgebaseSpecification.search(request);

        Page<KnowledgebaseEntity> page = knowledgebaseRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<KnowledgebaseResponse> queryByUser(KnowledgebaseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "kb", key = "#uid", unless = "#result==null")
    @Override
    public Optional<KnowledgebaseEntity> findByUid(String uid) {
        return knowledgebaseRepository.findByUid(uid);
    }

    @Override
    public KnowledgebaseResponse create(KnowledgebaseRequest request) {

        KnowledgebaseEntity entity = KnowledgebaseEntity.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getCacheSerialUid());
        }
        entity.setName(request.getName());
        entity.setType(KnowledgebaseTypeEnum.fromValue(request.getType()).name());
        entity.setHeadline(request.getHeadline());
        entity.setDescriptionHtml(request.getDescriptionHtml());
        entity.setFooterHtml(request.getFooterHtml());
        entity.setEmbedding(request.getEmbedding());
        entity.setLanguage(request.getLanguage());
        entity.setLevel(request.getLevel());
        entity.setOrgUid(request.getOrgUid());
        entity.setAgentUid(request.getAgentUid());
        //
        KnowledgebaseEntity savedKb = save(entity);
        if (savedKb == null) {
            throw new RuntimeException("knowledge_base not saved");
        }

        return convertToResponse(savedKb);
    }

    @Override
    public KnowledgebaseResponse update(KnowledgebaseRequest request) {

        Optional<KnowledgebaseEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            KnowledgebaseEntity entity = optional.get();
            entity.setName(request.getName());
            // entity.setType(KownledgebaseTypeEnum.fromValue(request.getType()));
            entity.setHeadline(request.getHeadline());
            entity.setDescriptionHtml(request.getDescriptionHtml());
            entity.setFooterHtml(request.getFooterHtml());
            entity.setEmbedding(request.getEmbedding());
            entity.setLanguage(request.getLanguage());
            //
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("knowledge_base not found");
        }
    }

    public List<KnowledgebaseEntity> findByLevelAndType(LevelEnum level, KnowledgebaseTypeEnum type) {
        return knowledgebaseRepository.findByLevelAndTypeAndDeleted(level.name(), type.name(), false);
    }

    public List<KnowledgebaseEntity> findByLevelAndTypeAndOrgUid(LevelEnum level, KnowledgebaseTypeEnum type, String orgUid) {
        return knowledgebaseRepository.findByLevelAndTypeAndOrgUidAndDeleted(level.name(), type.name(), orgUid, false);
    }

    public List<KnowledgebaseEntity> findByLevelAndTypeAndAgentUid(LevelEnum level, KnowledgebaseTypeEnum type, String agentUid) {
        return knowledgebaseRepository.findByLevelAndTypeAndAgentUidAndDeleted(level.name(), type.name(), agentUid, false);
    }

    @Override
    public KnowledgebaseEntity save(KnowledgebaseEntity entity) {
        try {
            return knowledgebaseRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<KnowledgebaseEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(KnowledgebaseRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            KnowledgebaseEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public KnowledgebaseResponse convertToResponse(KnowledgebaseEntity entity) {
        return modelMapper.map(entity, KnowledgebaseResponse.class);
    }

    public Page<CategoryResponse> getCategories(KnowledgebaseEntity knowledgebaseEntity) {
        // 
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setPageNumber(0);
        categoryRequest.setPageSize(50);
        categoryRequest.setType(KnowledgebaseTypeEnum.HELPCENTER.name());
        categoryRequest.setKbUid(knowledgebaseEntity.getUid());
        categoryRequest.setOrgUid(knowledgebaseEntity.getOrgUid());
        // 
        return categoryService.queryByOrg(categoryRequest);
    }

    public Page<ArticleResponse> getArticles(KnowledgebaseEntity knowledgebaseEntity) {
        // 
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setPageNumber(0);
        articleRequest.setPageSize(50);
        articleRequest.setKbUid(knowledgebaseEntity.getUid());
        articleRequest.setOrgUid(knowledgebaseEntity.getOrgUid());
        // 
        return articleService.queryByOrg(articleRequest);
    }
    
    public Page<ArticleResponse> getArticlesByCategory(KnowledgebaseEntity knowledgebaseEntity, String categoryUid) {
        // 
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setPageNumber(0);
        articleRequest.setPageSize(50);
        articleRequest.setCategoryUid(categoryUid);
        articleRequest.setKbUid(knowledgebaseEntity.getUid());
        articleRequest.setOrgUid(knowledgebaseEntity.getOrgUid());
        // 
        return articleService.queryByOrg(articleRequest);
    }

    public void initData() {

        if (knowledgebaseRepository.count() > 0) {
            return;
        }
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        KnowledgebaseRequest kownledgebaseRequestQuickReplyPlatform = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_PLATFORM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .level(LevelEnum.PLATFORM.name())
                .build();
        kownledgebaseRequestQuickReplyPlatform.setUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID);
        kownledgebaseRequestQuickReplyPlatform.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        // 方便超级管理员加载，避免重新写一个接口拉取
        kownledgebaseRequestQuickReplyPlatform.setOrgUid(orgUid);
        create(kownledgebaseRequestQuickReplyPlatform);
        
        //
        KnowledgebaseRequest kownledgebaseRequestHelpdoc = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_HELPCENTER_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestHelpdoc.setUid(BytedeskConsts.DEFAULT_KB_HELPCENTER_UID);
        kownledgebaseRequestHelpdoc.setType(KnowledgebaseTypeEnum.HELPCENTER.name());
        kownledgebaseRequestHelpdoc.setOrgUid(orgUid);
        create(kownledgebaseRequestHelpdoc);
        //
        KnowledgebaseRequest kownledgebaseRequestLlm = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_LLM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestLlm.setType(KnowledgebaseTypeEnum.LLM.name());
        kownledgebaseRequestLlm.setOrgUid(orgUid);
        create(kownledgebaseRequestLlm);
        //
        KnowledgebaseRequest kownledgebaseRequestKeyword = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_KEYWORD_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestKeyword.setType(KnowledgebaseTypeEnum.KEYWORD.name());
        kownledgebaseRequestKeyword.setOrgUid(orgUid);
        create(kownledgebaseRequestKeyword);
        // 
        KnowledgebaseRequest kownledgebaseRequestFaq = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_FAQ_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestFaq.setType(KnowledgebaseTypeEnum.FAQ.name());
        kownledgebaseRequestFaq.setOrgUid(orgUid);
        create(kownledgebaseRequestFaq);
        //
        KnowledgebaseRequest kownledgebaseRequestAutoReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_AUTOREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestAutoReply.setType(KnowledgebaseTypeEnum.AUTOREPLY.name());
        kownledgebaseRequestAutoReply.setOrgUid(orgUid);
        create(kownledgebaseRequestAutoReply);
        //
        KnowledgebaseRequest kownledgebaseRequestQuickReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_QUICKREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        kownledgebaseRequestQuickReply.setOrgUid(orgUid);
        create(kownledgebaseRequestQuickReply);
        //
        KnowledgebaseRequest kownledgebaseRequestTaboo = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_TABOO_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestTaboo.setType(KnowledgebaseTypeEnum.TABOO.name());
        kownledgebaseRequestTaboo.setOrgUid(orgUid);
        create(kownledgebaseRequestTaboo);
        // 
        

    }

}
