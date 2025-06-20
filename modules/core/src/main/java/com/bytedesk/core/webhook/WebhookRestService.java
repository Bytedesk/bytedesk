/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-06 10:54:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.webhook;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WebhookRestService extends BaseRestServiceWithExcel<WebhookEntity, WebhookRequest, WebhookResponse, WebhookExcel> {

    private final WebhookRepository webhookRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<WebhookEntity> queryByOrgEntity(WebhookRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebhookEntity> spec = WebhookSpecification.search(request);
        return webhookRepository.findAll(spec, pageable);
    }

    @Override
    public Page<WebhookResponse> queryByOrg(WebhookRequest request) {
        Page<WebhookEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WebhookResponse> queryByUser(WebhookRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public WebhookResponse queryByUid(WebhookRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "webhook", key = "#uid", unless="#result==null")
    @Override
    public Optional<WebhookEntity> findByUid(String uid) {
        return webhookRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return webhookRepository.existsByUid(uid);
    }

    @Override
    public WebhookResponse create(WebhookRequest request) {
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
        WebhookEntity entity = modelMapper.map(request, WebhookEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WebhookEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webhook failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WebhookResponse update(WebhookRequest request) {
        Optional<WebhookEntity> optional = webhookRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WebhookEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WebhookEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webhook failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Webhook not found");
        }
    }

    @Override
    protected WebhookEntity doSave(WebhookEntity entity) {
        return webhookRepository.save(entity);
    }

    @Override
    public WebhookEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WebhookEntity entity) {
        try {
            Optional<WebhookEntity> latest = webhookRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebhookEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return webhookRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WebhookEntity> optional = webhookRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // webhookRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Webhook not found");
        }
    }

    @Override
    public void delete(WebhookRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WebhookResponse convertToResponse(WebhookEntity entity) {
        return modelMapper.map(entity, WebhookResponse.class);
    }

    @Override
    public WebhookExcel convertToExcel(WebhookEntity entity) {
        return modelMapper.map(entity, WebhookExcel.class);
    }
    
    // public void initWebhooks(String orgUid) {
    //     // log.info("initThreadWebhook");
    //     for (String webhook : WebhookInitData.getAllWebhooks()) {
    //         WebhookRequest webhookRequest = WebhookRequest.builder()
    //                 .uid(Utils.formatUid(orgUid, webhook))
    //                 .name(webhook)
    //                 .order(0)
    //                 .type(WebhookTypeEnum.THREAD.name())
    //                 .level(LevelEnum.ORGANIZATION.name())
    //                 .platform(BytedeskConsts.PLATFORM_BYTEDESK)
    //                 .orgUid(orgUid)
    //                 .build();
    //         create(webhookRequest);
    //     }
    // }
    
}
