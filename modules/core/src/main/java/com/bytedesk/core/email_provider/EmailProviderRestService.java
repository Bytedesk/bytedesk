/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 18:16:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_provider;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EmailProviderRestService extends BaseRestServiceWithExport<EmailProviderEntity, EmailProviderRequest, EmailProviderResponse, EmailProviderExcel> {

    private final EmailProviderRepository emailRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<EmailProviderEntity> createSpecification(EmailProviderRequest request) {
        return EmailProviderSpecification.search(request, authService);
    }

    @Override
    protected Page<EmailProviderEntity> executePageQuery(Specification<EmailProviderEntity> spec, Pageable pageable) {
        return emailRepository.findAll(spec, pageable);
    }

    @Override
    public EmailProviderResponse queryByUid(EmailProviderRequest request) {
        Optional<EmailProviderEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Cacheable(value = "email", key = "#uid", unless="#result==null")
    @Override
    public Optional<EmailProviderEntity> findByUid(String uid) {
        return emailRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return emailRepository.existsByUid(uid);
    }

    @Override
    public EmailProviderResponse create(EmailProviderRequest request) {
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
        EmailProviderEntity entity = modelMapper.map(request, EmailProviderEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        EmailProviderEntity savedEntity = save(entity);
        return convertToResponse(savedEntity);
    }

    @Override
    public EmailProviderResponse update(EmailProviderRequest request) {
        Optional<EmailProviderEntity> optional = emailRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            EmailProviderEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            EmailProviderEntity savedEntity = save(entity);
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected EmailProviderEntity doSave(EmailProviderEntity entity) {
        return emailRepository.save(entity);
    }

    @Override
    public EmailProviderEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EmailProviderEntity entity) {
        try {
            Optional<EmailProviderEntity> latest = emailRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EmailProviderEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return emailRepository.save(latestEntity);
            } else {
                throw new RuntimeException("无法找到最新的实体数据，uid: " + entity.getUid());
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<EmailProviderEntity> optional = emailRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // emailRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(EmailProviderRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public EmailProviderResponse convertToResponse(EmailProviderEntity entity) {
        return modelMapper.map(entity, EmailProviderResponse.class);
    }

    @Override
    public EmailProviderExcel convertToExcel(EmailProviderEntity entity) {
        return modelMapper.map(entity, EmailProviderExcel.class);
    }

    /**
     * 初始化默认邮件服务提供商
     * 仅在对应 uid 不存在时创建，不会覆盖已有配置
     */
    public void initEmailProviders(String orgUid) {
        for (EmailProviderInitData.EmailProviderDef def : EmailProviderInitData.DEFAULT_EMAIL_PROVIDERS) {
            String uid = def.uid();
            if (!existsByUid(uid)) {
                try {
                    EmailProviderEntity entity = EmailProviderEntity.builder()
                            .uid(uid)
                            .name(def.name())
                            .description("系统预设的" + def.name() + "配置，请填写邮箱地址和授权码后即可使用")
                            .provider(def.provider())
                            .type(def.type())
                            .protocol(def.protocol())
                            .smtpHost(def.smtpHost())
                            .smtpPort(def.smtpPort())
                            .smtpSslEnabled(def.smtpSslEnabled())
                            .smtpTlsEnabled(def.smtpTlsEnabled())
                            .imapHost(def.imapHost())
                            .imapPort(def.imapPort())
                            .imapSslEnabled(def.imapSslEnabled())
                            .pop3Host(def.pop3Host())
                            .pop3Port(def.pop3Port())
                            .pop3SslEnabled(def.pop3SslEnabled())
                            .exchangeHost(def.exchangeHost())
                            .exchangePort(def.exchangePort() != null ? def.exchangePort() : 993)
                            .exchangeSslEnabled(def.exchangeSslEnabled() != null ? def.exchangeSslEnabled() : true)
                            .syncInterval(def.syncInterval())
                            .autoSyncEnabled(def.autoSyncEnabled())
                            .autoReplyEnabled(def.autoReplyEnabled())
                            .autoReplyContent(def.autoReplyContent())
                            .orgUid(orgUid)
                            .build();
                    emailRepository.save(entity);
                    log.info("initEmailProviders created: uid={}, name={}", uid, def.name());
                } catch (Exception e) {
                    log.warn("initEmailProviders failed for uid={}: {}", uid, e.getMessage());
                }
            }
        }
    }
}
