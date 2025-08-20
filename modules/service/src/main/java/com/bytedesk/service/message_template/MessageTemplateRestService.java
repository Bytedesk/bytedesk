/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 11:47:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_template;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MessageTemplateRestService extends BaseRestServiceWithExcel<MessageTemplateEntity, MessageTemplateRequest, MessageTemplateResponse, MessageTemplateExcel> {

    private final MessageTemplateRepository templateRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<MessageTemplateEntity> queryByOrgEntity(MessageTemplateRequest request) {
        Pageable pageable = request.getPageable();
        Specification<MessageTemplateEntity> spec = MessageTemplateSpecification.search(request);
        return templateRepository.findAll(spec, pageable);
    }

    @Override
    public Page<MessageTemplateResponse> queryByOrg(MessageTemplateRequest request) {
        Page<MessageTemplateEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MessageTemplateResponse> queryByUser(MessageTemplateRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public MessageTemplateResponse queryByUid(MessageTemplateRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "template", key = "#uid", unless="#result==null")
    @Override
    public Optional<MessageTemplateEntity> findByUid(String uid) {
        return templateRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return templateRepository.existsByUid(uid);
    }

    @Override
    public MessageTemplateResponse create(MessageTemplateRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MessageTemplateEntity entity = modelMapper.map(request, MessageTemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MessageTemplateEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create template failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public MessageTemplateResponse update(MessageTemplateRequest request) {
        Optional<MessageTemplateEntity> optional = templateRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MessageTemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MessageTemplateEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update template failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("MessageTemplate not found");
        }
    }

    @Override
    protected MessageTemplateEntity doSave(MessageTemplateEntity entity) {
        return templateRepository.save(entity);
    }

    @Override
    public MessageTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MessageTemplateEntity entity) {
        try {
            Optional<MessageTemplateEntity> latest = templateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MessageTemplateEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return templateRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<MessageTemplateEntity> optional = templateRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // templateRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("MessageTemplate not found");
        }
    }

    @Override
    public void delete(MessageTemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MessageTemplateResponse convertToResponse(MessageTemplateEntity entity) {
        return modelMapper.map(entity, MessageTemplateResponse.class);
    }

    @Override
    public MessageTemplateExcel convertToExcel(MessageTemplateEntity entity) {
        return modelMapper.map(entity, MessageTemplateExcel.class);
    }
    
    public void initMessageTemplates(String orgUid) {
        // log.info("initThreadMessageTemplate");
        // for (String template : MessageTemplateInitData.getAllMessageTemplates()) {
        //     MessageTemplateRequest templateRequest = MessageTemplateRequest.builder()
        //             .uid(Utils.formatUid(orgUid, template))
        //             .name(template)
        //             .order(0)
        //             .type(MessageTemplateTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(templateRequest);
        // }
    }
    
}
