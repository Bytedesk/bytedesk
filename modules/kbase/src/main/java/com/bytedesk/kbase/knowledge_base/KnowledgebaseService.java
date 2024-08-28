/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-27 22:04:31
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KnowledgebaseService extends BaseService<Knowledgebase, KnowledgebaseRequest, KnowledgebaseResponse> {

    private final KnowledgebaseRepository knowledgebaseRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<KnowledgebaseResponse> queryByOrg(KnowledgebaseRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<Knowledgebase> spec = KnowledgebaseSpecification.search(request);

        Page<Knowledgebase> page = knowledgebaseRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<KnowledgebaseResponse> queryByUser(KnowledgebaseRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Knowledgebase> findByUid(String uid) {
        return knowledgebaseRepository.findByUid(uid);
    }

    @Override
    public KnowledgebaseResponse create(KnowledgebaseRequest request) {

        Knowledgebase entity = Knowledgebase.builder().build();
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getCacheSerialUid());
        }
        entity.setName(request.getName());
        entity.setType(KnowledgebaseTypeEnum.fromValue(request.getType()).name());
        entity.setDescriptionHtml(request.getDescriptionHtml());
        entity.setEmbedding(request.getEmbedding());
        entity.setLanguage(request.getLanguage());
        entity.setLevel(request.getLevel());
        entity.setOrgUid(request.getOrgUid());
        entity.setAgentUid(request.getAgentUid());
        //
        Knowledgebase savedKb = save(entity);
        if (savedKb == null) {
            throw new RuntimeException("knowledge_base not saved");
        }

        return convertToResponse(savedKb);
    }

    @Override
    public KnowledgebaseResponse update(KnowledgebaseRequest request) {

        Optional<Knowledgebase> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            Knowledgebase entity = optional.get();
            entity.setName(request.getName());
            // entity.setType(KownledgebaseTypeEnum.fromValue(request.getType()));
            entity.setDescriptionHtml(request.getDescriptionHtml());
            entity.setEmbedding(request.getEmbedding());
            entity.setLanguage(request.getLanguage());
            //
            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("knowledge_base not found");
        }
    }


    public List<Knowledgebase> findByLevelAndType(LevelEnum level, KnowledgebaseTypeEnum type) {
        return knowledgebaseRepository.findByLevelAndTypeAndDeleted(level.name(), type.name(), false);
    }

    public List<Knowledgebase> findByLevelAndTypeAndOrgUid(LevelEnum level, KnowledgebaseTypeEnum type, String orgUid) {
        return knowledgebaseRepository.findByLevelAndTypeAndOrgUidAndDeleted(level.name(), type.name(), orgUid, false);
    }

    public List<Knowledgebase> findByLevelAndTypeAndAgentUid(LevelEnum level, KnowledgebaseTypeEnum type, String agentUid) {
        return knowledgebaseRepository.findByLevelAndTypeAndAgentUidAndDeleted(level.name(), type.name(), agentUid, false);
    }

    @Override
    public Knowledgebase save(Knowledgebase entity) {
        try {
            return knowledgebaseRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<Knowledgebase> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(Knowledgebase entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            Knowledgebase entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public KnowledgebaseResponse convertToResponse(Knowledgebase entity) {
        return modelMapper.map(entity, KnowledgebaseResponse.class);
    }

    public void initData() {

        if (knowledgebaseRepository.count() > 0) {
            return;
        }
        //
        KnowledgebaseRequest kownledgebaseRequeqstQuickReplyPlatform = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_PLATFORM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .level(LevelEnum.PLATFORM.name())
                .build();
        kownledgebaseRequeqstQuickReplyPlatform.setUid(BdConstants.DEFAULT_KB_UID);
        kownledgebaseRequeqstQuickReplyPlatform.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        // 方便超级管理员加载，避免重新写一个接口拉取
        kownledgebaseRequeqstQuickReplyPlatform.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequeqstQuickReplyPlatform);
        
        ////////////////////////////////////////////////////////////
        //
        KnowledgebaseRequest kownledgebaseRequestHelpdoc = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_HELPDOC_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestHelpdoc.setType(KnowledgebaseTypeEnum.HELPDOC.name());
        kownledgebaseRequestHelpdoc.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequestHelpdoc);
        //
        KnowledgebaseRequest kownledgebaseRequestLlm = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_LLM_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestLlm.setType(KnowledgebaseTypeEnum.LLM.name());
        kownledgebaseRequestLlm.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequestLlm);
        //
        KnowledgebaseRequest kownledgebaseRequestKeyword = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_KEYWORD_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestKeyword.setType(KnowledgebaseTypeEnum.KEYWORD.name());
        kownledgebaseRequestKeyword.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequestKeyword);
        // 
        KnowledgebaseRequest kownledgebaseRequeqstFaq = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_FAQ_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequeqstFaq.setType(KnowledgebaseTypeEnum.FAQ.name());
        kownledgebaseRequeqstFaq.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequeqstFaq);
        //
        KnowledgebaseRequest kownledgebaseRequeqstAutoReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_AUTOREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequeqstAutoReply.setType(KnowledgebaseTypeEnum.AUTOREPLY.name());
        kownledgebaseRequeqstAutoReply.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequeqstAutoReply);
        //
        KnowledgebaseRequest kownledgebaseRequeqstQuickReply = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_QUICKREPLY_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequeqstQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
        kownledgebaseRequeqstQuickReply.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequeqstQuickReply);
        //
        KnowledgebaseRequest kownledgebaseRequestTaboo = KnowledgebaseRequest.builder()
                .name(KnowledgebaseConsts.KB_TABOO_NAME)
                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                .language(LanguageEnum.ZH_CN.name())
                .build();
        kownledgebaseRequestTaboo.setType(KnowledgebaseTypeEnum.TABOO.name());
        kownledgebaseRequestTaboo.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        create(kownledgebaseRequestTaboo);
        // 
        

    }

}
