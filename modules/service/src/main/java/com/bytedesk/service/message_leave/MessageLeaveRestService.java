/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 09:28:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageLeaveRestService extends BaseRestService<MessageLeaveEntity, MessageLeaveRequest, MessageLeaveResponse> {

    private final MessageLeaveRepository messageLeaveRepository;

    private final UidUtils uidUtils;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final CategoryRestService categoryService;

    @Override
    public Page<MessageLeaveResponse> queryByOrg(MessageLeaveRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageLeaveEntity> spec = MessageLeaveSpecification.search(request);
        Page<MessageLeaveEntity> page = messageLeaveRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageLeaveResponse> queryByUser(MessageLeaveRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("User should login first.");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Cacheable(value = "messageLeave", key = "#uid", unless = "#result == null")
    @Override
    public Optional<MessageLeaveEntity> findByUid(String uid) {
        return messageLeaveRepository.findByUid(uid);
    }

    @Override
    public MessageLeaveResponse create(MessageLeaveRequest request) {
        log.info("request {}", request);

        MessageLeaveEntity messageLeave = modelMapper.map(request, MessageLeaveEntity.class);
        messageLeave.setUid(uidUtils.getUid());
        messageLeave.setStatus(MessageLeaveStatusEnum.PENDING.name());
        //
        // 保存留言
        MessageLeaveEntity savedMessageLeave = save(messageLeave);
        if (savedMessageLeave == null) {
            throw new RuntimeException("MessageLeave not saved");
        }

        return convertToResponse(savedMessageLeave);
    }

    @Override
    public MessageLeaveResponse update(MessageLeaveRequest request) {
        
        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(request.getUid());
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setStatus(request.getStatus());

            MessageLeaveEntity updateMessageLeave = save(messageLeave);
            if (updateMessageLeave == null) {
                throw new RuntimeException("MessageLeave not updated");
            }
            return convertToResponse(updateMessageLeave);
        }
        throw new RuntimeException("MessageLeave not found");
    }

    @Override
    public MessageLeaveEntity save(MessageLeaveEntity entity) {
        try {
            return messageLeaveRepository.save(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageLeaveEntity> messageLeaveOptional = findByUid(uid);
        if (messageLeaveOptional.isPresent()) {
            MessageLeaveEntity messageLeave = messageLeaveOptional.get();
            messageLeave.setDeleted(true);
            save(messageLeave);
        }
    }

    @Override
    public void delete(MessageLeaveRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            MessageLeaveEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public MessageLeaveResponse convertToResponse(MessageLeaveEntity entity) {
        return modelMapper.map(entity, MessageLeaveResponse.class);
    }

    public Page<MessageLeaveEntity> queryByOrgExcel(MessageLeaveRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageLeaveEntity> spec = MessageLeaveSpecification.search(request);
        return messageLeaveRepository.findAll(spec, pageable);
    }

    public MessageLeaveExcel convertToExcel(MessageLeaveEntity entity) {
        MessageLeaveExcel excel = modelMapper.map(entity, MessageLeaveExcel.class);
        if (StringUtils.hasText(entity.getCategoryUid())) {
            Optional<CategoryEntity> categoryOptional = categoryService.findByUid(entity.getCategoryUid());
            if (categoryOptional.isPresent()) {
                excel.setCategory(categoryOptional.get().getName());
            } else {
                excel.setCategory("未分类");
            }
        } else {
            excel.setCategory("未分类");
        }
        return excel;
    }

}
