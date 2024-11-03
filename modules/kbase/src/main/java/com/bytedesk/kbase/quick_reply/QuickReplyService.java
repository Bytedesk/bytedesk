/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:17:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuickReplyService extends BaseService<QuickReplyEntity, QuickReplyRequest, QuickReplyResponse> {

    private final QuickReplyRepository quickReplyRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    @Override
    public Page<QuickReplyResponse> queryByOrg(QuickReplyRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");

        Specification<QuickReplyEntity> spec = QuickReplySpecification.search(request);

        Page<QuickReplyEntity> page = quickReplyRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }
    
    @Override
    public Page<QuickReplyResponse> queryByUser(QuickReplyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    
    @Cacheable(value = "quickreply", key = "#uid", unless = "#result == null")
    @Override
    public Optional<QuickReplyEntity> findByUid(String uid) {
        return quickReplyRepository.findByUid(uid);
    }

    @Override
    public QuickReplyResponse create(QuickReplyRequest request) {

        QuickReplyEntity entity = modelMapper.map(request, QuickReplyEntity.class);
        entity.setUid(uidUtils.getCacheSerialUid());
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

    public QuickReplyExcel convertToExcel(QuickReplyResponse quickReply) {
        return modelMapper.map(quickReply, QuickReplyExcel.class);
    }

    // String categoryUid,
    public QuickReplyEntity convertExcelToQuickReply(QuickReplyExcel excel, String kbUid, String orgUid) {
        // return modelMapper.map(excel, QuickReply.class);
        QuickReplyEntity quickReply = QuickReplyEntity.builder().build();
        quickReply.setUid(uidUtils.getCacheSerialUid());
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
            categoryRequest.setType(CategoryConsts.CATEGORY_TYPE_QUICKREPLY);
            categoryRequest.setOrgUid(orgUid);
            //
            CategoryResponse categoryResponse = categoryService.create(categoryRequest);
            quickReply.setCategoryUid(categoryResponse.getUid());
        }

        quickReply.setKbUid(kbUid);
        quickReply.setOrgUid(orgUid);

        return quickReply;
    }

    //
    public void initData() {
        //
        if (quickReplyRepository.count() > 0) {
            return;
        }

        // level = platform, 不需要设置orgUid，此处设置orgUid方便超级管理员加载
        Optional<CategoryEntity> categoryContact = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryContact.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_CONTACT_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_CONTACT_CONTENT)
                    .categoryUid(categoryContact.get().getUid())
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .level(LevelEnum.PLATFORM)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            create(quickReplyRequest);
        }
        //
        Optional<CategoryEntity> categoryThanks = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryThanks.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_THANKS_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_THANKS_CONTENT)
                    .categoryUid(categoryThanks.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            create(quickReplyRequest);
        }
        //
        Optional<CategoryEntity> categoryWelcome = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryWelcome.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_WELCOME_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_WELCOME_CONTENT)
                    .categoryUid(categoryWelcome.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            create(quickReplyRequest);
        }

        Optional<CategoryEntity> categoryBye = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE,
                CategoryConsts.CATEGORY_TYPE_QUICKREPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryBye.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_BYE_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_BYE_CONTENT)
                    .categoryUid(categoryBye.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    .kbUid(BytedeskConsts.DEFAULT_KB_QUICKREPLY_UID)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.name());
            // 此处设置orgUid方便超级管理员加载
            quickReplyRequest.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
            create(quickReplyRequest);
        }

    }

}
