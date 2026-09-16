/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.sms_provider;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.system_config.utils.PlatformSecretUtils;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SmsProviderRestService extends BaseRestServiceWithExport<SmsProviderEntity, SmsProviderRequest, SmsProviderResponse, SmsProviderExcel> {

    private final SmsProviderRepository sms_providerRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<SmsProviderEntity> createSpecification(SmsProviderRequest request) {
        return SmsProviderSpecification.search(request, authService);
    }

    @Override
    protected Page<SmsProviderEntity> executePageQuery(Specification<SmsProviderEntity> spec, Pageable pageable) {
        return sms_providerRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "sms_provider", key = "#uid", unless="#result==null")
    @Override
    public Optional<SmsProviderEntity> findByUid(String uid) {
        return sms_providerRepository.findByUid(uid);
    }

    @Cacheable(value = "sms_provider", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SmsProviderEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return sms_providerRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return sms_providerRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SmsProviderResponse create(SmsProviderRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SmsProviderEntity> sms_provider = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (sms_provider.isPresent()) {
                return convertToResponse(sms_provider.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        SmsProviderEntity entity = modelMapper.map(request, SmsProviderEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // AccessKeySecret 加密入库：明文 → ENC(密文)
        entity.setAccessKeySecret(PlatformSecretUtils.encrypt(entity.getAccessKeySecret()));
        // 
        SmsProviderEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create sms_provider failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SmsProviderResponse update(SmsProviderRequest request) {
        Optional<SmsProviderEntity> optional = sms_providerRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SmsProviderEntity entity = optional.get();
            String existingSecret = entity.getAccessKeySecret();
            modelMapper.map(request, entity);
            // 前端回传掩码或空值时保留原 Secret，避免被覆盖丢失；新 Secret 加密入库
            if (!StringUtils.hasText(entity.getAccessKeySecret())
                    || PlatformSecretUtils.SECRET_MASK.equals(entity.getAccessKeySecret())) {
                entity.setAccessKeySecret(existingSecret);
            } else {
                entity.setAccessKeySecret(PlatformSecretUtils.encrypt(entity.getAccessKeySecret()));
            }
            //
            SmsProviderEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update sms_provider failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("SmsProvider not found");
        }
    }

    @Override
    protected SmsProviderEntity doSave(SmsProviderEntity entity) {
        return sms_providerRepository.save(entity);
    }

    @Override
    public SmsProviderEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SmsProviderEntity entity) {
        try {
            Optional<SmsProviderEntity> latest = sms_providerRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SmsProviderEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return sms_providerRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<SmsProviderEntity> optional = sms_providerRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // sms_providerRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("SmsProvider not found");
        }
    }

    @Override
    public void delete(SmsProviderRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SmsProviderResponse convertToResponse(SmsProviderEntity entity) {
        SmsProviderResponse response = modelMapper.map(entity, SmsProviderResponse.class);
        // AccessKeySecret 脱敏：已配置返回 ***，否则为空，永不回显明文/密文
        response.setAccessKeySecret(
                StringUtils.hasText(entity.getAccessKeySecret()) ? PlatformSecretUtils.SECRET_MASK : null);
        return response;
    }

    @Override
    public SmsProviderExcel convertToExcel(SmsProviderEntity entity) {
        return modelMapper.map(entity, SmsProviderExcel.class);
    }
    
    public void initSmsProviders(String orgUid) {
        for (SmsProviderInitData.SmsProviderDef def : SmsProviderInitData.DEFAULT_SMS_PROVIDERS) {
            String uid = def.uid();
            if (!existsByUid(uid)) {
                try {
                    SmsProviderEntity entity = SmsProviderEntity.builder()
                            .uid(uid)
                            .name(def.name())
                            .description("系统预设的" + def.name() + "配置，请填写 AccessKey 和签名模板后即可使用")
                            .type(def.type())
                            .providerType(def.providerType())
                            .region(def.region())
                            .endpoint(def.endpoint())
                            .enabled(true)
                            .orgUid(orgUid)
                            .build();
                    sms_providerRepository.save(entity);
                    log.info("initSmsProviders created: uid={}, name={}", uid, def.name());
                } catch (Exception e) {
                    log.warn("initSmsProviders failed for uid={}: {}", uid, e.getMessage());
                }
            }
        }
    }

    
    
}
