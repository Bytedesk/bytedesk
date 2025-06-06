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
package com.bytedesk.core.webhook_received;

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
public class WebhookReceivedRestService extends BaseRestServiceWithExcel<WebhookReceivedEntity, WebhookReceivedRequest, WebhookReceivedResponse, WebhookReceivedExcel> {

    private final WebhookReceivedRepository webhookRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<WebhookReceivedEntity> queryByOrgEntity(WebhookReceivedRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WebhookReceivedEntity> spec = WebhookReceivedSpecification.search(request);
        return webhookRepository.findAll(spec, pageable);
    }

    @Override
    public Page<WebhookReceivedResponse> queryByOrg(WebhookReceivedRequest request) {
        Page<WebhookReceivedEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<WebhookReceivedResponse> queryByUser(WebhookReceivedRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public WebhookReceivedResponse queryByUid(WebhookReceivedRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "webhook", key = "#uid", unless="#result==null")
    @Override
    public Optional<WebhookReceivedEntity> findByUid(String uid) {
        return webhookRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return webhookRepository.existsByUid(uid);
    }

    @Override
    public WebhookReceivedResponse create(WebhookReceivedRequest request) {
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
        WebhookReceivedEntity entity = modelMapper.map(request, WebhookReceivedEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WebhookReceivedEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create webhook failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public WebhookReceivedResponse update(WebhookReceivedRequest request) {
        Optional<WebhookReceivedEntity> optional = webhookRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WebhookReceivedEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WebhookReceivedEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update webhook failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WebhookReceived not found");
        }
    }

    @Override
    protected WebhookReceivedEntity doSave(WebhookReceivedEntity entity) {
        return webhookRepository.save(entity);
    }

    @Override
    public WebhookReceivedEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WebhookReceivedEntity entity) {
        try {
            Optional<WebhookReceivedEntity> latest = webhookRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WebhookReceivedEntity latestEntity = latest.get();
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
        Optional<WebhookReceivedEntity> optional = webhookRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // webhookRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WebhookReceived not found");
        }
    }

    @Override
    public void delete(WebhookReceivedRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WebhookReceivedResponse convertToResponse(WebhookReceivedEntity entity) {
        return modelMapper.map(entity, WebhookReceivedResponse.class);
    }

    @Override
    public WebhookReceivedExcel convertToExcel(WebhookReceivedEntity entity) {
        return modelMapper.map(entity, WebhookReceivedExcel.class);
    }
    
    // public void initWebhookReceiveds(String orgUid) {
    //     // log.info("initThreadWebhookReceived");
    //     for (String webhook : WebhookReceivedInitData.getAllWebhookReceiveds()) {
    //         WebhookReceivedRequest webhookRequest = WebhookReceivedRequest.builder()
    //                 .uid(Utils.formatUid(orgUid, webhook))
    //                 .name(webhook)
    //                 .order(0)
    //                 .type(WebhookReceivedTypeEnum.THREAD.name())
    //                 .level(LevelEnum.ORGANIZATION.name())
    //                 .platform(BytedeskConsts.PLATFORM_BYTEDESK)
    //                 .orgUid(orgUid)
    //                 .build();
    //         create(webhookRequest);
    //     }
    // }
    
}
