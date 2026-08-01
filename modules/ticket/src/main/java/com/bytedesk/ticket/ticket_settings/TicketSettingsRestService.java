/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 18:05:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket_settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.modelmapper.ModelMapper;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.service.form.FormEntity;
import com.bytedesk.service.form.FormRepository;
import com.bytedesk.service.form.FormResponse;
import com.bytedesk.service.form.FormTypeEnum;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.process.ProcessResponse;
import com.bytedesk.ticket.process.ProcessTypeEnum;
import com.bytedesk.ticket.ticket.TicketCategories;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsRequest;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsResponse;
import com.bytedesk.ticket.ticket_settings_auto_create.TicketAutoCreateSettingsEntity;
import com.bytedesk.ticket.ticket_settings_auto_create.TicketAutoCreateSettingsRequest;
import com.bytedesk.ticket.ticket_settings_auto_create.TicketAutoCreateSettingsResponse;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBindingEntity;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBindingRepository;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryItemData;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsData;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsEntity;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsResponse;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsEntity;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsRequest;
import com.bytedesk.ticket.ticket_settings_notification.TicketNotificationSettingsResponse;
import com.bytedesk.ticket.ticket_settings_sla.TicketSlaSettingsEntity;
import com.bytedesk.ticket.ticket_settings_sla.TicketSlaSettingsRequest;
import com.bytedesk.ticket.ticket_settings_sla.TicketSlaSettingsResponse;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityCategoryRuleData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityCategoryRuleRequest;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityCategoryRuleResponse;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilityModeEnum;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsData;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsEntity;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsRequest;
import com.bytedesk.ticket.ticket_settings_visibility.TicketVisibilitySettingsResponse;
import com.bytedesk.ticket.ticket_sla_rule.TicketSlaRuleEntity;
import com.bytedesk.ticket.ticket_sla_rule.TicketSlaRuleResponse;
import com.bytedesk.core.email_provider.EmailProviderEntity;
import com.bytedesk.core.email_provider.EmailProviderRepository;
import com.bytedesk.core.email_push.EmailPushSendService;
import com.bytedesk.core.sms_push.SmsPushSendService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketSettingsRestService extends
        BaseRestServiceWithExport<TicketSettingsEntity, TicketSettingsRequest, TicketSettingsResponse, TicketSettingsExcel> {

    private final TicketSettingsRepository ticketSettingsRepository;

    private final TicketSettingsBindingRepository bindingRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final ProcessRepository ticketProcessRepository;

    private final FormRepository formRepository;

    private final EmailProviderRepository emailProviderRepository;

    private final EmailPushSendService emailPushSendService;

    private final SmsPushSendService smsPushSendService;

    @Override
    protected Specification<TicketSettingsEntity> createSpecification(TicketSettingsRequest request) {
        return TicketSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketSettingsEntity> executePageQuery(Specification<TicketSettingsEntity> spec, Pageable pageable) {
        return ticketSettingsRepository.findAll(spec, pageable);
    }

    // @Cacheable(value = "ticketSettings", key = "#uid", unless = "#result==null")
    @Override
    public Optional<TicketSettingsEntity> findByUid(String uid) {
        return ticketSettingsRepository.findByUid(uid);
    }

    // @Cacheable(value = "ticketSettings", key = "#name + '_' + #orgUid + '_' +
    // #type", unless = "#result==null")
    public Optional<TicketSettingsEntity> findByNameAndOrgUid(String name, String orgUid, String type) {
        return ticketSettingsRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return ticketSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketSettingsResponse create(TicketSettingsRequest request) {
        String normalizedType = resolveSettingsType(request.getType());
        request.setType(normalizedType);
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid())) {
            Optional<TicketSettingsEntity> ticketSettings = findByNameAndOrgUid(
                    request.getName(),
                    request.getOrgUid(),
                    normalizedType);
            if (ticketSettings.isPresent()) {
                return convertToResponse(ticketSettings.get());
            }
        }
        //
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 基础实体
        TicketSettingsEntity entity = modelMapper.map(request, TicketSettingsEntity.class);
        entity.setType(normalizedType);
        // 赋 UID
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }

        // 初始化并绑定发布 + 草稿子配置
        TicketBasicSettingsEntity basic = createBasicSettingsEntity(request.getBasicSettings(), entity.getOrgUid());
        entity.setBasicSettings(basic);

        TicketBasicSettingsEntity draftBasic = createBasicSettingsEntity(resolveDraftBasicRequest(request),
                entity.getOrgUid());
        entity.setDraftBasicSettings(draftBasic);

        entity.setCategorySettings(createCategorySettingsEntity(request.getCategorySettings(), entity.getOrgUid()));

        entity.setDraftCategorySettings(
                createCategorySettingsEntity(resolveDraftCategoryRequest(request), entity.getOrgUid()));

        // 通知设置
        entity.setNotificationSettings(
                createNotificationSettingsEntity(request.getNotificationSettings(), entity.getOrgUid()));
        entity.setDraftNotificationSettings(
                createNotificationSettingsEntity(resolveDraftNotificationRequest(request), entity.getOrgUid()));

        entity.setSlaSettings(createSlaSettingsEntity(request.getSlaSettings(), entity.getOrgUid()));
        entity.setDraftSlaSettings(createSlaSettingsEntity(resolveDraftSlaRequest(request), entity.getOrgUid()));

        entity.setAutoCreateSettings(
                createAutoCreateSettingsEntity(request.getAutoCreateSettings(), entity.getOrgUid()));
        entity.setDraftAutoCreateSettings(
                createAutoCreateSettingsEntity(resolveDraftAutoCreateRequest(request), entity.getOrgUid()));

        entity.setVisibilitySettings(
                createVisibilitySettingsEntity(request.getVisibilitySettings(), entity.getOrgUid()));
        entity.setDraftVisibilitySettings(
                createVisibilitySettingsEntity(resolveDraftVisibilityRequest(request), entity.getOrgUid()));

        String resolvedProcessUid = resolveProcessUidOrDefault(request, entity.getOrgUid(), normalizedType);
        entity.setProcess(resolveProcessReference(resolvedProcessUid, entity.getOrgUid()));
        entity.setDraftProcess(resolveProcessReference(resolvedProcessUid, entity.getOrgUid()));

        String resolvedFormUid = resolveFormUidOrDefault(request, entity.getOrgUid(), normalizedType);
        entity.setForm(resolveFormReference(resolvedFormUid, entity.getOrgUid()));
        entity.setDraftForm(resolveFormReference(resolvedFormUid, entity.getOrgUid()));

        // 默认启用为空时置为 true
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        if (entity.getCustomFormEnabled() == null) {
            entity.setCustomFormEnabled(false);
        }
        // 若请求设置为默认，保证同 org 仅有一个默认
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), normalizedType, entity);
        } else if (entity.getIsDefault() == null) {
            entity.setIsDefault(false);
        }

        TicketSettingsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create ticketSettings failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TicketSettingsResponse update(TicketSettingsRequest request) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            //
            TicketSettingsEntity entity = optional.get();
            String normalizedType = StringUtils.hasText(request.getType())
                    ? resolveSettingsType(request.getType())
                    : resolveSettingsType(entity.getType());
            // 更新基础字段（不直接覆盖子配置）
            // modelMapper.map(request, entity);
            entity.setName(request.getName() != null ? request.getName() : entity.getName());
            entity.setDescription(
                    request.getDescription() != null ? request.getDescription() : entity.getDescription());
            entity.setType(normalizedType);
            boolean draftUpdated = false;

            // 更新草稿子配置
            TicketBasicSettingsRequest draftBasicRequest = resolveDraftBasicRequest(request);
            if (draftBasicRequest != null) {
                TicketBasicSettingsEntity draft = entity.getDraftBasicSettings();
                if (draft == null) {
                    draft = createBasicSettingsEntity(draftBasicRequest, entity.getOrgUid());
                    entity.setDraftBasicSettings(draft);
                } else {
                    applyBasicSettingsRequest(draft, draftBasicRequest);
                }
                draftUpdated = true;
            }

            if (request.getCategorySettings() != null) {
                TicketCategorySettingsEntity draftCategory = entity.getDraftCategorySettings();
                if (draftCategory == null) {
                    draftCategory = TicketCategorySettingsEntity.fromRequest(request.getCategorySettings(),
                            uidUtils::getUid);
                    draftCategory.setUid(uidUtils.getUid());
                    draftCategory.setOrgUid(entity.getOrgUid());
                    entity.setDraftCategorySettings(draftCategory);
                } else {
                    draftCategory.replaceFromRequest(request.getCategorySettings(), uidUtils::getUid);
                }
                draftUpdated = true;
            }

            TicketVisibilitySettingsRequest draftVisibilityRequest = resolveDraftVisibilityRequest(request);
            if (draftVisibilityRequest != null) {
                TicketVisibilitySettingsEntity draftVisibility = entity.getDraftVisibilitySettings();
                if (draftVisibility == null) {
                    draftVisibility = createVisibilitySettingsEntity(draftVisibilityRequest, entity.getOrgUid());
                    entity.setDraftVisibilitySettings(draftVisibility);
                } else {
                    TicketVisibilitySettingsEntity.applyRequest(draftVisibility, draftVisibilityRequest);
                }
                draftUpdated = true;
            }

            // 通知设置草稿
            TicketNotificationSettingsRequest draftNotifRequest = resolveDraftNotificationRequest(request);
            if (draftNotifRequest != null) {
                TicketNotificationSettingsEntity draftNotif = entity.getDraftNotificationSettings();
                if (draftNotif == null) {
                    draftNotif = TicketNotificationSettingsEntity.fromRequest(draftNotifRequest);
                    draftNotif.setUid(uidUtils.getUid());
                    draftNotif.setOrgUid(entity.getOrgUid());
                    entity.setDraftNotificationSettings(draftNotif);
                } else {
                    TicketNotificationSettingsEntity updated = TicketNotificationSettingsEntity
                            .fromRequest(draftNotifRequest);
                    applyNotificationSettings(draftNotif, updated);
                }
                draftUpdated = true;
            }

            TicketSlaSettingsRequest draftSlaRequest = resolveDraftSlaRequest(request);
            if (draftSlaRequest != null) {
                TicketSlaSettingsEntity draftSla = entity.getDraftSlaSettings();
                if (draftSla == null) {
                    draftSla = createSlaSettingsEntity(draftSlaRequest, entity.getOrgUid());
                    entity.setDraftSlaSettings(draftSla);
                } else {
                    TicketSlaSettingsEntity.applyRequest(draftSla, draftSlaRequest, uidUtils::getUid,
                            entity.getOrgUid());
                }
                draftUpdated = true;
            }

            if (request.getProcessUid() != null) {
                entity.setDraftProcess(resolveProcessReference(request.getProcessUid(), entity.getOrgUid()));
                draftUpdated = true;
            }

            if (request.getFormUid() != null) {
                entity.setDraftForm(resolveFormReference(request.getFormUid(), entity.getOrgUid()));
                draftUpdated = true;
            }

            TicketAutoCreateSettingsRequest draftAutoCreateRequest = resolveDraftAutoCreateRequest(request);
            if (draftAutoCreateRequest != null) {
                TicketAutoCreateSettingsEntity draftAutoCreate = entity.getDraftAutoCreateSettings();
                if (draftAutoCreate == null) {
                    draftAutoCreate = createAutoCreateSettingsEntity(draftAutoCreateRequest, entity.getOrgUid());
                    entity.setDraftAutoCreateSettings(draftAutoCreate);
                } else {
                    applyAutoCreateSettingsRequest(draftAutoCreate, draftAutoCreateRequest, entity.getOrgUid());
                }
                draftUpdated = true;
            }

            // 维护草稿未发布标记
            if (draftUpdated) {
                entity.setHasUnpublishedChanges(true);
            }

            // 处理 isDefault / enabled
            if (request.getIsDefault() != null) {
                if (Boolean.TRUE.equals(request.getIsDefault())) {
                    ensureSingleDefault(entity.getOrgUid(), normalizedType, entity);
                } else {
                    entity.setIsDefault(false);
                }
            }
            if (request.getEnabled() != null) {
                entity.setEnabled(request.getEnabled());
            }
            if (request.getCustomFormEnabled() != null) {
                entity.setCustomFormEnabled(request.getCustomFormEnabled());
            }

            TicketSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ticketSettings failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    /**
     * 根据 org + workgroup 获取设置；若未绑定则创建/获取组织默认设置并自动绑定该工作组。
     */
    @Transactional
    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid) {
        return getOrDefaultByWorkgroup(orgUid, workgroupUid, TicketTypeEnum.EXTERNAL.name());
    }

    @Transactional
    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        // 1) 已绑定则直接返回
        if (TicketTypeEnum.EXTERNAL.name().equals(normalizedType)) {
            Optional<TicketSettingsBindingEntity> bindingOpt = findBindingByWorkgroup(orgUid, workgroupUid);
            if (bindingOpt.isPresent()) {
                Optional<TicketSettingsEntity> settingsOpt = findByUid(bindingOpt.get().getTicketSettingsUid());
                if (settingsOpt.isPresent()) {
                    TicketSettingsEntity entity = settingsOpt.get();
                    if (!normalizedType.equals(entity.getType())) {
                        entity.setType(normalizedType);
                        save(entity);
                    }
                    return convertToResponse(entity);
                }
            }
            // 2) 获取或创建默认，并建立绑定
            TicketSettingsEntity def = getOrCreateDefault(orgUid, normalizedType);
            if (bindingOpt.isEmpty()) {
                TicketSettingsBindingEntity binding = TicketSettingsBindingEntity
                        .builder()
                        .uid(uidUtils.getUid())
                        .orgUid(orgUid)
                        .workgroupUid(workgroupUid)
                        .ticketSettingsUid(def.getUid())
                        .build();
                bindingRepository.save(binding);
            }
            return convertToResponse(def);
        }
        // INTERNAL 类型暂不绑定工作组，直接返回对应默认配置
        TicketSettingsEntity def = getOrCreateDefault(orgUid, normalizedType);
        return convertToResponse(def);
    }

    /**
     * Resolve ticket settings entity by workgroup/org for downstream domain
     * services.
     */
    @Transactional
    public TicketSettingsEntity resolveEntityByWorkgroup(String orgUid, String workgroupUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        if (TicketTypeEnum.EXTERNAL.name().equals(normalizedType)) {
            Optional<TicketSettingsBindingEntity> bindingOpt = findBindingByWorkgroup(orgUid, workgroupUid);
            if (bindingOpt.isPresent()) {
                Optional<TicketSettingsEntity> entityOpt = ticketSettingsRepository
                        .findByUid(bindingOpt.get().getTicketSettingsUid());
                if (entityOpt.isPresent()) {
                    TicketSettingsEntity entity = entityOpt.get();
                    if (!normalizedType.equals(entity.getType())) {
                        entity.setType(normalizedType);
                        save(entity);
                    }
                    return entity;
                }
            }
            TicketSettingsEntity def = getOrCreateDefault(orgUid, normalizedType);
            if (StringUtils.hasText(workgroupUid) && bindingOpt.isEmpty()) {
                TicketSettingsBindingEntity binding = TicketSettingsBindingEntity
                        .builder()
                        .uid(uidUtils.getUid())
                        .orgUid(orgUid)
                        .workgroupUid(workgroupUid)
                        .ticketSettingsUid(def.getUid())
                        .build();
                bindingRepository.save(binding);
            }
            return def;
        }
        return getOrCreateDefault(orgUid, normalizedType);
    }

    private Optional<TicketSettingsBindingEntity> findBindingByWorkgroup(String orgUid, String workgroupUid) {
        if (!StringUtils.hasText(orgUid) || !StringUtils.hasText(workgroupUid)) {
            return Optional.empty();
        }
        return bindingRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
    }

    /** 获取或创建组织默认 TicketSettings（发布+草稿齐全，保证并发唯一） */
    @Transactional
    public TicketSettingsEntity getOrCreateDefault(String orgUid) {
        return getOrCreateDefault(orgUid, TicketTypeEnum.EXTERNAL.name());
    }

    @Transactional
    public TicketSettingsEntity getOrCreateDefault(String orgUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        Optional<TicketSettingsEntity> existing = ticketSettingsRepository.findDefaultForUpdate(orgUid, normalizedType);
        if (existing.isPresent()) {
            TicketSettingsEntity existingEntity = existing.get();
            boolean changed = false;

            // 兼容历史数据：若默认 settings 已存在但未绑定默认流程/表单，则在读的时候补齐。
            if (existingEntity.getProcess() == null || existingEntity.getDraftProcess() == null) {
                String defaultProcessUid = resolveDefaultProcessUid(orgUid, normalizedType);
                ProcessEntity defaultProcess = resolveProcessReference(defaultProcessUid, orgUid);
                if (existingEntity.getProcess() == null && defaultProcess != null) {
                    existingEntity.setProcess(defaultProcess);
                    changed = true;
                }
                if (existingEntity.getDraftProcess() == null && defaultProcess != null) {
                    existingEntity.setDraftProcess(defaultProcess);
                    changed = true;
                }
            }

            if (existingEntity.getForm() == null || existingEntity.getDraftForm() == null) {
                String defaultFormUid = resolveDefaultFormUid(orgUid, normalizedType);
                FormEntity defaultForm = resolveFormReference(defaultFormUid, orgUid);
                if (existingEntity.getForm() == null && defaultForm != null) {
                    existingEntity.setForm(defaultForm);
                    changed = true;
                }
                if (existingEntity.getDraftForm() == null && defaultForm != null) {
                    existingEntity.setDraftForm(defaultForm);
                    changed = true;
                }
            }

            return changed ? save(existingEntity) : existingEntity;
        }

        // 兼容旧数据：若存在未设置 type 的默认记录，则补全后复用
        List<TicketSettingsEntity> legacyDefaults = ticketSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid);
        if (legacyDefaults != null) {
            Optional<TicketSettingsEntity> legacy = legacyDefaults.stream()
                    .filter(item -> !StringUtils.hasText(item.getType()))
                    .findFirst();
            if (legacy.isPresent()) {
                TicketSettingsEntity legacyEntity = legacy.get();
                legacyEntity.setType(normalizedType);
                return save(legacyEntity);
            }
        }

        // 按 WorkgroupSettingsRestService 模式创建：发布 + 草稿各自独立初始化并分配唯一 UID
        String settingsName;
        String settingsDescription;

        // 根据工单类型区分名称和描述
        if (TicketTypeEnum.INTERNAL.name().equals(normalizedType)) {
            settingsName = I18Consts.I18N_TICKET_SETTINGS_INTERNAL_NAME;
            settingsDescription = I18Consts.I18N_TICKET_SETTINGS_INTERNAL_DESCRIPTION;
        } else {
            settingsName = I18Consts.I18N_TICKET_SETTINGS_EXTERNAL_NAME;
            settingsDescription = I18Consts.I18N_TICKET_SETTINGS_EXTERNAL_DESCRIPTION;
        }

        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .type(normalizedType)
                .name(settingsName)
                .description(settingsDescription)
                .isDefault(true)
                .enabled(true)
                .customFormEnabled(false)
                .build();

        settings.setCategorySettings(createCategorySettingsEntity(null, orgUid));

        settings.setDraftCategorySettings(createCategorySettingsEntity(null, orgUid));

        // 通知设置
        settings.setNotificationSettings(createNotificationSettingsEntity(null, orgUid));
        settings.setDraftNotificationSettings(createNotificationSettingsEntity(null, orgUid));

        settings.setSlaSettings(createSlaSettingsEntity(null, orgUid));
        settings.setDraftSlaSettings(createSlaSettingsEntity(null, orgUid));

        settings.setAutoCreateSettings(createAutoCreateSettingsEntity(null, orgUid));
        settings.setDraftAutoCreateSettings(createAutoCreateSettingsEntity(null, orgUid));

        settings.setVisibilitySettings(createVisibilitySettingsEntity(null, orgUid));
        settings.setDraftVisibilitySettings(createVisibilitySettingsEntity(null, orgUid));

        TicketBasicSettingsEntity basic = createBasicSettingsEntity(null, orgUid);
        settings.setBasicSettings(basic);
        TicketBasicSettingsEntity draftBasic = createBasicSettingsEntity(null, orgUid);
        settings.setDraftBasicSettings(draftBasic);

        String defaultProcessUid = resolveDefaultProcessUid(orgUid, normalizedType);
        settings.setProcess(resolveProcessReference(defaultProcessUid, orgUid));
        settings.setDraftProcess(resolveProcessReference(defaultProcessUid, orgUid));

        String defaultFormUid = resolveDefaultFormUid(orgUid, normalizedType);
        settings.setForm(resolveFormReference(defaultFormUid, orgUid));
        settings.setDraftForm(resolveFormReference(defaultFormUid, orgUid));

        // 确保同 org 仅有一个默认（虽然已锁定查询，此调用保持一致性）
        ensureSingleDefault(orgUid, normalizedType, settings);

        TicketSettingsEntity saved = save(settings);
        if (saved == null) {
            throw new RuntimeException("Create default ticket settings failed");
        }
        return saved;
    }

    /**
     * 批量绑定工作组到指定 TicketSettings。
     * 每个工作组仅能绑定一条记录：若已存在则覆盖其 ticketSettingsUid。
     */
    @Transactional
    public void bindWorkgroups(String ticketSettingsUid, String orgUid, java.util.List<String> workgroupUids) {
        if (!StringUtils.hasText(ticketSettingsUid) || !StringUtils.hasText(orgUid) || workgroupUids == null) {
            throw new IllegalArgumentException("参数非法");
        }
        for (String wgUid : workgroupUids) {
            if (!StringUtils.hasText(wgUid))
                continue;
            Optional<TicketSettingsBindingEntity> opt = bindingRepository
                    .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, wgUid);
            TicketSettingsBindingEntity binding = opt
                    .orElseGet(() -> TicketSettingsBindingEntity.builder()
                            .orgUid(orgUid)
                            .workgroupUid(wgUid)
                            .uid(uidUtils.getUid())
                            .ticketSettingsUid(ticketSettingsUid)
                            .build());
            binding.setTicketSettingsUid(ticketSettingsUid); // 覆盖更新
            bindingRepository.save(binding);
        }
    }

    public java.util.List<TicketSettingsBindingEntity> listBindings(
            String ticketSettingsUid) {
        return bindingRepository.findByTicketSettingsUidAndDeletedFalse(ticketSettingsUid);
    }

    /**
     * 按 orgUid+workgroupUid 保存草稿。若尚未绑定则自动创建默认 settings 绑定后再更新草稿。
     */
    @Transactional
    public TicketSettingsResponse saveByWorkgroup(String orgUid, String workgroupUid,
            TicketSettingsRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        String normalizedType = resolveSettingsType(request.getType());
        if (!TicketTypeEnum.EXTERNAL.name().equals(normalizedType)) {
            throw new IllegalArgumentException("Workgroup scoped ticket settings only support EXTERNAL type");
        }
        // 先获取已绑定的 settings；没有则创建/获取默认并绑定
        Optional<TicketSettingsBindingEntity> bindingOpt = bindingRepository
                .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        TicketSettingsEntity entity = null;
        if (bindingOpt.isPresent()) {
            Optional<TicketSettingsEntity> settingsOpt = findByUid(bindingOpt.get().getTicketSettingsUid());
            if (settingsOpt.isPresent()) {
                entity = settingsOpt.get();
            }
        }
        if (entity == null) {
            entity = getOrCreateDefault(orgUid, normalizedType); // 默认 settings 已包含发布+草稿
            // 建立绑定
            TicketSettingsBindingEntity binding = TicketSettingsBindingEntity
                    .builder()
                    .uid(uidUtils.getUid())
                    .orgUid(orgUid)
                    .workgroupUid(workgroupUid)
                    .ticketSettingsUid(entity.getUid())
                    .build();
            bindingRepository.save(binding);
        }
        if (entity != null && !normalizedType.equals(entity.getType())) {
            entity.setType(normalizedType);
        }
        // 更新基础可编辑字段（name/description）
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        boolean draftUpdated = false;
        TicketCategorySettingsRequest draftCategoryRequest = resolveDraftCategoryRequest(request);
        if (draftCategoryRequest != null) {
            TicketCategorySettingsEntity draftCategory = entity.getDraftCategorySettings();
            if (draftCategory == null) {
                draftCategory = TicketCategorySettingsEntity
                        .fromRequest(draftCategoryRequest, uidUtils::getUid);
                draftCategory.setUid(uidUtils.getUid());
                draftCategory.setOrgUid(entity.getOrgUid());
                entity.setDraftCategorySettings(draftCategory);
            } else {
                draftCategory.replaceFromRequest(draftCategoryRequest, uidUtils::getUid);
            }
            draftUpdated = true;
        }
        TicketBasicSettingsRequest draftBasicRequest = resolveDraftBasicRequest(request);
        if (draftBasicRequest != null) {
            TicketBasicSettingsEntity draft = entity.getDraftBasicSettings();
            if (draft == null) {
                draft = createBasicSettingsEntity(draftBasicRequest, entity.getOrgUid());
                entity.setDraftBasicSettings(draft);
            } else {
                applyBasicSettingsRequest(draft, draftBasicRequest);
            }
            draftUpdated = true;
        }

        // 通知设置
        TicketNotificationSettingsRequest draftNotifRequest = resolveDraftNotificationRequest(request);
        if (draftNotifRequest != null) {
            TicketNotificationSettingsEntity draftNotif = entity.getDraftNotificationSettings();
            if (draftNotif == null) {
                draftNotif = TicketNotificationSettingsEntity.fromRequest(draftNotifRequest);
                draftNotif.setUid(uidUtils.getUid());
                draftNotif.setOrgUid(entity.getOrgUid());
                entity.setDraftNotificationSettings(draftNotif);
            } else {
                TicketNotificationSettingsEntity updated = TicketNotificationSettingsEntity
                        .fromRequest(draftNotifRequest);
                applyNotificationSettings(draftNotif, updated);
            }
            draftUpdated = true;
        }

        TicketSlaSettingsRequest draftSlaRequest = resolveDraftSlaRequest(request);
        if (draftSlaRequest != null) {
            TicketSlaSettingsEntity draftSla = entity.getDraftSlaSettings();
            if (draftSla == null) {
                draftSla = createSlaSettingsEntity(draftSlaRequest, entity.getOrgUid());
                entity.setDraftSlaSettings(draftSla);
            } else {
                TicketSlaSettingsEntity.applyRequest(draftSla, draftSlaRequest, uidUtils::getUid, entity.getOrgUid());
            }
            draftUpdated = true;
        }

        if (request.getProcessUid() != null) {
            entity.setDraftProcess(resolveProcessReference(request.getProcessUid(), entity.getOrgUid()));
            draftUpdated = true;
        }

        if (request.getFormUid() != null) {
            entity.setDraftForm(resolveFormReference(request.getFormUid(), entity.getOrgUid()));
            draftUpdated = true;
        }

        TicketAutoCreateSettingsRequest draftAutoCreateRequest = resolveDraftAutoCreateRequest(request);
        if (draftAutoCreateRequest != null) {
            TicketAutoCreateSettingsEntity draftAutoCreate = entity.getDraftAutoCreateSettings();
            if (draftAutoCreate == null) {
                draftAutoCreate = createAutoCreateSettingsEntity(draftAutoCreateRequest, entity.getOrgUid());
                entity.setDraftAutoCreateSettings(draftAutoCreate);
            } else {
                applyAutoCreateSettingsRequest(draftAutoCreate, draftAutoCreateRequest, entity.getOrgUid());
            }
            draftUpdated = true;
        }

        TicketVisibilitySettingsRequest draftVisibilityRequest = resolveDraftVisibilityRequest(request);
        if (draftVisibilityRequest != null) {
            TicketVisibilitySettingsEntity draftVisibility = entity.getDraftVisibilitySettings();
            if (draftVisibility == null) {
                draftVisibility = createVisibilitySettingsEntity(draftVisibilityRequest, entity.getOrgUid());
                entity.setDraftVisibilitySettings(draftVisibility);
            } else {
                TicketVisibilitySettingsEntity.applyRequest(draftVisibility, draftVisibilityRequest);
            }
            draftUpdated = true;
        }

        if (draftUpdated) {
            entity.setHasUnpublishedChanges(true);
        }
        TicketSettingsEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Save by workgroup failed");
        }
        return convertToResponse(saved);
    }

    /**
     * 发布草稿配置到正式配置，参考 WorkgroupSettings 的 publish 逻辑。
     */
    @Transactional
    public TicketSettingsResponse publish(String uid) {
        Optional<TicketSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("TicketSettings not found: " + uid);
        }
        TicketSettingsEntity entity = optional.get();

        if (entity.getDraftBasicSettings() != null) {
            TicketBasicSettingsEntity publishedBasic = entity.getBasicSettings();
            if (publishedBasic == null) {
                publishedBasic = createBasicSettingsEntity(null, entity.getOrgUid());
                entity.setBasicSettings(publishedBasic);
            }
            copyBasicSettings(entity.getDraftBasicSettings(), publishedBasic);
        }

        if (entity.getDraftCategorySettings() != null) {
            TicketCategorySettingsEntity draftCategory = entity.getDraftCategorySettings();
            TicketCategorySettingsEntity publishedCategory = entity.getCategorySettings();
            if (publishedCategory == null) {
                publishedCategory = TicketCategorySettingsEntity.fromRequest(null, uidUtils::getUid);
                publishedCategory.setUid(uidUtils.getUid());
                publishedCategory.setOrgUid(entity.getOrgUid());
                entity.setCategorySettings(publishedCategory);
            }
            if (draftCategory.getContent() != null) {
                draftCategory.getContent().normalize();
                publishedCategory.setContent(copyCategorySettings(draftCategory.getContent()));
            } else {
                publishedCategory.setContent(TicketCategorySettingsData.builder().build());
            }
        }

        // 同步流程及表单引用
        entity.setProcess(entity.getDraftProcess());
        entity.setForm(entity.getDraftForm());

        // 同步通知设置
        if (entity.getDraftNotificationSettings() != null) {
            TicketNotificationSettingsEntity publishedNotif = entity.getNotificationSettings();
            if (publishedNotif == null) {
                publishedNotif = TicketNotificationSettingsEntity.fromRequest(null);
                publishedNotif.setUid(uidUtils.getUid());
                publishedNotif.setOrgUid(entity.getOrgUid());
                entity.setNotificationSettings(publishedNotif);
            }
            copyNotificationSettings(entity.getDraftNotificationSettings(), publishedNotif);
        }

        if (entity.getDraftSlaSettings() != null) {
            TicketSlaSettingsEntity publishedSla = entity.getSlaSettings();
            if (publishedSla == null) {
                publishedSla = createSlaSettingsEntity(null, entity.getOrgUid());
                entity.setSlaSettings(publishedSla);
            }
            copySlaSettings(entity.getDraftSlaSettings(), publishedSla);
        }

        if (entity.getDraftAutoCreateSettings() != null) {
            TicketAutoCreateSettingsEntity publishedAutoCreate = entity.getAutoCreateSettings();
            if (publishedAutoCreate == null) {
                publishedAutoCreate = createAutoCreateSettingsEntity(null, entity.getOrgUid());
                entity.setAutoCreateSettings(publishedAutoCreate);
            }
            copyAutoCreateSettings(entity.getDraftAutoCreateSettings(), publishedAutoCreate, entity.getOrgUid());
        }

        if (entity.getDraftVisibilitySettings() != null) {
            TicketVisibilitySettingsEntity publishedVisibility = entity.getVisibilitySettings();
            if (publishedVisibility == null) {
                publishedVisibility = createVisibilitySettingsEntity(null, entity.getOrgUid());
                entity.setVisibilitySettings(publishedVisibility);
            }
            copyVisibilitySettings(entity.getDraftVisibilitySettings(), publishedVisibility);
        }

        // 发布时间与草稿标记维护
        entity.setPublishedAt(ZonedDateTime.now());
        entity.setHasUnpublishedChanges(false);
        //
        TicketSettingsEntity saved = save(entity);
        if (saved == null) {
            throw new RuntimeException("Publish ticketSettings failed");
        }

        return convertToResponse(saved);
    }

    /**
     * 按 orgUid + workgroupUid 发布对应 TicketSettings（便于前端直接在工作组维度触发）。
     */
    @Transactional
    public TicketSettingsResponse publishByWorkgroup(String orgUid, String workgroupUid) {
        Optional<TicketSettingsBindingEntity> bindingOpt = bindingRepository
                .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        if (bindingOpt.isPresent()) {
            return publish(bindingOpt.get().getTicketSettingsUid());
        }
        throw new RuntimeException("TicketSettings not found by binding");
    }

    @Override
    protected TicketSettingsEntity doSave(TicketSettingsEntity entity) {
        return ticketSettingsRepository.save(entity);
    }

    @Override
    public TicketSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketSettingsEntity entity) {
        try {
            Optional<TicketSettingsEntity> latest = ticketSettingsRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketSettingsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return ticketSettingsRepository.save(latestEntity);
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
        List<TicketSettingsBindingEntity> activeBindings = bindingRepository
                .findByTicketSettingsUidAndDeletedFalse(uid);
        if (!activeBindings.isEmpty()) {
            String boundWorkgroups = activeBindings.stream()
                    .map(TicketSettingsBindingEntity::getWorkgroupUid)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.joining(","));
            throw new IllegalStateException(
                    boundWorkgroups.isEmpty()
                            ? "Ticket settings is bound to workgroups, please unbind before deleting."
                            : String.format(
                                    "Ticket settings is bound to workgroups (%s), please unbind before deleting.",
                                    boundWorkgroups));
        }
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // ticketSettingsRepository.delete(optional.get());
        } else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    @Override
    public void delete(TicketSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    private TicketCategorySettingsRequest resolveDraftCategoryRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return request.getCategorySettings();
    }

    private TicketNotificationSettingsRequest resolveDraftNotificationRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return request.getNotificationSettings();
    }

    private TicketSlaSettingsRequest resolveDraftSlaRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return request.getSlaSettings();
    }

    private TicketAutoCreateSettingsRequest resolveDraftAutoCreateRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return request.getAutoCreateSettings();
    }

    private TicketVisibilitySettingsRequest resolveDraftVisibilityRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return sanitizeVisibilitySettingsRequest(request.getVisibilitySettings(), request.getType());
    }

    private TicketNotificationSettingsEntity createNotificationSettingsEntity(TicketNotificationSettingsRequest request,
            String orgUid) {
        TicketNotificationSettingsEntity entity = TicketNotificationSettingsEntity.fromRequest(request);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(orgUid);
        return entity;
    }

    private TicketSlaSettingsEntity createSlaSettingsEntity(TicketSlaSettingsRequest request, String orgUid) {
        TicketSlaSettingsEntity entity = TicketSlaSettingsEntity.fromRequest(request, uidUtils::getUid, orgUid);
        entity.setOrgUid(orgUid);
        return entity;
    }

    private TicketAutoCreateSettingsEntity createAutoCreateSettingsEntity(TicketAutoCreateSettingsRequest request,
            String orgUid) {
        TicketAutoCreateSettingsEntity entity = TicketAutoCreateSettingsEntity.fromRequest(request);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(orgUid);
        return entity;
    }

    private TicketVisibilitySettingsEntity createVisibilitySettingsEntity(TicketVisibilitySettingsRequest request,
            String orgUid) {
        TicketVisibilitySettingsEntity entity = TicketVisibilitySettingsEntity.fromRequest(request);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(orgUid);
        return entity;
    }

        private TicketVisibilitySettingsRequest sanitizeVisibilitySettingsRequest(TicketVisibilitySettingsRequest request,
            String rawType) {
        if (request == null) {
            return null;
        }
        TicketTypeEnum ticketType = TicketTypeEnum.fromValue(rawType);
        String mode = request.getMode();
        List<TicketVisibilityCategoryRuleRequest> categoryRules = request.getCategoryRules() == null
            ? new ArrayList<>()
            : request.getCategoryRules().stream()
                .filter(Objects::nonNull)
                .map(rule -> TicketVisibilityCategoryRuleRequest.builder()
                    .categoryUid(rule.getCategoryUid())
                    .visibility(rule.getVisibility())
                    .build())
                .collect(Collectors.toList());

        if (ticketType != TicketTypeEnum.INTERNAL) {
            if (TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED.name().equalsIgnoreCase(mode)) {
            mode = TicketVisibilityModeEnum.ORG_WIDE.name();
            }
            categoryRules = categoryRules.stream()
                .filter(rule -> !TicketVisibilityModeEnum.DEPARTMENT_RESTRICTED.name()
                    .equalsIgnoreCase(rule.getVisibility()))
                .collect(Collectors.toList());
        }

        return TicketVisibilitySettingsRequest.builder()
            .uid(request.getUid())
            .orgUid(request.getOrgUid())
            .userUid(request.getUserUid())
            .mode(mode)
            .categoryRules(categoryRules)
            .build();
        }

    private TicketCategorySettingsEntity createCategorySettingsEntity(TicketCategorySettingsRequest request,
            String orgUid) {
        TicketCategorySettingsEntity category = TicketCategorySettingsEntity.fromRequest(request, uidUtils::getUid);
        category.setUid(uidUtils.getUid());
        category.setOrgUid(orgUid);
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            category.setContent(buildDefaultCategorySettingsData());
        }
        return category;
    }

    private TicketCategorySettingsData buildDefaultCategorySettingsData() {
        List<TicketCategoryItemData> items = new ArrayList<>();
        String[] defaultCategories = TicketCategories.getAllCategories();
        for (int index = 0; index < defaultCategories.length; index++) {
            items.add(TicketCategoryItemData.builder()
                    .uid(uidUtils.getUid())
                    .name(defaultCategories[index])
                    .enabled(Boolean.TRUE)
                    .defaultCategory(index == 0)
                    .orderIndex(index)
                    .build());
        }
        TicketCategorySettingsData data = TicketCategorySettingsData.builder()
                .items(items)
                .build();
        data.normalize();
        return data;
    }

    private TicketBasicSettingsRequest resolveDraftBasicRequest(TicketSettingsRequest request) {
        if (request == null) {
            return null;
        }
        return request.getBasicSettings();
    }

    private String resolveProcessUidOrDefault(TicketSettingsRequest request, String orgUid, String normalizedType) {
        if (request != null && StringUtils.hasText(request.getProcessUid())) {
            return request.getProcessUid();
        }
        return resolveDefaultProcessUid(orgUid, normalizedType);
    }

    private String resolveFormUidOrDefault(TicketSettingsRequest request, String orgUid, String normalizedType) {
        if (request != null && StringUtils.hasText(request.getFormUid())) {
            return request.getFormUid();
        }
        return resolveDefaultFormUid(orgUid, normalizedType);
    }

    private String resolveDefaultProcessUid(String orgUid, String normalizedType) {
        if (!StringUtils.hasText(orgUid)) {
            return null;
        }
        ProcessTypeEnum processType = mapTicketTypeToProcessType(normalizedType);
        if (processType == null) {
            return null;
        }
        // 通过 Utils.formatUid 生成默认的 processUid
        String defaultProcessUid = processType == ProcessTypeEnum.TICKET_EXTERNAL
                ? Utils.formatUid(orgUid,
                        TicketConsts.TICKET_PROCESS_KEY + TicketConsts.TICKET_EXTERNAL_PROCESS_UID_SUFFIX)
                : Utils.formatUid(orgUid, TicketConsts.TICKET_PROCESS_KEY);
        return ticketProcessRepository
                .findByUidAndOrgUidAndType(defaultProcessUid, orgUid, processType.name())
                .map(ProcessEntity::getUid)
                .orElse(null);
    }

    private String resolveDefaultFormUid(String orgUid, String normalizedType) {
        if (!StringUtils.hasText(orgUid)) {
            return null;
        }
        FormTypeEnum formType = mapTicketTypeToFormType(normalizedType);
        if (formType == null) {
            return null;
        }
        return formRepository
                .findFirstByOrgUidAndTypeAndDeletedFalseOrderByCreatedAtAsc(orgUid, formType.name())
                .map(FormEntity::getUid)
                .orElse(null);
    }

    private ProcessTypeEnum mapTicketTypeToProcessType(String normalizedType) {
        TicketTypeEnum ticketType = TicketTypeEnum.fromValue(normalizedType);
        if (TicketTypeEnum.INTERNAL.equals(ticketType)) {
            return ProcessTypeEnum.TICKET_INTERNAL;
        }
        if (TicketTypeEnum.EXTERNAL.equals(ticketType)) {
            return ProcessTypeEnum.TICKET_EXTERNAL;
        }
        return null;
    }

    private FormTypeEnum mapTicketTypeToFormType(String normalizedType) {
        TicketTypeEnum ticketType = TicketTypeEnum.fromValue(normalizedType);
        if (TicketTypeEnum.INTERNAL.equals(ticketType)) {
            return FormTypeEnum.TICKET_INTERNAL;
        }
        if (TicketTypeEnum.EXTERNAL.equals(ticketType)) {
            return FormTypeEnum.TICKET_EXTERNAL;
        }
        return null;
    }

    private ProcessEntity resolveProcessReference(String processUid, String orgUid) {
        if (!StringUtils.hasText(processUid)) {
            return null;
        }
        ProcessEntity process = ticketProcessRepository.findByUid(processUid)
                .orElseThrow(() -> new NotFoundException("Ticket process not found: " + processUid));
        if (StringUtils.hasText(orgUid) && StringUtils.hasText(process.getOrgUid())
                && !Objects.equals(orgUid, process.getOrgUid())) {
            throw new NotFoundException("Ticket process not found: " + processUid);
        }
        return process;
    }

    private FormEntity resolveFormReference(String formUid, String orgUid) {
        if (!StringUtils.hasText(formUid)) {
            return null;
        }
        FormEntity form = formRepository.findByUid(formUid)
                .orElseThrow(() -> new NotFoundException("Form not found: " + formUid));
        if (StringUtils.hasText(orgUid) && StringUtils.hasText(form.getOrgUid())
                && !Objects.equals(orgUid, form.getOrgUid())) {
            throw new NotFoundException("Form not found: " + formUid);
        }
        return form;
    }

    private TicketBasicSettingsEntity createBasicSettingsEntity(TicketBasicSettingsRequest request, String orgUid) {
        TicketBasicSettingsEntity entity = TicketBasicSettingsEntity.fromRequest(request, modelMapper);
        entity.setUid(uidUtils.getUid());
        entity.setOrgUid(orgUid);
        return entity;
    }

    private void applyBasicSettingsRequest(TicketBasicSettingsEntity target, TicketBasicSettingsRequest request) {
        if (target == null || request == null) {
            return;
        }
        if (request.getNumberPrefix() != null) {
            target.setNumberPrefix(request.getNumberPrefix());
        }
        if (request.getNumberLength() != null) {
            target.setNumberLength(request.getNumberLength());
        }
        if (request.getDefaultPriority() != null) {
            target.setDefaultPriority(request.getDefaultPriority());
        }
        if (request.getValidityDays() != null) {
            target.setValidityDays(request.getValidityDays());
        }
        if (request.getAutoCloseHours() != null) {
            target.setAutoCloseHours(request.getAutoCloseHours());
        }
        if (request.getEnableAutoClose() != null) {
            target.setEnableAutoClose(request.getEnableAutoClose());
        }
        if (request.getRequireLogin() != null) {
            target.setRequireLogin(request.getRequireLogin());
        }
        if (request.getAssignmentMode() != null) {
            target.setAssignmentMode(request.getAssignmentMode());
        }

        // 工单提示语配置
        if (request.getAccessTip() != null) {
            target.setAccessTip(request.getAccessTip());
        }
        if (request.getCloseTip() != null) {
            target.setCloseTip(request.getCloseTip());
        }

        // 联系方式字段配置
        if (request.getShowContactName() != null) {
            target.setShowContactName(request.getShowContactName());
        }
        if (request.getRequireContactName() != null) {
            target.setRequireContactName(request.getRequireContactName());
        }
        if (request.getShowEmail() != null) {
            target.setShowEmail(request.getShowEmail());
        }
        if (request.getRequireEmail() != null) {
            target.setRequireEmail(request.getRequireEmail());
        }
        if (request.getShowPhone() != null) {
            target.setShowPhone(request.getShowPhone());
        }
        if (request.getRequirePhone() != null) {
            target.setRequirePhone(request.getRequirePhone());
        }
        if (request.getShowWechat() != null) {
            target.setShowWechat(request.getShowWechat());
        }
        if (request.getRequireWechat() != null) {
            target.setRequireWechat(request.getRequireWechat());
        }

        // 智能工单生成
        if (request.getEnableSmartTicketGenerate() != null) {
            target.setEnableSmartTicketGenerate(request.getEnableSmartTicketGenerate());
        }
        if (request.getSmartTicketRobotUid() != null || !StringUtils.hasText(target.getSmartTicketRobotUid())) {
            target.setSmartTicketRobotUid(request.getSmartTicketRobotUid());
        }
    }

    private void copyBasicSettings(TicketBasicSettingsEntity source, TicketBasicSettingsEntity target) {
        if (source == null || target == null) {
            return;
        }
        target.setNumberPrefix(source.getNumberPrefix());
        target.setNumberLength(source.getNumberLength());
        target.setDefaultPriority(source.getDefaultPriority());
        target.setValidityDays(source.getValidityDays());
        target.setAutoCloseHours(source.getAutoCloseHours());
        target.setEnableAutoClose(source.getEnableAutoClose());
        target.setRequireLogin(source.getRequireLogin());
        target.setAssignmentMode(source.getAssignmentMode());

        // 工单提示语配置
        target.setAccessTip(source.getAccessTip());
        target.setCloseTip(source.getCloseTip());

        // 联系方式字段配置
        target.setShowContactName(source.getShowContactName());
        target.setRequireContactName(source.getRequireContactName());
        target.setShowEmail(source.getShowEmail());
        target.setRequireEmail(source.getRequireEmail());
        target.setShowPhone(source.getShowPhone());
        target.setRequirePhone(source.getRequirePhone());
        target.setShowWechat(source.getShowWechat());
        target.setRequireWechat(source.getRequireWechat());

        // 智能工单生成
        target.setEnableSmartTicketGenerate(source.getEnableSmartTicketGenerate());
        target.setSmartTicketRobotUid(source.getSmartTicketRobotUid());
    }

    private TicketBasicSettingsResponse mapBasicSettings(TicketBasicSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return TicketBasicSettingsResponse.builder()
                .uid(entity.getUid())
                .numberPrefix(entity.getNumberPrefix())
                .numberLength(entity.getNumberLength())
                .defaultPriority(entity.getDefaultPriority())
                .validityDays(entity.getValidityDays())
                .autoCloseHours(entity.getAutoCloseHours())
                .enableAutoClose(entity.getEnableAutoClose())
                .requireLogin(entity.getRequireLogin())
                .assignmentMode(entity.getAssignmentMode())

                // 工单提示语配置
                .accessTip(entity.getAccessTip())
                .closeTip(entity.getCloseTip())

                // 联系方式字段配置
                .showContactName(entity.getShowContactName())
                .requireContactName(entity.getRequireContactName())
                .showEmail(entity.getShowEmail())
                .requireEmail(entity.getRequireEmail())
                .showPhone(entity.getShowPhone())
                .requirePhone(entity.getRequirePhone())
                .showWechat(entity.getShowWechat())
                .requireWechat(entity.getRequireWechat())

                // 智能工单生成
                .enableSmartTicketGenerate(entity.getEnableSmartTicketGenerate())
                .smartTicketRobotUid(entity.getSmartTicketRobotUid())
                .build();
    }

    private TicketCategorySettingsData copyCategorySettings(TicketCategorySettingsData source) {
        if (source == null) {
            TicketCategorySettingsData copy = TicketCategorySettingsData.builder().build();
            copy.normalize();
            return copy;
        }
        List<TicketCategoryItemData> copiedItems = source.getItems() == null
                ? new ArrayList<>()
                : source.getItems().stream()
                        .map(item -> TicketCategoryItemData.builder()
                                .uid(item.getUid())
                                .name(item.getName())
                                .description(item.getDescription())
                                .enabled(item.getEnabled())
                                .defaultCategory(item.getDefaultCategory())
                                .orderIndex(item.getOrderIndex())
                                .build())
                        .collect(Collectors.toList());
        TicketCategorySettingsData copy = TicketCategorySettingsData.builder()
                .items(copiedItems)
                .build();
        copy.normalize();
        return copy;
    }

    private TicketCategorySettingsResponse mapCategorySettings(TicketCategorySettingsEntity entity) {
        if (entity == null || entity.getContent() == null) {
            return null;
        }
        TicketCategorySettingsData content = entity.getContent();
        List<TicketCategoryItemResponse> items = content.getItems() == null
                ? new ArrayList<>()
                : content.getItems().stream()
                        .map(item -> TicketCategoryItemResponse.builder()
                                .uid(item.getUid())
                                .name(item.getName())
                                .description(item.getDescription())
                                .enabled(item.getEnabled())
                                .defaultCategory(item.getDefaultCategory())
                                .orderIndex(item.getOrderIndex())
                                .build())
                        .collect(Collectors.toList());
        return TicketCategorySettingsResponse.builder()
                .items(items)
                .defaultCategoryUid(content.resolveDefaultUid())
                .enabledCount(content.countEnabled())
                .disabledCount(content.countDisabled())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TicketNotificationSettingsResponse mapNotificationSettings(TicketNotificationSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return TicketNotificationSettingsResponse.builder()
                .uid(entity.getUid())
                .emailEnabled(entity.getEmailEnabled())
                .emailProviderUid(entity.getEmailProviderUid())
                .emailEvents(entity.getEmailEvents())
                .emailTemplates(entity.getEmailTemplates())
                .internalEnabled(entity.getInternalEnabled())
                .internalEvents(entity.getInternalEvents())
                .webhookEnabled(entity.getWebhookEnabled())
                .webhookUrl(entity.getWebhookUrl())
                .webhookEvents(entity.getWebhookEvents())
                .smsEnabled(entity.getSmsEnabled())
                .smsProviderUid(entity.getSmsProviderUid())
                .smsEvents(entity.getSmsEvents())
                .smsTemplateIds(entity.getSmsTemplateIds())
                .emailNotifyWhenOnline(entity.getEmailNotifyWhenOnline())
                .smsNotifyWhenOnline(entity.getSmsNotifyWhenOnline())
                .apnsEnabled(entity.getApnsEnabled())
                .build();
    }

    private TicketAutoCreateSettingsResponse mapAutoCreateSettings(TicketAutoCreateSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        return TicketAutoCreateSettingsResponse.builder()
                .uid(entity.getUid())
                .enabled(entity.getEnabled())
                .closeTypes(entity.getCloseTypes() == null
                        ? TicketAutoCreateSettingsEntity.defaultCloseTypes()
                        : new ArrayList<>(entity.getCloseTypes()))
                .minVisitorMessageCount(entity.getMinVisitorMessageCount())
                .minRobotMessageCount(entity.getMinRobotMessageCount())
                .requireAiUnresolved(entity.getRequireAiUnresolved())
                .requireAgentOffline(entity.getRequireAgentOffline())
                .skipIfTicketExists(entity.getSkipIfTicketExists())
                .autoTicketRobotUid(entity.getAutoTicketRobotUid())
                .build();
    }

    private TicketVisibilitySettingsData copyVisibilitySettingsData(TicketVisibilitySettingsData source) {
        TicketVisibilitySettingsData copy = source == null
                ? TicketVisibilitySettingsData.builder().build()
                : TicketVisibilitySettingsData.builder()
                        .mode(source.getMode())
                        .categoryRules(source.getCategoryRules() == null ? new ArrayList<>()
                                : source.getCategoryRules().stream()
                                        .map(rule -> TicketVisibilityCategoryRuleData.builder()
                                                .categoryUid(rule.getCategoryUid())
                                                .visibility(rule.getVisibility())
                                                .build())
                                        .collect(Collectors.toList()))
                        .build();
        copy.normalize();
        return copy;
    }

    private void copyVisibilitySettings(TicketVisibilitySettingsEntity source, TicketVisibilitySettingsEntity target) {
        if (source == null || target == null) {
            return;
        }
        target.setContent(copyVisibilitySettingsData(source.getContent()));
    }

    private TicketVisibilitySettingsResponse mapVisibilitySettings(TicketVisibilitySettingsEntity entity) {
        if (entity == null || entity.getContent() == null) {
            return null;
        }
        TicketVisibilitySettingsData content = entity.getContent();
        return TicketVisibilitySettingsResponse.builder()
                .uid(entity.getUid())
                .mode(content.getMode())
                .categoryRules(content.getCategoryRules() == null ? new ArrayList<>()
                        : content.getCategoryRules().stream()
                                .map(rule -> TicketVisibilityCategoryRuleResponse.builder()
                                        .categoryUid(rule.getCategoryUid())
                                        .visibility(rule.getVisibility())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    private TicketSlaSettingsResponse mapSlaSettings(TicketSlaSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        List<TicketSlaRuleResponse> rules = entity.getRules() == null ? new ArrayList<>()
                : entity.getRules().stream()
                        .map(rule -> TicketSlaRuleResponse.builder()
                                .uid(rule.getUid())
                                .slaType(rule.getSlaType())
                                .priority(rule.getPriority())
                                .categoryUid(rule.getCategoryUid())
                                .durationMinutes(rule.getDurationMinutes())
                                .warningMinutes(rule.getWarningMinutes())
                                .enabled(rule.getEnabled())
                                .orderIndex(rule.getOrderIndex())
                                .build())
                        .collect(Collectors.toList());
        return TicketSlaSettingsResponse.builder()
                .uid(entity.getUid())
                .enabled(entity.getEnabled())
                .businessHoursEnabled(entity.getBusinessHoursEnabled())
                .businessHoursStartTime(entity.getBusinessHoursStartTime())
                .businessHoursEndTime(entity.getBusinessHoursEndTime())
                .businessHoursTimezone(entity.getBusinessHoursTimezone())
                .businessHoursCountryCode(entity.getBusinessHoursCountryCode())
                .pauseOnHold(entity.getPauseOnHold())
                .notifyOnBreach(entity.getNotifyOnBreach())
                .autoEscalateEnabled(entity.getAutoEscalateEnabled())
                .escalateAssigneeUid(entity.getEscalateAssigneeUid())
                .autoCloseCustomerPendingEnabled(entity.getAutoCloseCustomerPendingEnabled())
                .customerVerifyAutoCloseHours(entity.getCustomerVerifyAutoCloseHours())
                .warningPercent(entity.getWarningPercent())
                .rules(rules)
                .build();
    }

    private void applyNotificationSettings(TicketNotificationSettingsEntity target,
            TicketNotificationSettingsEntity source) {
        if (target == null || source == null)
            return;
        if (source.getEmailEnabled() != null)
            target.setEmailEnabled(source.getEmailEnabled());
        if (source.getEmailProviderUid() != null)
            target.setEmailProviderUid(source.getEmailProviderUid());
        if (source.getEmailEvents() != null)
            target.setEmailEvents(source.getEmailEvents());
        if (source.getEmailTemplates() != null)
            target.setEmailTemplates(source.getEmailTemplates());
        if (source.getInternalEnabled() != null)
            target.setInternalEnabled(source.getInternalEnabled());
        if (source.getInternalEvents() != null)
            target.setInternalEvents(source.getInternalEvents());
        if (source.getWebhookEnabled() != null)
            target.setWebhookEnabled(source.getWebhookEnabled());
        if (source.getWebhookUrl() != null)
            target.setWebhookUrl(source.getWebhookUrl());
        if (source.getWebhookEvents() != null)
            target.setWebhookEvents(source.getWebhookEvents());
        if (source.getSmsEnabled() != null)
            target.setSmsEnabled(source.getSmsEnabled());
        if (source.getSmsProviderUid() != null)
            target.setSmsProviderUid(source.getSmsProviderUid());
        if (source.getSmsEvents() != null)
            target.setSmsEvents(source.getSmsEvents());
        if (source.getSmsTemplateIds() != null)
            target.setSmsTemplateIds(source.getSmsTemplateIds());
        if (source.getEmailNotifyWhenOnline() != null)
            target.setEmailNotifyWhenOnline(source.getEmailNotifyWhenOnline());
        if (source.getSmsNotifyWhenOnline() != null)
            target.setSmsNotifyWhenOnline(source.getSmsNotifyWhenOnline());
    }

    private void copyNotificationSettings(TicketNotificationSettingsEntity source,
            TicketNotificationSettingsEntity target) {
        if (source == null || target == null)
            return;
        target.setEmailEnabled(source.getEmailEnabled());
        target.setEmailProviderUid(source.getEmailProviderUid());
        target.setEmailEvents(source.getEmailEvents() != null ? new java.util.ArrayList<>(source.getEmailEvents())
                : new java.util.ArrayList<>());
        target.setEmailTemplates(
                source.getEmailTemplates() != null ? new java.util.ArrayList<>(source.getEmailTemplates())
                        : new java.util.ArrayList<>());
        target.setInternalEnabled(source.getInternalEnabled());
        target.setInternalEvents(
                source.getInternalEvents() != null ? new java.util.ArrayList<>(source.getInternalEvents())
                        : new java.util.ArrayList<>());
        target.setWebhookEnabled(source.getWebhookEnabled());
        target.setWebhookUrl(source.getWebhookUrl());
        target.setWebhookEvents(source.getWebhookEvents() != null ? new java.util.ArrayList<>(source.getWebhookEvents())
                : new java.util.ArrayList<>());
        target.setSmsEnabled(source.getSmsEnabled());
        target.setSmsProviderUid(source.getSmsProviderUid());
        target.setSmsEvents(source.getSmsEvents() != null ? new java.util.ArrayList<>(source.getSmsEvents())
                : new java.util.ArrayList<>());
        target.setSmsTemplateIds(
                source.getSmsTemplateIds() != null ? new java.util.HashMap<>(source.getSmsTemplateIds())
                        : new java.util.HashMap<>());
        target.setEmailNotifyWhenOnline(source.getEmailNotifyWhenOnline());
        target.setSmsNotifyWhenOnline(source.getSmsNotifyWhenOnline());
    }

    private void copySlaSettings(TicketSlaSettingsEntity source, TicketSlaSettingsEntity target) {
        if (source == null || target == null)
            return;
        target.setEnabled(source.getEnabled());
        target.setBusinessHoursEnabled(source.getBusinessHoursEnabled());
        target.setBusinessHoursStartTime(source.getBusinessHoursStartTime());
        target.setBusinessHoursEndTime(source.getBusinessHoursEndTime());
        target.setBusinessHoursTimezone(source.getBusinessHoursTimezone());
        target.setBusinessHoursCountryCode(source.getBusinessHoursCountryCode());
        target.setPauseOnHold(source.getPauseOnHold());
        target.setNotifyOnBreach(source.getNotifyOnBreach());
        target.setAutoEscalateEnabled(source.getAutoEscalateEnabled());
        target.setEscalateAssigneeUid(source.getEscalateAssigneeUid());
        target.setAutoCloseCustomerPendingEnabled(source.getAutoCloseCustomerPendingEnabled());
        target.setCustomerVerifyAutoCloseHours(source.getCustomerVerifyAutoCloseHours());
        target.setWarningPercent(source.getWarningPercent());
        target.getRules().clear();
        if (source.getRules() != null) {
            for (TicketSlaRuleEntity rule : source.getRules()) {
                target.getRules().add(TicketSlaRuleEntity.builder()
                        .uid(uidUtils.getUid())
                        .slaType(rule.getSlaType())
                        .priority(rule.getPriority())
                        .categoryUid(rule.getCategoryUid())
                        .durationMinutes(rule.getDurationMinutes())
                        .warningMinutes(rule.getWarningMinutes())
                        .enabled(rule.getEnabled())
                        .orderIndex(rule.getOrderIndex())
                        .build());
            }
        }
    }

    private void applyAutoCreateSettingsRequest(TicketAutoCreateSettingsEntity target,
            TicketAutoCreateSettingsRequest request, String orgUid) {
        if (target == null || request == null) {
            return;
        }
        TicketAutoCreateSettingsEntity.applyRequest(target, request);
    }

    private void copyAutoCreateSettings(TicketAutoCreateSettingsEntity source,
            TicketAutoCreateSettingsEntity target, String orgUid) {
        if (source == null || target == null) {
            return;
        }
        target.setEnabled(source.getEnabled());
        target.setCloseTypes(source.getCloseTypes() == null
                ? TicketAutoCreateSettingsEntity.defaultCloseTypes()
                : new ArrayList<>(source.getCloseTypes()));
        target.setMinVisitorMessageCount(source.getMinVisitorMessageCount());
        target.setMinRobotMessageCount(source.getMinRobotMessageCount());
        target.setRequireAiUnresolved(source.getRequireAiUnresolved());
        target.setRequireAgentOffline(source.getRequireAgentOffline());
        target.setSkipIfTicketExists(source.getSkipIfTicketExists());
        target.setAutoTicketRobotUid(source.getAutoTicketRobotUid());
    }

    private ProcessResponse mapProcess(ProcessEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProcessResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .schema(entity.getSchema())
                .deploymentId(entity.getDeploymentId())
                .build();
    }

    private FormResponse mapForm(FormEntity entity) {
        if (entity == null) {
            return null;
        }
        return FormResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .status(entity.getStatus())
                .schema(entity.getSchema())
                // .enabled(entity.getEnabled())
                // .categoryUid(entity.getCategoryUid())
                .build();
    }

    @Override
    public TicketSettingsResponse convertToResponse(TicketSettingsEntity entity) {
        TicketSettingsResponse resp = modelMapper.map(entity, TicketSettingsResponse.class);
        // 基础设置
        resp.setBasicSettings(mapBasicSettings(entity.getBasicSettings()));
        resp.setDraftBasicSettings(mapBasicSettings(entity.getDraftBasicSettings()));
        // 分类设置
        resp.setCategorySettings(mapCategorySettings(entity.getCategorySettings()));
        resp.setDraftCategorySettings(mapCategorySettings(entity.getDraftCategorySettings()));
        // 流程与表单
        resp.setProcess(mapProcess(entity.getProcess()));
        resp.setDraftProcess(mapProcess(entity.getDraftProcess()));
        resp.setForm(mapForm(entity.getForm()));
        resp.setDraftForm(mapForm(entity.getDraftForm()));

        // 通知设置
        resp.setNotificationSettings(mapNotificationSettings(entity.getNotificationSettings()));
        resp.setDraftNotificationSettings(mapNotificationSettings(entity.getDraftNotificationSettings()));

        resp.setSlaSettings(mapSlaSettings(entity.getSlaSettings()));
        resp.setDraftSlaSettings(mapSlaSettings(entity.getDraftSlaSettings()));

        resp.setAutoCreateSettings(mapAutoCreateSettings(entity.getAutoCreateSettings()));
        resp.setDraftAutoCreateSettings(mapAutoCreateSettings(entity.getDraftAutoCreateSettings()));

        resp.setVisibilitySettings(mapVisibilitySettings(entity.getVisibilitySettings()));
        resp.setDraftVisibilitySettings(mapVisibilitySettings(entity.getDraftVisibilitySettings()));

        return resp;
    }

    public Optional<TicketSettingsEntity> findDefaultByOrgUidAndType(String orgUid, String type) {
        List<TicketSettingsEntity> settings = ticketSettingsRepository.findByOrgUidAndTypeAndIsDefaultTrue(orgUid,
                type);
        if (settings == null || settings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(settings.get(0));
    }

    @Override
    public TicketSettingsExcel convertToExcel(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsExcel.class);
    }

    private String resolveSettingsType(String rawType) {
        return TicketTypeEnum.fromValue(rawType).name();
    }

    /**
     * 保证同一个 orgUid 下仅有一个 isDefault=true（参考 WorkgroupSettingsRestService）
     */
    private void ensureSingleDefault(String orgUid, String type, TicketSettingsEntity target) {
        if (!StringUtils.hasText(orgUid) || target == null) {
            return;
        }
        Optional<TicketSettingsEntity> currentOpt = ticketSettingsRepository.findDefaultForUpdate(orgUid, type);
        if (currentOpt.isPresent()) {
            TicketSettingsEntity current = currentOpt.get();
            if (!current.getUid().equals(target.getUid())) {
                current.setIsDefault(false);
                ticketSettingsRepository.save(current);
            }
        }
        List<TicketSettingsEntity> legacyDefaults = ticketSettingsRepository.findByOrgUidAndIsDefaultTrue(orgUid);
        if (legacyDefaults != null) {
            for (TicketSettingsEntity candidate : legacyDefaults) {
                if (candidate == null || candidate.getUid().equals(target.getUid())) {
                    continue;
                }
                boolean sameOrLegacyType = !StringUtils.hasText(candidate.getType())
                        || candidate.getType().equals(type);
                if (sameOrLegacyType && Boolean.TRUE.equals(candidate.getIsDefault())) {
                    candidate.setIsDefault(false);
                    ticketSettingsRepository.save(candidate);
                }
            }
        }
        target.setIsDefault(true);
    }

    // ========== 测试邮件 / 短信 ==========

    /**
     * 发送测试邮件，使用指定的邮件供应商。
     */
    public JsonResult<Boolean> sendTestEmail(String emailProviderUid, String to) {
        if (!StringUtils.hasText(emailProviderUid)) {
            return JsonResult.error("请选择邮件供应商");
        }
        if (!StringUtils.hasText(to)) {
            return JsonResult.error("请输入测试收件邮箱");
        }
        Optional<EmailProviderEntity> emailOpt = emailProviderRepository.findByUid(emailProviderUid);
        if (emailOpt.isEmpty() || emailOpt.get().isDeleted() || !Boolean.TRUE.equals(emailOpt.get().getEnabled())) {
            return JsonResult.error("邮件供应商不存在或已禁用");
        }
        String subject = "【微语客服】测试邮件";
        String html = "<h3>测试邮件</h3><p>这是一封来自微语客服系统的测试邮件，如果您收到此邮件，说明邮件配置正确。</p>";
        String orgUid = authService.getCurrentUser().getOrgUid();
        var result = emailPushSendService.sendEmail(emailOpt.get(), to, subject, html, orgUid);
        if (result.isSuccess()) {
            return JsonResult.success("测试邮件发送成功");
        }
        return JsonResult.error("测试邮件发送失败: " + (result.getErrorMessage() != null ? result.getErrorMessage() : "未知错误"));
    }

    /**
     * 发送测试短信，直接传入模板编码和签名。
     */
    public JsonResult<Boolean> sendTestSms(String to, String country, String signName, String templateCode) {
        if (!StringUtils.hasText(to)) {
            return JsonResult.error("请输入测试手机号");
        }
        if (!StringUtils.hasText(templateCode)) {
            return JsonResult.error("请选择短信模板");
        }
        if (!StringUtils.hasText(signName)) {
            return JsonResult.error("短信签名缺失");
        }
        String mobileCountry = StringUtils.hasText(country) ? country : "86";
        java.util.Map<String, String> testParams = new java.util.HashMap<>();
        testParams.put("name", "测试用户");
        testParams.put("ticketNumber", "TEST001");
        testParams.put("status", "测试");
        String orgUid = authService.getCurrentUser().getOrgUid();
        var result = smsPushSendService.sendSmsWithTemplate(to, mobileCountry, signName, templateCode, testParams,
                orgUid);
        if (result.isSuccess()) {
            return JsonResult.success("测试短信发送成功");
        }
        return JsonResult.error("测试短信发送失败: " + (result.getErrorMessage() != null ? result.getErrorMessage() : "未知错误"));
    }

}
