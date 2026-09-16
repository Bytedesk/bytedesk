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
package com.bytedesk.core.sms_template;

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
import com.bytedesk.core.sms_provider.SmsProviderEntity;
import com.bytedesk.core.sms_provider.SmsProviderRestService;
import com.bytedesk.core.sms_push.SmsPushSendService;
import com.bytedesk.core.system_config.utils.PlatformSecretUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SmsTemplateRestService extends BaseRestServiceWithExport<SmsTemplateEntity, SmsTemplateRequest, SmsTemplateResponse, SmsTemplateExcel> {

    private final SmsTemplateRepository sms_templateRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final SmsProviderRestService smsProviderRestService;

    private final SmsPushSendService smsPushSendService;

    @Override
    protected Specification<SmsTemplateEntity> createSpecification(SmsTemplateRequest request) {
        return SmsTemplateSpecification.search(request, authService);
    }

    @Override
    protected Page<SmsTemplateEntity> executePageQuery(Specification<SmsTemplateEntity> spec, Pageable pageable) {
        return sms_templateRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "sms_template", key = "#uid", unless="#result==null")
    @Override
    public Optional<SmsTemplateEntity> findByUid(String uid) {
        return sms_templateRepository.findByUid(uid);
    }

    @Cacheable(value = "sms_template", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<SmsTemplateEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return sms_templateRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return sms_templateRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public SmsTemplateResponse create(SmsTemplateRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<SmsTemplateEntity> sms_template = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (sms_template.isPresent()) {
                return convertToResponse(sms_template.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        SmsTemplateEntity entity = modelMapper.map(request, SmsTemplateEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        SmsTemplateEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create sms_template failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public SmsTemplateResponse update(SmsTemplateRequest request) {
        Optional<SmsTemplateEntity> optional = sms_templateRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            SmsTemplateEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            SmsTemplateEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update sms_template failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("SmsTemplate not found");
        }
    }

    @Override
    protected SmsTemplateEntity doSave(SmsTemplateEntity entity) {
        return sms_templateRepository.save(entity);
    }

    @Override
    public SmsTemplateEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, SmsTemplateEntity entity) {
        try {
            Optional<SmsTemplateEntity> latest = sms_templateRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                SmsTemplateEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return sms_templateRepository.save(latestEntity);
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
        Optional<SmsTemplateEntity> optional = sms_templateRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // sms_templateRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("SmsTemplate not found");
        }
    }

    @Override
    public void delete(SmsTemplateRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public SmsTemplateResponse convertToResponse(SmsTemplateEntity entity) {
        return modelMapper.map(entity, SmsTemplateResponse.class);
    }

    @Override
    public SmsTemplateExcel convertToExcel(SmsTemplateEntity entity) {
        return modelMapper.map(entity, SmsTemplateExcel.class);
    }
    
    /**
     * 初始化默认短信模板
     * 仅在模板不存在时创建，不会覆盖已有模板
     */
    public void initSmsTemplates(String orgUid) {
        for (SmsTemplateInitData.SmsTemplateDef def : SmsTemplateInitData.DEFAULT_TICKET_TEMPLATES) {
            String uid = def.uid();
            if (!existsByUid(uid)) {
                try {
                    SmsTemplateEntity entity = SmsTemplateEntity.builder()
                            .uid(uid)
                            .name(def.name())
                            .description(def.description())
                            .type(def.type())
                            .content(def.content())
                            .signName(def.signName())
                            .templateCode(def.templateCode())
                            .variableNames(List.of("name"))
                            .orgUid(orgUid)
                            .build();
                    sms_templateRepository.save(entity);
                    log.info("initSmsTemplates created: uid={}, name={}", uid, def.name());
                } catch (Exception e) {
                    log.warn("initSmsTemplates failed for uid={}: {}", uid, e.getMessage());
                }
            } else {
                try {
                    Optional<SmsTemplateEntity> existingOpt = sms_templateRepository.findByUid(uid);
                    if (existingOpt.isPresent()) {
                        SmsTemplateEntity existing = existingOpt.get();
                        if (SmsTemplateTypeEnum.TICKET.name().equals(existing.getType())
                                && (existing.getVariableNames() == null
                                        || !existing.getVariableNames().equals(List.of("name")))) {
                            existing.setVariableNames(List.of("name"));
                            sms_templateRepository.save(existing);
                            log.info("initSmsTemplates normalized variableNames for uid={}", uid);
                        }
                    }
                } catch (Exception e) {
                    log.warn("initSmsTemplates normalize failed for uid={}: {}", uid, e.getMessage());
                }
            }
        }
    }

    public JsonResult<Boolean> sendTestSms(SmsTemplateTestRequest request) {
        if (!StringUtils.hasText(request.getTemplateUid())) {
            return JsonResult.error("短信模板不能为空");
        }
        if (!StringUtils.hasText(request.getProviderUid())) {
            return JsonResult.error("短信服务商不能为空");
        }
        if (!StringUtils.hasText(request.getMobile())) {
            return JsonResult.error("测试手机号不能为空");
        }

        SmsTemplateEntity template = findByUid(request.getTemplateUid())
                .orElseThrow(() -> new RuntimeException("SmsTemplate not found"));
        SmsProviderEntity provider = smsProviderRestService.findByUid(request.getProviderUid())
                .orElseThrow(() -> new RuntimeException("SmsProvider not found"));

        String signName = StringUtils.hasText(template.getSignName()) ? template.getSignName() : provider.getName();
        if (!StringUtils.hasText(signName)) {
            return JsonResult.error("短信签名不能为空");
        }
        if (!StringUtils.hasText(template.getTemplateCode())) {
            return JsonResult.error("短信模板编码不能为空");
        }

        List<String> variableNames = template.getVariableNames() == null ? List.of() : template.getVariableNames();
        Map<String, String> params = new LinkedHashMap<>();
        Map<String, String> requestVariables = request.getVariables() == null ? Map.of() : request.getVariables();
        for (String variableName : variableNames) {
            params.put(variableName, requestVariables.getOrDefault(variableName, "test"));
        }
        if (params.isEmpty()) {
            params.put("content", StringUtils.hasText(template.getContent()) ? template.getContent() : "测试短信");
        }

        var result = smsPushSendService.sendSmsWithTemplateByProvider(
                request.getMobile(),
                StringUtils.hasText(request.getCountry()) ? request.getCountry() : "86",
            provider.getName(),
            provider.getRegion(),
            provider.getAccessKeyId(),
            PlatformSecretUtils.decrypt(provider.getAccessKeySecret()),
            provider.getEndpoint(),
                signName,
                template.getTemplateCode(),
                params,
                template.getOrgUid());

        if (result.isSuccess()) {
            return JsonResult.success("测试短信发送成功");
        }
        return JsonResult.error("测试短信发送失败: " + (result.getErrorMessage() != null ? result.getErrorMessage() : "未知错误"));
    }

    
    
}
