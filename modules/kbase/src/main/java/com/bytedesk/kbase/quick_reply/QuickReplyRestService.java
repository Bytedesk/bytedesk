/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-10 12:26:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuickReplyRestService extends BaseRestServiceWithExcel<QuickReplyEntity, QuickReplyRequest, QuickReplyResponse, QuickReplyExcel> {

    private final QuickReplyRepository quickReplyRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryRestService categoryService;

    private final KbaseRestService knowledgebaseService;

    private final AuthService authService;

    @Override
    public Page<QuickReplyEntity> queryByOrgEntity(QuickReplyRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QuickReplyEntity> spec = QuickReplySpecification.search(request);
        return quickReplyRepository.findAll(spec, pageable);
    }

    @Override
    public Page<QuickReplyResponse> queryByOrg(QuickReplyRequest request) {
        Page<QuickReplyEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }
    
    @Override
    public Page<QuickReplyResponse> queryByUser(QuickReplyRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "quickreply", key = "#request.agentUid", unless = "#result == null")
    public List<QuickReplyResponseAgent> query(QuickReplyRequest request) {

        List<QuickReplyResponseAgent> quickReplyList = new ArrayList<QuickReplyResponseAgent>();

        // 当前用户快捷回复/常用语
        List<KbaseEntity> agentKbase = knowledgebaseService.findByLevelAndTypeAndAgentUid(LevelEnum.AGENT,
                KbaseTypeEnum.QUICKREPLY,
                request.getAgentUid());
        quickReplyList.addAll(transformToQuickReplyResponseAgent(agentKbase));

        // 当前组织快捷回复/常用语
        List<KbaseEntity> orgKbase = knowledgebaseService.findByLevelAndTypeAndOrgUid(LevelEnum.ORGANIZATION,
                KbaseTypeEnum.QUICKREPLY,
                request.getOrgUid());
        quickReplyList.addAll(transformToQuickReplyResponseAgent(orgKbase));

        // 平台快捷回复/常用语
        List<KbaseEntity> platformKbase = knowledgebaseService.findByLevelAndType(LevelEnum.PLATFORM,
                KbaseTypeEnum.QUICKREPLY);
        quickReplyList.addAll(transformToQuickReplyResponseAgent(platformKbase));

        return quickReplyList;
    }

    
    @Cacheable(value = "quickreply", key = "#uid", unless = "#result == null")
    @Override
    public Optional<QuickReplyEntity> findByUid(String uid) {
        return quickReplyRepository.findByUid(uid);
    }

    @Override
    public QuickReplyResponse create(QuickReplyRequest request) {
        // 判断uid是否已经存在
        if (StringUtils.hasText(request.getUid()) && findByUid(request.getUid()).isPresent()) {
            return null;
        }

        QuickReplyEntity entity = modelMapper.map(request, QuickReplyEntity.class);
        if (StringUtils.hasText(request.getUid())) {
            entity.setUid(request.getUid());
        } else {
            entity.setUid(uidUtils.getUid());
        }
        entity.setType(MessageTypeEnum.fromValue(request.getType()).name());

        return convertToResponse(save(entity));
    }

    @Override
    public QuickReplyResponse update(QuickReplyRequest request) {

        Optional<QuickReplyEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            QuickReplyEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setType(MessageTypeEnum.fromValue(request.getType()).name());

            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("quick_reply not found");
        }
    }

    @Override
    public QuickReplyEntity save(QuickReplyEntity entity) {
        try {
            return quickReplyRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    public void save(List<QuickReplyEntity> entities) {
        quickReplyRepository.saveAll(entities);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QuickReplyEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QuickReplyRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QuickReplyEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QuickReplyResponse convertToResponse(QuickReplyEntity entity) {
        return modelMapper.map(entity, QuickReplyResponse.class);
    }

    @Override
    public QuickReplyExcel convertToExcel(QuickReplyEntity quickReply) {
        // categoryUid
        Optional<CategoryEntity> categoryOptional = categoryService.findByUid(quickReply.getCategoryUid());
        QuickReplyExcel quickReplyExcel = modelMapper.map(quickReply, QuickReplyExcel.class);
        if (categoryOptional.isPresent()) {
            quickReplyExcel.setCategory(categoryOptional.get().getName());
        }
        return quickReplyExcel;
    }

    private List<QuickReplyResponseAgent> transformToQuickReplyResponseAgent(List<KbaseEntity> kbList) {

        List<QuickReplyResponseAgent> quickReplyList = new ArrayList<QuickReplyResponseAgent>();
        //
        Iterator<KbaseEntity> kbPlatformIterator = kbList.iterator();
        while (kbPlatformIterator.hasNext()) {
            KbaseEntity kb = kbPlatformIterator.next();
            //
            QuickReplyResponseAgent quickReplyKb = QuickReplyResponseAgent.builder()
                    .key(kb.getUid())
                    .title(kb.getName())
                    .content(kb.getDescriptionHtml())
                    .type(QuickReplyTypeEnum.KB.name())
                    .level(kb.getLevel())
                    .platform(kb.getPlatform())
                    .build();
            //
            List<CategoryEntity> categoryList = categoryService.findByKbUid(kb.getUid());
            Iterator<CategoryEntity> iterator = categoryList.iterator();
            while (iterator.hasNext()) {
                CategoryEntity category = iterator.next();
                //
                QuickReplyResponseAgent quickReplyCategory = QuickReplyResponseAgent.builder()
                        .key(category.getUid())
                        .title(category.getName())
                        .type(QuickReplyTypeEnum.CATEGORY.name())
                        .build();
                //
                List<QuickReplyEntity> quickReplies = quickReplyRepository.findByCategoryUid(category.getUid());
                Iterator<QuickReplyEntity> quickRepliesIterator = quickReplies.iterator();
                while (quickRepliesIterator.hasNext()) {
                    QuickReplyEntity quickReply = quickRepliesIterator.next();
                    //
                    QuickReplyResponseAgent quickReplyAgent = QuickReplyResponseAgent.builder()
                            .key(quickReply.getUid())
                            .title(quickReply.getTitle())
                            .content(quickReply.getContent())
                            .type(quickReply.getType())
                            .build();
                    quickReplyCategory.getChildren().add(quickReplyAgent);
                }
                quickReplyKb.getChildren().add(quickReplyCategory);

            }
            quickReplyList.add(quickReplyKb);
        }
        //
        return quickReplyList;
    }

    // 快捷回复分类
    public void initQuickReplyCategory(String orgUid) {
        // 
        String quickReplyCategoryContactUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT);
        // 快捷回复-询问联系方式
        CategoryRequest categoryContact = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT)
                .order(0)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                .build();
        categoryContact.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryContact.setUid(quickReplyCategoryContactUid);
        // 此处设置orgUid方便超级管理员加载
        categoryContact.setOrgUid(orgUid);
        categoryService.create(categoryContact);

        // 快捷回复-感谢
        String quickReplyCategoryThanksUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS);
        CategoryRequest categoryThanks = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS)
                .order(1)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                .build();
        categoryThanks.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryThanks.setUid(quickReplyCategoryThanksUid);
        // 此处设置orgUid方便超级管理员加载
        categoryThanks.setOrgUid(orgUid);
        categoryService.create(categoryThanks);

        // 快捷回复-问候
        String quickReplyCategoryWelcomeUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME);
        CategoryRequest categoryWelcome = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME)
                .order(2)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                .build();
        categoryWelcome.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryWelcome.setUid(quickReplyCategoryWelcomeUid);
        // 此处设置orgUid方便超级管理员加载
        categoryWelcome.setOrgUid(orgUid);
        categoryService.create(categoryWelcome);

        // 快捷回复-告别
        String quickReplyCategoryByeUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE);
        CategoryRequest categoryBye = CategoryRequest.builder()
                .name(I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE)
                .order(3)
                .level(LevelEnum.ORGANIZATION.name())
                .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                .build();
        categoryBye.setType(CategoryTypeEnum.QUICKREPLY.name());
        categoryBye.setUid(quickReplyCategoryByeUid);
        // 此处设置orgUid方便超级管理员加载
        categoryBye.setOrgUid(orgUid);
        categoryService.create(categoryBye);
    }

    public void initQuickReply(String orgUid) {

        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        String quickReplyCategoryContactUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT);
        Optional<CategoryEntity> categoryContact = categoryService.findByUid(quickReplyCategoryContactUid);
        if (categoryContact.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_CONTACT_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_CONTACT_CONTENT)
                    .categoryUid(categoryContact.get().getUid())
                    .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                    .level(LevelEnum.ORGANIZATION.name())
                    .build();
            quickReplyRequest.setUid(Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CONTACT_TITLE));
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(orgUid);
            create(quickReplyRequest);
        }
        //
        String quickReplyCategoryThanksUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS);
        Optional<CategoryEntity> categoryThanks = categoryService.findByUid(quickReplyCategoryThanksUid);
        if (categoryThanks.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_THANKS_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_THANKS_CONTENT)
                    .categoryUid(categoryThanks.get().getUid())
                    .level(LevelEnum.ORGANIZATION.name())
                    .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                    .build();
            quickReplyRequest.setUid(Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_THANKS_TITLE));
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(orgUid);
            create(quickReplyRequest);
        }
        //
        String quickReplyCategoryWelcomeUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME);
        Optional<CategoryEntity> categoryWelcome = categoryService.findByUid(quickReplyCategoryWelcomeUid);
        if (categoryWelcome.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_WELCOME_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_WELCOME_CONTENT)
                    .categoryUid(categoryWelcome.get().getUid())
                    .level(LevelEnum.ORGANIZATION.name())
                    .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                    .build();
            quickReplyRequest.setUid(Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_WELCOME_TITLE));
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(orgUid);
            create(quickReplyRequest);
        }

        String quickReplyCategoryByeUid = Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE);
        Optional<CategoryEntity> categoryBye = categoryService.findByUid(quickReplyCategoryByeUid);
        if (categoryBye.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_BYE_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_BYE_CONTENT)
                    .categoryUid(categoryBye.get().getUid())
                    .level(LevelEnum.ORGANIZATION.name())
                    .kbUid(Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID))
                    .build();
            quickReplyRequest.setUid(Utils.formatUid(orgUid, I18Consts.I18N_QUICK_REPLY_BYE_TITLE));
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(orgUid);
            create(quickReplyRequest);
        }
    }

    // String categoryUid,
    public QuickReplyEntity convertExcelToQuickReply(QuickReplyExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, QuickReply.class);
        QuickReplyEntity quickReply = QuickReplyEntity.builder().build();
        quickReply.setUid(uidUtils.getUid());
        quickReply.setTitle(excel.getTitle());
        quickReply.setContent(excel.getContent());
        // quickReply.setType(MessageTypeEnum.TEXT); // TODO: 根据实际类型设置
        quickReply.setType(MessageTypeEnum.fromValue(excel.getType()).name());
        //
        // quickReply.setCategoryUid(categoryUid);
        Optional<CategoryEntity> categoryOptional = categoryService.findByNameAndKbUid(excel.getCategory(), kbUid);
        if (categoryOptional.isPresent()) {
            quickReply.setCategoryUid(categoryOptional.get().getUid());
        } else {
            // create category
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .name(excel.getCategory())
                    .kbUid(kbUid)
                    .build();
            categoryRequest.setType(CategoryTypeEnum.QUICKREPLY.name());
            categoryRequest.setOrgUid(orgUid);
            //
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            quickReply.setCategoryUid(categoryResponse.getUid());
        }

        quickReply.setKbUid(kbUid);
        quickReply.setOrgUid(orgUid);

        return quickReply;
    }

}
