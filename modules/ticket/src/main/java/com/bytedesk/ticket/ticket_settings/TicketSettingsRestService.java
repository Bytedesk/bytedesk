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
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingEntity;
import com.bytedesk.ticket.ticket_settings.binding.TicketSettingsBindingRepository;
import com.bytedesk.ticket.ticket_settings.sub.TicketCategorySettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategorySettingsRequest;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategorySettingsResponse;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketCategoryItemResponse;
import com.bytedesk.ticket.ticket_settings.sub.model.CategoryItemData;
import com.bytedesk.ticket.ticket_settings.sub.model.CategorySettingsData;

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
    public Optional<TicketSettingsEntity> findByNameAndOrgUid(String name, String orgUid) {
        return ticketSettingsRepository.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return ticketSettingsRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TicketSettingsResponse create(TicketSettingsRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid())) {
            Optional<TicketSettingsEntity> ticketSettings = findByNameAndOrgUid(request.getName(), request.getOrgUid());
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
        // 赋 UID
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }

        // 初始化并绑定发布 + 草稿子配置，参考 WorkgroupSettings 的 create 逻辑
        // Basic
        // TicketBasicSettingsEntity basic = TicketBasicSettingsEntity.fromRequest(request.getBasicSettings(), modelMapper);
        // basic.setUid(uidUtils.getUid());
        // entity.setBasicSettings(basic);
        // TicketBasicSettingsEntity draftBasic = TicketBasicSettingsEntity.fromRequest(request.getBasicSettings(), modelMapper);
        // draftBasic.setUid(uidUtils.getUid());
        // entity.setDraftBasicSettings(draftBasic);

        TicketCategorySettingsEntity category = TicketCategorySettingsEntity
            .fromRequest(request.getCategorySettings(), uidUtils::getUid);
        category.setUid(uidUtils.getUid());
        entity.setCategorySettings(category);

        TicketCategorySettingsEntity draftCategory = TicketCategorySettingsEntity
            .fromRequest(resolveDraftCategoryRequest(request), uidUtils::getUid);
        draftCategory.setUid(uidUtils.getUid());
        entity.setDraftCategorySettings(draftCategory);

        // // StatusFlow
        // TicketStatusFlowSettingsEntity statusFlow = new TicketStatusFlowSettingsEntity();
        // statusFlow.setUid(uidUtils.getUid());
        // TicketStatusFlowSettingsEntity draftStatusFlow = TicketStatusFlowSettingsEntity
        //         .fromRequest(request.getStatusFlowSettings());
        // draftStatusFlow.setUid(uidUtils.getUid());
        // String statusFlowUid = statusFlow.getUid();
        // modelMapper.map(draftStatusFlow, statusFlow);
        // statusFlow.setUid(statusFlowUid);
        // entity.setStatusFlowSettings(statusFlow);
        // entity.setDraftStatusFlowSettings(draftStatusFlow);

        // // Priority
        // TicketPrioritySettingsEntity priority = new TicketPrioritySettingsEntity();
        // priority.setUid(uidUtils.getUid());
        // TicketPrioritySettingsEntity draftPriority = TicketPrioritySettingsEntity
        //         .fromRequest(request.getDraftPrioritySettings());
        // draftPriority.setUid(uidUtils.getUid());
        // String priorityUid = priority.getUid();
        // modelMapper.map(draftPriority, priority);
        // priority.setUid(priorityUid);
        // entity.setPrioritySettings(priority);
        // entity.setDraftPrioritySettings(draftPriority);

        // // Assignment
        // TicketAssignmentSettingsEntity assignment = new TicketAssignmentSettingsEntity();
        // assignment.setUid(uidUtils.getUid());
        // TicketAssignmentSettingsEntity draftAssignment = TicketAssignmentSettingsEntity
        //         .fromRequest(request.getDraftAssignmentSettings());
        // draftAssignment.setUid(uidUtils.getUid());
        // String assignmentUid = assignment.getUid();
        // modelMapper.map(draftAssignment, assignment);
        // assignment.setUid(assignmentUid);
        // entity.setAssignmentSettings(assignment);
        // entity.setDraftAssignmentSettings(draftAssignment);

        // // Notification
        // TicketNotificationSettingsEntity notifySettings = new TicketNotificationSettingsEntity();
        // notifySettings.setUid(uidUtils.getUid());
        // TicketNotificationSettingsEntity draftNotify = TicketNotificationSettingsEntity
        //         .fromRequest(request.getDraftNotificationSettings());
        // draftNotify.setUid(uidUtils.getUid());
        // String notifyUid = notifySettings.getUid();
        // modelMapper.map(draftNotify, notifySettings);
        // notifySettings.setUid(notifyUid);
        // entity.setNotificationSettings(notifySettings);
        // entity.setDraftNotificationSettings(draftNotify);

        // // CustomField
        // TicketCustomFieldSettingsEntity customField = new TicketCustomFieldSettingsEntity();
        // customField.setUid(uidUtils.getUid());
        // TicketCustomFieldSettingsEntity draftCustomField = TicketCustomFieldSettingsEntity
        //         .fromRequest(request.getDraftCustomFieldSettings());
        // draftCustomField.setUid(uidUtils.getUid());
        // String customFieldUid = customField.getUid();
        // modelMapper.map(draftCustomField, customField);
        // customField.setUid(customFieldUid);
        // entity.setCustomFieldSettings(customField);
        // entity.setDraftCustomFieldSettings(draftCustomField);

        // 默认启用为空时置为 true
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        // 若请求设置为默认，保证同 org 仅有一个默认
        if (Boolean.TRUE.equals(entity.getIsDefault())) {
            ensureSingleDefault(entity.getOrgUid(), entity);
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
            TicketSettingsEntity entity = optional.get();
            // 更新基础字段（不直接覆盖子配置）
            modelMapper.map(request, entity);
            boolean draftUpdated = false;

            if (request.getCategorySettings() != null) {
                TicketCategorySettingsEntity category = entity.getCategorySettings();
                if (category == null) {
                    category = TicketCategorySettingsEntity
                            .fromRequest(request.getCategorySettings(), uidUtils::getUid);
                    category.setUid(uidUtils.getUid());
                    entity.setCategorySettings(category);
                } else {
                    category.replaceFromRequest(request.getCategorySettings(), uidUtils::getUid);
                }
            }

            if (request.getDraftCategorySettings() != null) {
                TicketCategorySettingsEntity draftCategory = entity.getDraftCategorySettings();
                if (draftCategory == null) {
                    draftCategory = TicketCategorySettingsEntity
                            .fromRequest(request.getDraftCategorySettings(), uidUtils::getUid);
                    draftCategory.setUid(uidUtils.getUid());
                    entity.setDraftCategorySettings(draftCategory);
                } else {
                    draftCategory.replaceFromRequest(request.getDraftCategorySettings(), uidUtils::getUid);
                }
                draftUpdated = true;
            }

            // // 仅更新草稿子配置：与 WorkgroupSettingsRestService.update 一致
            // if (request.getDraftBasicSettings() != null) {
            //     TicketBasicSettingsEntity draft = entity.getDraftBasicSettings();
            //     if (draft == null) {
            //         draft = TicketBasicSettingsEntity.fromRequest(request.getDraftBasicSettings(), modelMapper);
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftBasicSettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketBasicSettingsEntity tmp = TicketBasicSettingsEntity
            //                 .fromRequest(request.getBasicSettings(), modelMapper);
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }
            // if (request.getDraftStatusFlowSettings() != null) {
            //     TicketStatusFlowSettingsEntity draft = entity.getDraftStatusFlowSettings();
            //     if (draft == null) {
            //         draft = TicketStatusFlowSettingsEntity.fromRequest(request.getStatusFlowSettings());
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftStatusFlowSettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketStatusFlowSettingsEntity tmp = TicketStatusFlowSettingsEntity
            //                 .fromRequest(request.getStatusFlowSettings());
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }
            // if (request.getDraftPrioritySettings() != null) {
            //     TicketPrioritySettingsEntity draft = entity.getDraftPrioritySettings();
            //     if (draft == null) {
            //         draft = TicketPrioritySettingsEntity.fromRequest(request.getPrioritySettings());
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftPrioritySettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketPrioritySettingsEntity tmp = TicketPrioritySettingsEntity
            //                 .fromRequest(request.getPrioritySettings());
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }
            // if (request.getDraftAssignmentSettings() != null) {
            //     TicketAssignmentSettingsEntity draft = entity.getDraftAssignmentSettings();
            //     if (draft == null) {
            //         draft = TicketAssignmentSettingsEntity.fromRequest(request.getDraftAssignmentSettings());
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftAssignmentSettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketAssignmentSettingsEntity tmp = TicketAssignmentSettingsEntity
            //                 .fromRequest(request.getAssignmentSettings());
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }
            // if (request.getDraftNotificationSettings() != null) {
            //     TicketNotificationSettingsEntity draft = entity.getDraftNotificationSettings();
            //     if (draft == null) {
            //         draft = TicketNotificationSettingsEntity.fromRequest(request.getDraftNotificationSettings());
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftNotificationSettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketNotificationSettingsEntity tmp = TicketNotificationSettingsEntity
            //                 .fromRequest(request.getDraftNotificationSettings());
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }
            // if (request.getDraftCustomFieldSettings() != null) {
            //     TicketCustomFieldSettingsEntity draft = entity.getDraftCustomFieldSettings();
            //     if (draft == null) {
            //         draft = TicketCustomFieldSettingsEntity.fromRequest(request.getDraftCustomFieldSettings());
            //         draft.setUid(uidUtils.getUid());
            //         entity.setDraftCustomFieldSettings(draft);
            //     } else {
            //         String originalUid = draft.getUid();
            //         TicketCustomFieldSettingsEntity tmp = TicketCustomFieldSettingsEntity
            //                 .fromRequest(request.getDraftCustomFieldSettings());
            //         modelMapper.map(tmp, draft);
            //         draft.setUid(originalUid);
            //     }
            //     draftUpdated = true;
            // }

            // 维护草稿未发布标记
            if (draftUpdated) {
                entity.setHasUnpublishedChanges(true);
            }

            // 处理 isDefault / enabled
            if (request.getIsDefault() != null) {
                if (Boolean.TRUE.equals(request.getIsDefault())) {
                    ensureSingleDefault(entity.getOrgUid(), entity);
                } else {
                    entity.setIsDefault(false);
                }
            }
            if (request.getEnabled() != null) {
                entity.setEnabled(request.getEnabled());
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
        // 1) 已绑定则直接返回
        Optional<TicketSettingsBindingEntity> bindingOpt = bindingRepository
                .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        if (bindingOpt.isPresent()) {
            Optional<TicketSettingsEntity> settingsOpt = findByUid(bindingOpt.get().getTicketSettingsUid());
            if (settingsOpt.isPresent()) {
                return convertToResponse(settingsOpt.get());
            }
        }
        // 2) 获取或创建默认，并建立绑定
        TicketSettingsEntity def = getOrCreateDefault(orgUid);
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

    /** 获取或创建组织默认 TicketSettings（发布+草稿齐全，保证并发唯一） */
    @Transactional
    public TicketSettingsEntity getOrCreateDefault(String orgUid) {
        Optional<TicketSettingsEntity> existing = ticketSettingsRepository.findDefaultForUpdate(orgUid);
        if (existing.isPresent())
            return existing.get();

        // 按 WorkgroupSettingsRestService 模式创建：发布 + 草稿各自独立初始化并分配唯一 UID
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid(uidUtils.getUid())
                .orgUid(orgUid)
                .name("默认工单配置")
                .description("系统默认工单配置")
                .isDefault(true)
                .enabled(true)
                .build();

        TicketCategorySettingsEntity category = TicketCategorySettingsEntity.fromRequest(null, uidUtils::getUid);
        category.setUid(uidUtils.getUid());
        settings.setCategorySettings(category);

        TicketCategorySettingsEntity draftCategory = TicketCategorySettingsEntity.fromRequest(null, uidUtils::getUid);
        draftCategory.setUid(uidUtils.getUid());
        settings.setDraftCategorySettings(draftCategory);

        // // 依照 WorkgroupSettingsRestService 排列方式：每种子配置“发布 + 草稿”成对紧邻，便于阅读与维护
        // // Basic (published + draft)
        // TicketBasicSettingsEntity basic = TicketBasicSettingsEntity.fromRequest(null, modelMapper);
        // basic.setUid(uidUtils.getUid());
        // settings.setBasicSettings(basic);
        // TicketBasicSettingsEntity draftBasic = TicketBasicSettingsEntity.fromRequest(null, modelMapper);
        // draftBasic.setUid(uidUtils.getUid());
        // settings.setDraftBasicSettings(draftBasic);

        // // StatusFlow (published + draft)
        // TicketStatusFlowSettingsEntity flow = TicketStatusFlowSettingsEntity.fromRequest(null);
        // flow.setUid(uidUtils.getUid());
        // settings.setStatusFlowSettings(flow);
        // TicketStatusFlowSettingsEntity draftFlow = TicketStatusFlowSettingsEntity.fromRequest(null);
        // draftFlow.setUid(uidUtils.getUid());
        // settings.setDraftStatusFlowSettings(draftFlow);

        // // Priority (published + draft)
        // TicketPrioritySettingsEntity priority = TicketPrioritySettingsEntity.fromRequest(null);
        // priority.setUid(uidUtils.getUid());
        // settings.setPrioritySettings(priority);
        // TicketPrioritySettingsEntity draftPriority = TicketPrioritySettingsEntity.fromRequest(null);
        // draftPriority.setUid(uidUtils.getUid());
        // settings.setDraftPrioritySettings(draftPriority);

        // // Assignment (published + draft)
        // TicketAssignmentSettingsEntity assignment = TicketAssignmentSettingsEntity.fromRequest(null);
        // assignment.setUid(uidUtils.getUid());
        // settings.setAssignmentSettings(assignment);
        // TicketAssignmentSettingsEntity draftAssignment = TicketAssignmentSettingsEntity.fromRequest(null);
        // draftAssignment.setUid(uidUtils.getUid());
        // settings.setDraftAssignmentSettings(draftAssignment);

        // // Notification (published + draft)
        // TicketNotificationSettingsEntity notification = TicketNotificationSettingsEntity.fromRequest(null);
        // notification.setUid(uidUtils.getUid());
        // settings.setNotificationSettings(notification);
        // TicketNotificationSettingsEntity draftNotification = TicketNotificationSettingsEntity.fromRequest(null);
        // draftNotification.setUid(uidUtils.getUid());
        // settings.setDraftNotificationSettings(draftNotification);

        // // CustomField (published + draft)
        // TicketCustomFieldSettingsEntity customField = TicketCustomFieldSettingsEntity.fromRequest(null);
        // customField.setUid(uidUtils.getUid());
        // settings.setCustomFieldSettings(customField);
        // TicketCustomFieldSettingsEntity draftCustomField = TicketCustomFieldSettingsEntity.fromRequest(null);
        // draftCustomField.setUid(uidUtils.getUid());
        // settings.setDraftCustomFieldSettings(draftCustomField);

        // 确保同 org 仅有一个默认（虽然已锁定查询，此调用保持一致性）
        ensureSingleDefault(orgUid, settings);

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
     * 按 orgUid+workgroupUid 保存草稿（专用新 DTO）。若尚未绑定则自动创建默认 settings 绑定后再更新草稿。
     */
    @Transactional
    public TicketSettingsResponse saveByWorkgroup(String orgUid, String workgroupUid,
            com.bytedesk.ticket.ticket_settings.dto.TicketSettingsByWorkgroupUpdateRequest request) {
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
            entity = getOrCreateDefault(orgUid); // 默认 settings 已包含发布+草稿
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
        // 更新基础可编辑字段（name/description）
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        boolean draftUpdated = false;
        if (request.getDraftCategorySettings() != null) {
            TicketCategorySettingsEntity draftCategory = entity.getDraftCategorySettings();
            if (draftCategory == null) {
                draftCategory = TicketCategorySettingsEntity
                        .fromRequest(request.getDraftCategorySettings(), uidUtils::getUid);
                draftCategory.setUid(uidUtils.getUid());
                entity.setDraftCategorySettings(draftCategory);
            } else {
                draftCategory.replaceFromRequest(request.getDraftCategorySettings(), uidUtils::getUid);
            }
            draftUpdated = true;
        }
        // if (request.getDraftBasicSettings() != null) {
        //     TicketBasicSettingsEntity draft = entity.getDraftBasicSettings();
        //     if (draft == null) {
        //         draft = TicketBasicSettingsEntity.fromRequest(request.getDraftBasicSettings(), modelMapper);
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftBasicSettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketBasicSettingsEntity tmp = TicketBasicSettingsEntity
        //                 .fromRequest(request.getDraftBasicSettings(), modelMapper);
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
        // if (request.getDraftStatusFlowSettings() != null) {
        //     TicketStatusFlowSettingsEntity draft = entity.getDraftStatusFlowSettings();
        //     if (draft == null) {
        //         draft = TicketStatusFlowSettingsEntity.fromRequest(request.getDraftStatusFlowSettings());
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftStatusFlowSettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketStatusFlowSettingsEntity tmp = TicketStatusFlowSettingsEntity
        //                 .fromRequest(request.getDraftStatusFlowSettings());
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
        // if (request.getDraftPrioritySettings() != null) {
        //     TicketPrioritySettingsEntity draft = entity.getDraftPrioritySettings();
        //     if (draft == null) {
        //         draft = TicketPrioritySettingsEntity.fromRequest(request.getDraftPrioritySettings());
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftPrioritySettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketPrioritySettingsEntity tmp = TicketPrioritySettingsEntity
        //                 .fromRequest(request.getDraftPrioritySettings());
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
        // if (request.getDraftAssignmentSettings() != null) {
        //     TicketAssignmentSettingsEntity draft = entity.getDraftAssignmentSettings();
        //     if (draft == null) {
        //         draft = TicketAssignmentSettingsEntity.fromRequest(request.getDraftAssignmentSettings());
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftAssignmentSettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketAssignmentSettingsEntity tmp = TicketAssignmentSettingsEntity
        //                 .fromRequest(request.getDraftAssignmentSettings());
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
        // if (request.getDraftNotificationSettings() != null) {
        //     TicketNotificationSettingsEntity draft = entity.getDraftNotificationSettings();
        //     if (draft == null) {
        //         draft = TicketNotificationSettingsEntity.fromRequest(request.getDraftNotificationSettings());
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftNotificationSettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketNotificationSettingsEntity tmp = TicketNotificationSettingsEntity
        //                 .fromRequest(request.getDraftNotificationSettings());
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
        // if (request.getDraftCustomFieldSettings() != null) {
        //     TicketCustomFieldSettingsEntity draft = entity.getDraftCustomFieldSettings();
        //     if (draft == null) {
        //         draft = TicketCustomFieldSettingsEntity.fromRequest(request.getDraftCustomFieldSettings());
        //         draft.setUid(uidUtils.getUid());
        //         entity.setDraftCustomFieldSettings(draft);
        //     } else {
        //         String originalUid = draft.getUid();
        //         TicketCustomFieldSettingsEntity tmp = TicketCustomFieldSettingsEntity
        //                 .fromRequest(request.getDraftCustomFieldSettings());
        //         modelMapper.map(tmp, draft);
        //         draft.setUid(originalUid);
        //     }
        //     draftUpdated = true;
        // }
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
     * 保存/更新指定工作组的设置（按 orgUid+workgroupUid 幂等）。
     */
    // @Transactional
    // public TicketSettingsResponse saveByWorkgroup(TicketSettingsRequest request)
    // {
    // // 1) 先根据绑定表查出是否已有对应 settings
    // Optional<TicketSettingsBindingEntity>
    // bindingOpt = bindingRepository
    // .findByOrgUidAndWorkgroupUidAndDeletedFalse(request.getOrgUid(),
    // request.getWorkgroupUid());

    // TicketSettingsEntity entity = null;
    // if (bindingOpt.isPresent()) {
    // // 已绑定：加载对应 settings
    // Optional<TicketSettingsEntity> optional =
    // findByUid(bindingOpt.get().getTicketSettingsUid());
    // if (optional.isPresent()) {
    // entity = optional.get();
    // }
    // }
    // if (entity == null) {
    // // 未绑定：创建新 settings，并建立绑定
    // entity = modelMapper.map(request, TicketSettingsEntity.class);
    // if (!StringUtils.hasText(entity.getUid())) {
    // entity.setUid(uidUtils.getUid());
    // }
    // }

    // // 更新草稿子配置：仅当请求包含对应部分时才更新
    // if (request.getDraftBasicSettings() != null) {
    // if (entity.getDraftBasicSettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketBasicSettingsEntity draft =
    // TicketBasicSettingsEntity.fromRequest(request.getDraftBasicSettings(),
    // modelMapper);
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftBasicSettings(draft);
    // } else {
    // String originalUid = entity.getDraftBasicSettings().getUid();
    // TicketBasicSettingsEntity tmp =
    // TicketBasicSettingsEntity.fromRequest(request.getDraftBasicSettings(),
    // modelMapper);
    // modelMapper.map(tmp, entity.getDraftBasicSettings());
    // entity.getDraftBasicSettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }
    // if (request.getDraftStatusFlowSettings() != null) {
    // if (entity.getDraftStatusFlowSettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketStatusFlowSettingsEntity draft
    // =
    // TicketStatusFlowSettingsEntity.fromRequest(request.getDraftStatusFlowSettings());
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftStatusFlowSettings(draft);
    // } else {
    // String originalUid = entity.getDraftStatusFlowSettings().getUid();
    // TicketStatusFlowSettingsEntity tmp =
    // TicketStatusFlowSettingsEntity.fromRequest(request.getDraftStatusFlowSettings());
    // modelMapper.map(tmp, entity.getDraftStatusFlowSettings());
    // entity.getDraftStatusFlowSettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }
    // if (request.getDraftPrioritySettings() != null) {
    // if (entity.getDraftPrioritySettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketPrioritySettingsEntity draft =
    // TicketPrioritySettingsEntity.fromRequest(request.getDraftPrioritySettings());
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftPrioritySettings(draft);
    // } else {
    // String originalUid = entity.getDraftPrioritySettings().getUid();
    // TicketPrioritySettingsEntity tmp =
    // TicketPrioritySettingsEntity.fromRequest(request.getDraftPrioritySettings());
    // modelMapper.map(tmp, entity.getDraftPrioritySettings());
    // entity.getDraftPrioritySettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }
    // if (request.getDraftAssignmentSettings() != null) {
    // if (entity.getDraftAssignmentSettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketAssignmentSettingsEntity draft
    // =
    // TicketAssignmentSettingsEntity.fromRequest(request.getDraftAssignmentSettings());
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftAssignmentSettings(draft);
    // } else {
    // String originalUid = entity.getDraftAssignmentSettings().getUid();
    // TicketAssignmentSettingsEntity tmp =
    // TicketAssignmentSettingsEntity.fromRequest(request.getDraftAssignmentSettings());
    // modelMapper.map(tmp, entity.getDraftAssignmentSettings());
    // entity.getDraftAssignmentSettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }
    // if (request.getDraftNotificationSettings() != null) {
    // if (entity.getDraftNotificationSettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketNotificationSettingsEntity
    // draft =
    // TicketNotificationSettingsEntity.fromRequest(request.getDraftNotificationSettings());
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftNotificationSettings(draft);
    // } else {
    // String originalUid = entity.getDraftNotificationSettings().getUid();
    // TicketNotificationSettingsEntity tmp =
    // TicketNotificationSettingsEntity.fromRequest(request.getDraftNotificationSettings());
    // modelMapper.map(tmp, entity.getDraftNotificationSettings());
    // entity.getDraftNotificationSettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }
    // if (request.getDraftCustomFieldSettings() != null) {
    // if (entity.getDraftCustomFieldSettings() == null) {
    // com.bytedesk.ticket.ticket_settings.sub.TicketCustomFieldSettingsEntity draft
    // =
    // TicketCustomFieldSettingsEntity.fromRequest(request.getDraftCustomFieldSettings());
    // draft.setUid(uidUtils.getUid());
    // entity.setDraftCustomFieldSettings(draft);
    // } else {
    // String originalUid = entity.getDraftCustomFieldSettings().getUid();
    // TicketCustomFieldSettingsEntity tmp =
    // TicketCustomFieldSettingsEntity.fromRequest(request.getDraftCustomFieldSettings());
    // modelMapper.map(tmp, entity.getDraftCustomFieldSettings());
    // entity.getDraftCustomFieldSettings().setUid(originalUid);
    // }
    // // 不再维护 hasUnpublishedChanges 标记
    // }

    // // 不再使用 initialized/lastModifiedUserUid 字段
    // TicketSettingsEntity saved = save(entity);

    // // 确保绑定关系存在且指向最新的 settings
    // TicketSettingsBindingEntity
    // binding = bindingOpt
    // .orElseGet(() ->
    // TicketSettingsBindingEntity.builder()
    // .uid(uidUtils.getUid())
    // .orgUid(request.getOrgUid())
    // .workgroupUid(request.getWorkgroupUid())
    // .ticketSettingsUid(saved.getUid())
    // .build());
    // binding.setTicketSettingsUid(saved.getUid());
    // bindingRepository.save(binding);
    // return convertToResponse(saved);
    // }

    /**
     * 发布草稿配置到正式配置，参考 WorkgroupSettings 的 publish 逻辑。
     * 目前 TicketSettings
     * 已改为拆分多个子配置（basic/statusFlow/priority/assignment/notification/customField）发布 +
     * 草稿。
     * 若正式为空而草稿存在，则克隆草稿；若正式存在则仅复制业务字段（忽略 id/uid/version/时间）。
     */
    @Transactional
    public TicketSettingsResponse publish(String uid) {
        Optional<TicketSettingsEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("TicketSettings not found: " + uid);
        }
        TicketSettingsEntity entity = optional.get();

        // ===== 基础设置 =====
        // if (entity.getDraftBasicSettings() != null) {
        //     if (entity.getBasicSettings() == null) {
        //         entity.setBasicSettings(cloneSettings(entity.getDraftBasicSettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftBasicSettings(), entity.getBasicSettings());
        //     }
        // }
        // // ===== 流转设置 =====
        // if (entity.getDraftStatusFlowSettings() != null) {
        //     if (entity.getStatusFlowSettings() == null) {
        //         entity.setStatusFlowSettings(cloneSettings(entity.getDraftStatusFlowSettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftStatusFlowSettings(), entity.getStatusFlowSettings());
        //     }
        // }
        // // ===== 优先级设置 =====
        // if (entity.getDraftPrioritySettings() != null) {
        //     if (entity.getPrioritySettings() == null) {
        //         entity.setPrioritySettings(cloneSettings(entity.getDraftPrioritySettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftPrioritySettings(), entity.getPrioritySettings());
        //     }
        // }
        // // ===== 分配设置 =====
        // if (entity.getDraftAssignmentSettings() != null) {
        //     if (entity.getAssignmentSettings() == null) {
        //         entity.setAssignmentSettings(cloneSettings(entity.getDraftAssignmentSettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftAssignmentSettings(), entity.getAssignmentSettings());
        //     }
        // }
        // // ===== 通知设置 =====
        // if (entity.getDraftNotificationSettings() != null) {
        //     if (entity.getNotificationSettings() == null) {
        //         entity.setNotificationSettings(cloneSettings(entity.getDraftNotificationSettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftNotificationSettings(), entity.getNotificationSettings());
        //     }
        // }
        // // ===== 自定义字段设置 =====
        // if (entity.getDraftCustomFieldSettings() != null) {
        //     if (entity.getCustomFieldSettings() == null) {
        //         entity.setCustomFieldSettings(cloneSettings(entity.getDraftCustomFieldSettings()));
        //     } else {
        //         copyBusinessFields(entity.getDraftCustomFieldSettings(), entity.getCustomFieldSettings());
        //     }
        // }

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

        // 发布时间与草稿标记维护
        entity.setPublishedAt(ZonedDateTime.now());
        entity.setHasUnpublishedChanges(false);
        TicketSettingsEntity saved = save(entity);
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

    /**
     * 克隆草稿配置为新的发布配置，分配新的 uid，清空 id/version。
     */
    // @SuppressWarnings("unchecked")
    // private <T> T cloneSettings(T source) {
    //     if (source == null) {
    //         return null;
    //     }
    //     T target = (T) modelMapper.map(source, source.getClass());
    //     try {
    //         // 通过反射设置基础标识字段（BaseEntity: setUid, setId, setVersion）
    //         source.getClass().getMethod("setUid", String.class).invoke(target, uidUtils.getUid());
    //         // id 设为 null
    //         try {
    //             source.getClass().getMethod("setId", Long.class).invoke(target, (Long) null);
    //         } catch (NoSuchMethodException ignore) {
    //         }
    //         try {
    //             source.getClass().getMethod("setVersion", Long.class).invoke(target, 0L);
    //         } catch (NoSuchMethodException ignore) {
    //         }
    //     } catch (Exception e) {
    //         log.warn("cloneSettings reflection failed: {}", e.getMessage());
    //     }
    //     return target;
    // }

    /**
     * 复制业务字段：使用 modelMapper 覆盖（保留目标 uid/id/version）。
     */
    // private void copyBusinessFields(Object draft, Object published) {
    //     if (draft == null || published == null) {
    //         return;
    //     }
    //     String uid = null;
    //     Long id = null;
    //     Long version = null;
    //     try {
    //         uid = (String) published.getClass().getMethod("getUid").invoke(published);
    //     } catch (Exception ignore) {
    //     }
    //     try {
    //         id = (Long) published.getClass().getMethod("getId").invoke(published);
    //     } catch (Exception ignore) {
    //     }
    //     try {
    //         version = (Long) published.getClass().getMethod("getVersion").invoke(published);
    //     } catch (Exception ignore) {
    //     }
    //     modelMapper.map(draft, published);
    //     try {
    //         if (uid != null)
    //             published.getClass().getMethod("setUid", String.class).invoke(published, uid);
    //     } catch (Exception ignore) {
    //     }
    //     try {
    //         if (id != null)
    //             published.getClass().getMethod("setId", Long.class).invoke(published, id);
    //     } catch (Exception ignore) {
    //     }
    //     try {
    //         if (version != null)
    //             published.getClass().getMethod("setVersion", Long.class).invoke(published, version);
    //     } catch (Exception ignore) {
    //     }
    // }

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
        return request.getDraftCategorySettings() != null
                ? request.getDraftCategorySettings()
                : request.getCategorySettings();
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

    @Override
    public TicketSettingsResponse convertToResponse(TicketSettingsEntity entity) {
        TicketSettingsResponse resp = modelMapper.map(entity, TicketSettingsResponse.class);
        // 发布版本映射
        // if (entity.getBasicSettings() != null) {
        //     resp.setBasicSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse.builder()
        //                     .numberPrefix(entity.getBasicSettings().getNumberPrefix())
        //                     .numberLength(entity.getBasicSettings().getNumberLength())
        //                     .defaultPriority(entity.getBasicSettings().getDefaultPriority())
        //                     .validityDays(entity.getBasicSettings().getValidityDays())
        //                     .autoCloseHours(entity.getBasicSettings().getAutoCloseHours())
        //                     .enableAutoClose(entity.getBasicSettings().getEnableAutoClose())
        //                     .build());
        // }
        // if (entity.getStatusFlowSettings() != null) {
        //     resp.setStatusFlowSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse.builder()
        //                     .content(entity.getStatusFlowSettings().getContent())
        //                     .build());
        // }
        // if (entity.getPrioritySettings() != null) {
        //     resp.setPrioritySettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse.builder()
        //                     .content(entity.getPrioritySettings().getContent())
        //                     .build());
        // }
        // if (entity.getAssignmentSettings() != null) {
        //     resp.setAssignmentSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse.builder()
        //                     .autoAssign(entity.getAssignmentSettings().getAutoAssign())
        //                     .assignmentType(entity.getAssignmentSettings().getAssignmentType())
        //                     .workingHoursEnabled(entity.getAssignmentSettings().getWorkingHoursEnabled())
        //                     .workingHoursStart(entity.getAssignmentSettings().getWorkingHoursStart())
        //                     .workingHoursEnd(entity.getAssignmentSettings().getWorkingHoursEnd())
        //                     .workingDays(entity.getAssignmentSettings().getWorkingDays())
        //                     .maxConcurrentTickets(entity.getAssignmentSettings().getMaxConcurrentTickets())
        //                     .build());
        // }
        // if (entity.getNotificationSettings() != null) {
        //     resp.setNotificationSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse.builder()
        //                     .emailEnabled(entity.getNotificationSettings().getEmailEnabled())
        //                     .emailEvents(entity.getNotificationSettings().getEmailEvents())
        //                     .emailTemplates(entity.getNotificationSettings().getEmailTemplates())
        //                     .internalEnabled(entity.getNotificationSettings().getInternalEnabled())
        //                     .internalEvents(entity.getNotificationSettings().getInternalEvents())
        //                     .webhookEnabled(entity.getNotificationSettings().getWebhookEnabled())
        //                     .webhookUrl(entity.getNotificationSettings().getWebhookUrl())
        //                     .webhookEvents(entity.getNotificationSettings().getWebhookEvents())
        //                     .build());
        // }
        // if (entity.getCustomFieldSettings() != null) {
        //     resp.setCustomFieldSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse.builder()
        //                     .content(entity.getCustomFieldSettings().getContent())
        //                     .build());
        // }
        // // 草稿版本映射
        // if (entity.getDraftBasicSettings() != null) {
        //     resp.setDraftBasicSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse.builder()
        //                     .numberPrefix(entity.getDraftBasicSettings().getNumberPrefix())
        //                     .numberLength(entity.getDraftBasicSettings().getNumberLength())
        //                     .defaultPriority(entity.getDraftBasicSettings().getDefaultPriority())
        //                     .validityDays(entity.getDraftBasicSettings().getValidityDays())
        //                     .autoCloseHours(entity.getDraftBasicSettings().getAutoCloseHours())
        //                     .enableAutoClose(entity.getDraftBasicSettings().getEnableAutoClose())
        //                     .build());
        // }
        // if (entity.getDraftStatusFlowSettings() != null) {
        //     resp.setDraftStatusFlowSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse.builder()
        //                     .content(entity.getDraftStatusFlowSettings().getContent())
        //                     .build());
        // }
        // if (entity.getDraftPrioritySettings() != null) {
        //     resp.setDraftPrioritySettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse.builder()
        //                     .content(entity.getDraftPrioritySettings().getContent())
        //                     .build());
        // }
        // if (entity.getDraftAssignmentSettings() != null) {
        //     resp.setDraftAssignmentSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse.builder()
        //                     .autoAssign(entity.getDraftAssignmentSettings().getAutoAssign())
        //                     .assignmentType(entity.getDraftAssignmentSettings().getAssignmentType())
        //                     .workingHoursEnabled(entity.getDraftAssignmentSettings().getWorkingHoursEnabled())
        //                     .workingHoursStart(entity.getDraftAssignmentSettings().getWorkingHoursStart())
        //                     .workingHoursEnd(entity.getDraftAssignmentSettings().getWorkingHoursEnd())
        //                     .workingDays(entity.getDraftAssignmentSettings().getWorkingDays())
        //                     .maxConcurrentTickets(entity.getDraftAssignmentSettings().getMaxConcurrentTickets())
        //                     .build());
        // }
        // if (entity.getDraftNotificationSettings() != null) {
        //     resp.setDraftNotificationSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse.builder()
        //                     .emailEnabled(entity.getDraftNotificationSettings().getEmailEnabled())
        //                     .emailEvents(entity.getDraftNotificationSettings().getEmailEvents())
        //                     .emailTemplates(entity.getDraftNotificationSettings().getEmailTemplates())
        //                     .internalEnabled(entity.getDraftNotificationSettings().getInternalEnabled())
        //                     .internalEvents(entity.getDraftNotificationSettings().getInternalEvents())
        //                     .webhookEnabled(entity.getDraftNotificationSettings().getWebhookEnabled())
        //                     .webhookUrl(entity.getDraftNotificationSettings().getWebhookUrl())
        //                     .webhookEvents(entity.getDraftNotificationSettings().getWebhookEvents())
        //                     .build());
        // }
        // if (entity.getDraftCustomFieldSettings() != null) {
        //     resp.setDraftCustomFieldSettings(
        //             com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse.builder()
        //                     .content(entity.getDraftCustomFieldSettings().getContent())
        //                     .build());
        // }
        resp.setCategorySettings(mapCategorySettings(entity.getCategorySettings()));
        resp.setDraftCategorySettings(mapCategorySettings(entity.getDraftCategorySettings()));
        return resp;
    }

    @Override
    public TicketSettingsExcel convertToExcel(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsExcel.class);
    }

    /**
     * 保证同一个 orgUid 下仅有一个 isDefault=true（参考 WorkgroupSettingsRestService）
     */
    private void ensureSingleDefault(String orgUid, TicketSettingsEntity target) {
        if (!StringUtils.hasText(orgUid) || target == null) {
            return;
        }
        Optional<TicketSettingsEntity> currentOpt = ticketSettingsRepository.findDefaultForUpdate(orgUid);
        if (currentOpt.isPresent()) {
            TicketSettingsEntity current = currentOpt.get();
            if (!current.getUid().equals(target.getUid())) {
                current.setIsDefault(false);
                ticketSettingsRepository.save(current);
            }
        }
        target.setIsDefault(true);
    }

}
