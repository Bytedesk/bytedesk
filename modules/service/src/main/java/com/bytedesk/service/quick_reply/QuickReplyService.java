/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-29 20:04:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.quick_reply;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.category.Category;
import com.bytedesk.core.category.CategoryConsts;
import com.bytedesk.core.category.CategoryService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuickReplyService extends BaseService<QuickReply, QuickReplyRequest, QuickReplyResponse> {

    private final QuickReplyRepository quickReplyRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final CategoryService categoryService;

    @Override
    public Page<QuickReplyResponse> queryByOrg(QuickReplyRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        
        Specification<QuickReply> spec = QuickReplySpecification.search(request);

        Page<QuickReply> page = quickReplyRepository.findAll(spec, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QuickReplyResponse> queryByUser(QuickReplyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<QuickReply> findByUid(String uid) {
        return quickReplyRepository.findByUid(uid);
    }

    @Override
    public QuickReplyResponse create(QuickReplyRequest request) {
        
        QuickReply entity = modelMapper.map(request, QuickReply.class);
        entity.setUid(uidUtils.getCacheSerialUid());
        entity.setType(MessageTypeEnum.fromValue(request.getType()));
        // 
        // category
        Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
        if (categoryOptional.isPresent()) {
            entity.setCategory(categoryOptional.get());
        }

        return convertToResponse(save(entity));
    }

    @Override
    public QuickReplyResponse update(QuickReplyRequest request) {
        
        Optional<QuickReply> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            QuickReply entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setContent(request.getContent());
            entity.setType(MessageTypeEnum.fromValue(request.getType()));

            // category
            Optional<Category> categoryOptional = categoryService.findByUid(request.getCategoryUid());
            if (categoryOptional.isPresent()) {
                entity.setCategory(categoryOptional.get());
            }

            return convertToResponse(save(entity));
        } else {
            throw new RuntimeException("quick_reply not found");
        }
    }

    @Override
    public QuickReply save(QuickReply entity) {
        try {
            return quickReplyRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<QuickReply> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QuickReply entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QuickReply entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public QuickReplyResponse convertToResponse(QuickReply entity) {
        return modelMapper.map(entity, QuickReplyResponse.class);
    }

    // 
    public void initData() {
        // 
        if (quickReplyRepository.count() > 0) {
            return;
        }
        // 
        Optional<Category> categoryContact = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_CONTACT,
                CategoryConsts.CATEGORY_TYPE_QUICK_REPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryContact.isPresent()) {
            // 
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_CONTACT_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_CONTACT_CONTENT)
                    .categoryUid(categoryContact.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    // .orgUid(orgUid)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.getValue());
            create(quickReplyRequest);
        }
        // 
        Optional<Category> categoryThanks = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_THANKS,
                CategoryConsts.CATEGORY_TYPE_QUICK_REPLY, 
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryThanks.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_THANKS_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_THANKS_CONTENT)
                    .categoryUid(categoryThanks.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    // .orgUid(orgUid)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.getValue());
            create(quickReplyRequest);
        }
        // 
        Optional<Category> categoryWelcome = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_WELCOME,
                CategoryConsts.CATEGORY_TYPE_QUICK_REPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryWelcome.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_WELCOME_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_WELCOME_CONTENT)
                    .categoryUid(categoryWelcome.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    // .orgUid(orgUid)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.getValue());
            create(quickReplyRequest);
        }
        
        Optional<Category> categoryBye = categoryService.findByNameAndTypeAndLevelAndPlatform(
                I18Consts.I18N_QUICK_REPLY_CATEGORY_BYE,
                CategoryConsts.CATEGORY_TYPE_QUICK_REPLY,
                LevelEnum.PLATFORM,
                PlatformEnum.BYTEDESK);
        if (categoryBye.isPresent()) {
            //
            QuickReplyRequest quickReplyRequest = QuickReplyRequest.builder()
                    .title(I18Consts.I18N_QUICK_REPLY_BYE_TITLE)
                    .content(I18Consts.I18N_QUICK_REPLY_BYE_CONTENT)
                    .categoryUid(categoryBye.get().getUid())
                    .level(LevelEnum.PLATFORM)
                    // .orgUid(orgUid)
                    .build();
            quickReplyRequest.setType(MessageTypeEnum.TEXT.getValue());
            create(quickReplyRequest);
        }

    }
    
}
