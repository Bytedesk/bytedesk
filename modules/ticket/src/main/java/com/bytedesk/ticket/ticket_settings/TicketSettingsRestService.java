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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.form.FormEntity;
import com.bytedesk.service.form.FormRepository;
import com.bytedesk.service.form.FormResponse;
import com.bytedesk.service.form.FormTypeEnum;
import com.bytedesk.ticket.process.TicketProcessEntity;
import com.bytedesk.ticket.process.TicketProcessRepository;
import com.bytedesk.ticket.process.TicketProcessResponse;
import com.bytedesk.ticket.process.TicketProcessTypeEnum;
import com.bytedesk.ticket.ticket.TicketConsts;
import com.bytedesk.ticket.ticket.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsRequest;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsResponse;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBindingEntity;
import com.bytedesk.ticket.ticket_settings_binding.TicketSettingsBindingRepository;
import com.bytedesk.ticket.ticket_settings_category.CategoryItemData;
import com.bytedesk.ticket.ticket_settings_category.CategorySettingsData;
import com.bytedesk.ticket.ticket_settings_category.TicketCategoryItemResponse;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsEntity;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings_category.TicketCategorySettingsResponse;

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

    private final TicketProcessRepository ticketProcessRepository;

    private final FormRepository formRepository;

    @Override
    protected Specification<TicketSettingsEntity> createSpecification(TicketSettingsRequest request) {
        return TicketSettingsSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketSettingsEntity> executePageQuery(Specification<TicketSettingsEntity> spec, Pageable pageable) {
        return ticketSettingsRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "ticketSettings", key = "#uid", unless = "#result==null")
    @Override
    public Optional<TicketSettingsEntity> findByUid(String uid) {
        return ticketSettingsRepository.findByUid(uid);
    }

    @Cacheable(value = "ticketSettings", key = "#name + '_' + #orgUid + '_' + #type", unless = "#result==null")
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

        TicketCategorySettingsEntity category = TicketCategorySettingsEntity.fromRequest(request.getCategorySettings(),
                uidUtils::getUid);
        category.setUid(uidUtils.getUid());
        entity.setCategorySettings(category);

        TicketCategorySettingsEntity draftCategory = TicketCategorySettingsEntity
            .fromRequest(resolveDraftCategoryRequest(request), uidUtils::getUid);
        draftCategory.setUid(uidUtils.getUid());
        entity.setDraftCategorySettings(draftCategory);

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
            entity.setDescription(request.getDescription() != null ? request.getDescription() : entity.getDescription());
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
                    entity.setDraftCategorySettings(draftCategory);
                } else {
                    draftCategory.replaceFromRequest(request.getCategorySettings(), uidUtils::getUid);
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
    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid) {
        return getOrDefaultByWorkgroup(orgUid, workgroupUid, TicketTypeEnum.EXTERNAL.name());
    }

    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        // 1) 已绑定则直接返回
        if (TicketTypeEnum.EXTERNAL.name().equals(normalizedType)) {
            Optional<TicketSettingsBindingEntity> bindingOpt = bindingRepository
                    .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
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
     * Resolve ticket settings entity by workgroup/org for downstream domain services.
     */
    @Transactional
    public TicketSettingsEntity resolveEntityByWorkgroup(String orgUid, String workgroupUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        if (TicketTypeEnum.EXTERNAL.name().equals(normalizedType)) {
            Optional<TicketSettingsBindingEntity> bindingOpt = bindingRepository
                    .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
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

    /** 获取或创建组织默认 TicketSettings（发布+草稿齐全，保证并发唯一） */
    @Transactional
    public TicketSettingsEntity getOrCreateDefault(String orgUid) {
        return getOrCreateDefault(orgUid, TicketTypeEnum.EXTERNAL.name());
    }

    @Transactional
    public TicketSettingsEntity getOrCreateDefault(String orgUid, String rawType) {
        String normalizedType = resolveSettingsType(rawType);
        Optional<TicketSettingsEntity> existing = ticketSettingsRepository.findDefaultForUpdate(orgUid, normalizedType);
        if (existing.isPresent())
            return existing.get();

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
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .type(normalizedType)
                .name("默认工单配置")
                .description("系统默认工单配置")
                .isDefault(true)
                .enabled(true)
            .customFormEnabled(false)
                .build();

        TicketCategorySettingsEntity category = TicketCategorySettingsEntity.fromRequest(null, uidUtils::getUid);
        category.setUid(uidUtils.getUid());
        settings.setCategorySettings(category);

        TicketCategorySettingsEntity draftCategory = TicketCategorySettingsEntity.fromRequest(null, uidUtils::getUid);
        draftCategory.setUid(uidUtils.getUid());
        settings.setDraftCategorySettings(draftCategory);

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

        if (request.getProcessUid() != null) {
            entity.setDraftProcess(resolveProcessReference(request.getProcessUid(), entity.getOrgUid()));
            draftUpdated = true;
        }

        if (request.getFormUid() != null) {
            entity.setDraftForm(resolveFormReference(request.getFormUid(), entity.getOrgUid()));
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
                entity.setCategorySettings(publishedCategory);
            }
            if (draftCategory.getContent() != null) {
                draftCategory.getContent().normalize();
                publishedCategory.setContent(copyCategorySettings(draftCategory.getContent()));
            } else {
                publishedCategory.setContent(CategorySettingsData.builder().build());
            }
        }

        // 同步流程及表单引用
        entity.setProcess(entity.getDraftProcess());
        entity.setForm(entity.getDraftForm());

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
        TicketProcessTypeEnum processType = mapTicketTypeToProcessType(normalizedType);
        if (processType == null) {
            return null;
        }
        return ticketProcessRepository
                .findByKeyAndOrgUidAndType(TicketConsts.TICKET_PROCESS_KEY, orgUid, processType.name())
                .map(TicketProcessEntity::getUid)
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

    private TicketProcessTypeEnum mapTicketTypeToProcessType(String normalizedType) {
        TicketTypeEnum ticketType = TicketTypeEnum.fromValue(normalizedType);
        if (TicketTypeEnum.INTERNAL.equals(ticketType)) {
            return TicketProcessTypeEnum.TICKET_INTERNAL;
        }
        if (TicketTypeEnum.EXTERNAL.equals(ticketType)) {
            return TicketProcessTypeEnum.TICKET_EXTERNAL;
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

    private TicketProcessEntity resolveProcessReference(String processUid, String orgUid) {
        if (!StringUtils.hasText(processUid)) {
            return null;
        }
        TicketProcessEntity process = ticketProcessRepository.findByUid(processUid)
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
                .build();
    }

    private CategorySettingsData copyCategorySettings(CategorySettingsData source) {
        if (source == null) {
            CategorySettingsData copy = CategorySettingsData.builder().build();
            copy.normalize();
            return copy;
        }
        List<CategoryItemData> copiedItems = source.getItems() == null
                ? new ArrayList<>()
                : source.getItems().stream()
                        .map(item -> CategoryItemData.builder()
                                .uid(item.getUid())
                                .name(item.getName())
                                .description(item.getDescription())
                                .enabled(item.getEnabled())
                                .defaultCategory(item.getDefaultCategory())
                                .orderIndex(item.getOrderIndex())
                                .build())
                        .collect(Collectors.toList());
        CategorySettingsData copy = CategorySettingsData.builder()
                .items(copiedItems)
                .build();
        copy.normalize();
        return copy;
    }

    private TicketCategorySettingsResponse mapCategorySettings(TicketCategorySettingsEntity entity) {
        if (entity == null || entity.getContent() == null) {
            return null;
        }
        CategorySettingsData content = entity.getContent();
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

    private TicketProcessResponse mapProcess(TicketProcessEntity entity) {
        if (entity == null) {
            return null;
        }
        return TicketProcessResponse.builder()
                .uid(entity.getUid())
                .name(entity.getName())
                .key(entity.getKey())
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
                .enabled(entity.getEnabled())
                .categoryUid(entity.getCategoryUid())
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

        return resp;
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

}
