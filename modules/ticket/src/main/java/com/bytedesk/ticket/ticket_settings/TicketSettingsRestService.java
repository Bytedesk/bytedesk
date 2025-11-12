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
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.ticket.ticket_settings.sub.TicketAssignmentSettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.TicketBasicSettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.TicketCustomFieldSettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.TicketNotificationSettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.TicketPrioritySettingsEntity;
import com.bytedesk.ticket.ticket_settings.sub.TicketStatusFlowSettingsEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketSettingsRestService extends BaseRestServiceWithExport<TicketSettingsEntity, TicketSettingsRequest, TicketSettingsResponse, TicketSettingsExcel> {

    private final TicketSettingsRepository ticketSettingsRepository;

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

    @Cacheable(value = "ticketSettings", key = "#uid", unless="#result==null")
    @Override
    public Optional<TicketSettingsEntity> findByUid(String uid) {
        return ticketSettingsRepository.findByUid(uid);
    }

    @Cacheable(value = "ticketSettings", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
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
        TicketBasicSettingsEntity basic = new TicketBasicSettingsEntity();
        basic.setUid(uidUtils.getUid());
        TicketBasicSettingsEntity draftBasic = new TicketBasicSettingsEntity();
        draftBasic.setUid(uidUtils.getUid());
        if (request.getDraftBasicSettings() != null) {
            modelMapper.map(request.getDraftBasicSettings(), draftBasic);
        }
        // 初始创建时，发布版本以草稿为基准；保留发布 UID
        String basicUid = basic.getUid();
        modelMapper.map(draftBasic, basic);
        basic.setUid(basicUid);
        entity.setBasicSettings(basic);
        entity.setDraftBasicSettings(draftBasic);

        // StatusFlow
        TicketStatusFlowSettingsEntity statusFlow = new TicketStatusFlowSettingsEntity();
        statusFlow.setUid(uidUtils.getUid());
        TicketStatusFlowSettingsEntity draftStatusFlow = new TicketStatusFlowSettingsEntity();
        draftStatusFlow.setUid(uidUtils.getUid());
        if (request.getDraftStatusFlowSettings() != null) {
            modelMapper.map(request.getDraftStatusFlowSettings(), draftStatusFlow);
        }
        String statusFlowUid = statusFlow.getUid();
        modelMapper.map(draftStatusFlow, statusFlow);
        statusFlow.setUid(statusFlowUid);
        entity.setStatusFlowSettings(statusFlow);
        entity.setDraftStatusFlowSettings(draftStatusFlow);

        // Priority
        TicketPrioritySettingsEntity priority = new TicketPrioritySettingsEntity();
        priority.setUid(uidUtils.getUid());
        TicketPrioritySettingsEntity draftPriority = new TicketPrioritySettingsEntity();
        draftPriority.setUid(uidUtils.getUid());
        if (request.getDraftPrioritySettings() != null) {
            modelMapper.map(request.getDraftPrioritySettings(), draftPriority);
        }
        String priorityUid = priority.getUid();
        modelMapper.map(draftPriority, priority);
        priority.setUid(priorityUid);
        entity.setPrioritySettings(priority);
        entity.setDraftPrioritySettings(draftPriority);

        // Assignment
        TicketAssignmentSettingsEntity assignment = new TicketAssignmentSettingsEntity();
        assignment.setUid(uidUtils.getUid());
        TicketAssignmentSettingsEntity draftAssignment = new TicketAssignmentSettingsEntity();
        draftAssignment.setUid(uidUtils.getUid());
        if (request.getDraftAssignmentSettings() != null) {
            modelMapper.map(request.getDraftAssignmentSettings(), draftAssignment);
        }
        String assignmentUid = assignment.getUid();
        modelMapper.map(draftAssignment, assignment);
        assignment.setUid(assignmentUid);
        entity.setAssignmentSettings(assignment);
        entity.setDraftAssignmentSettings(draftAssignment);

        // Notification
        TicketNotificationSettingsEntity notifySettings = new TicketNotificationSettingsEntity();
        notifySettings.setUid(uidUtils.getUid());
        TicketNotificationSettingsEntity draftNotify = new TicketNotificationSettingsEntity();
        draftNotify.setUid(uidUtils.getUid());
        if (request.getDraftNotificationSettings() != null) {
            modelMapper.map(request.getDraftNotificationSettings(), draftNotify);
        }
        String notifyUid = notifySettings.getUid();
        modelMapper.map(draftNotify, notifySettings);
        notifySettings.setUid(notifyUid);
        entity.setNotificationSettings(notifySettings);
        entity.setDraftNotificationSettings(draftNotify);

        // CustomField
        TicketCustomFieldSettingsEntity customField = new TicketCustomFieldSettingsEntity();
        customField.setUid(uidUtils.getUid());
        TicketCustomFieldSettingsEntity draftCustomField = new TicketCustomFieldSettingsEntity();
        draftCustomField.setUid(uidUtils.getUid());
        if (request.getDraftCustomFieldSettings() != null) {
            modelMapper.map(request.getDraftCustomFieldSettings(), draftCustomField);
        }
        String customFieldUid = customField.getUid();
        modelMapper.map(draftCustomField, customField);
        customField.setUid(customFieldUid);
        entity.setCustomFieldSettings(customField);
        entity.setDraftCustomFieldSettings(draftCustomField);

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

            // 仅更新草稿子配置：与 WorkgroupSettingsRestService.update 一致
            if (request.getDraftBasicSettings() != null) {
                TicketBasicSettingsEntity draft = entity.getDraftBasicSettings();
                if (draft == null) {
                    draft = new TicketBasicSettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftBasicSettings(), draft);
                    entity.setDraftBasicSettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftBasicSettings(), draft);
                    draft.setUid(originalUid);
                }
            }
            if (request.getDraftStatusFlowSettings() != null) {
                TicketStatusFlowSettingsEntity draft = entity.getDraftStatusFlowSettings();
                if (draft == null) {
                    draft = new TicketStatusFlowSettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftStatusFlowSettings(), draft);
                    entity.setDraftStatusFlowSettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftStatusFlowSettings(), draft);
                    draft.setUid(originalUid);
                }
            }
            if (request.getDraftPrioritySettings() != null) {
                TicketPrioritySettingsEntity draft = entity.getDraftPrioritySettings();
                if (draft == null) {
                    draft = new TicketPrioritySettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftPrioritySettings(), draft);
                    entity.setDraftPrioritySettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftPrioritySettings(), draft);
                    draft.setUid(originalUid);
                }
            }
            if (request.getDraftAssignmentSettings() != null) {
                TicketAssignmentSettingsEntity draft = entity.getDraftAssignmentSettings();
                if (draft == null) {
                    draft = new TicketAssignmentSettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftAssignmentSettings(), draft);
                    entity.setDraftAssignmentSettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftAssignmentSettings(), draft);
                    draft.setUid(originalUid);
                }
            }
            if (request.getDraftNotificationSettings() != null) {
                TicketNotificationSettingsEntity draft = entity.getDraftNotificationSettings();
                if (draft == null) {
                    draft = new TicketNotificationSettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftNotificationSettings(), draft);
                    entity.setDraftNotificationSettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftNotificationSettings(), draft);
                    draft.setUid(originalUid);
                }
            }
            if (request.getDraftCustomFieldSettings() != null) {
                TicketCustomFieldSettingsEntity draft = entity.getDraftCustomFieldSettings();
                if (draft == null) {
                    draft = new TicketCustomFieldSettingsEntity();
                    draft.setUid(uidUtils.getUid());
                    modelMapper.map(request.getDraftCustomFieldSettings(), draft);
                    entity.setDraftCustomFieldSettings(draft);
                } else {
                    String originalUid = draft.getUid();
                    modelMapper.map(request.getDraftCustomFieldSettings(), draft);
                    draft.setUid(originalUid);
                }
            }

            TicketSettingsEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update ticketSettings failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    /**
     * 根据 org + workgroup 获取设置，不存在时返回默认结构（不落库），供前端初始化。
     */
    public TicketSettingsResponse getOrDefaultByWorkgroup(String orgUid, String workgroupUid) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        if (optional.isPresent()) {
            return convertToResponse(optional.get());
        }
        // 返回结构化默认（不落库）
        TicketSettingsResponse resp = TicketSettingsResponse.builder()
            .orgUid(orgUid)
            .workgroupUid(workgroupUid)
            // 初始化标记已移除
            .build();
        // 默认草稿：Basic
        resp.setDraftBasicSettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse.builder()
                .numberPrefix("TK").numberLength(8)
                .defaultPriority("medium")
                .validityDays(30)
                .autoCloseHours(72)
                .enableAutoClose(true)
                .build()
        );
        // 默认草稿：StatusFlow / Priority / Assignment / Notification / CustomFields
        resp.setDraftStatusFlowSettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse.builder()
                .content(com.bytedesk.ticket.ticket_settings.sub.model.StatusFlowSettingsData.builder().build())
                .build()
        );
        resp.setDraftPrioritySettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse.builder()
                .content(com.bytedesk.ticket.ticket_settings.sub.model.PrioritySettingsData.builder().build())
                .build()
        );
        resp.setDraftAssignmentSettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse.builder()
                .autoAssign(true)
                .assignmentType("round_robin")
                .workingHoursEnabled(true)
                .workingHoursStart("09:00")
                .workingHoursEnd("18:00")
                .workingDays(java.util.List.of(1,2,3,4,5))
                .maxConcurrentTickets(10)
                .build()
        );
        resp.setDraftNotificationSettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse.builder()
                .emailEnabled(true)
                .emailEvents(java.util.List.of("created","assigned","resolved","closed"))
                .emailTemplates(java.util.List.of())
                .internalEnabled(true)
                .internalEvents(java.util.List.of("created","assigned","resolved","closed"))
                .webhookEnabled(false)
                .webhookEvents(java.util.List.of())
                .build()
        );
        resp.setDraftCustomFieldSettings(
            com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse.builder()
                .content(com.bytedesk.ticket.ticket_settings.sub.model.CustomFieldSettingsData.builder().build())
                .build()
        );
        return resp;
    }

    /**
     * 保存/更新指定工作组的设置（按 orgUid+workgroupUid 幂等）。
     */
    @Transactional
    public TicketSettingsResponse saveByWorkgroup(TicketSettingsRequest request) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository.findByOrgUidAndWorkgroupUidAndDeletedFalse(request.getOrgUid(), request.getWorkgroupUid());
        TicketSettingsEntity entity = optional.orElseGet(() -> {
            TicketSettingsEntity e = modelMapper.map(request, TicketSettingsEntity.class);
            if (!StringUtils.hasText(e.getUid())) {
                e.setUid(uidUtils.getUid());
            }
            return e;
        });

        // 更新草稿子配置：仅当请求包含对应部分时才更新
        if (request.getDraftBasicSettings() != null) {
            if (entity.getDraftBasicSettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketBasicSettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketBasicSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftBasicSettings(), draft);
                entity.setDraftBasicSettings(draft);
            } else {
                String originalUid = entity.getDraftBasicSettings().getUid();
                modelMapper.map(request.getDraftBasicSettings(), entity.getDraftBasicSettings());
                entity.getDraftBasicSettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }
        if (request.getDraftStatusFlowSettings() != null) {
            if (entity.getDraftStatusFlowSettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketStatusFlowSettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketStatusFlowSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftStatusFlowSettings(), draft);
                entity.setDraftStatusFlowSettings(draft);
            } else {
                String originalUid = entity.getDraftStatusFlowSettings().getUid();
                modelMapper.map(request.getDraftStatusFlowSettings(), entity.getDraftStatusFlowSettings());
                entity.getDraftStatusFlowSettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }
        if (request.getDraftPrioritySettings() != null) {
            if (entity.getDraftPrioritySettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketPrioritySettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketPrioritySettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftPrioritySettings(), draft);
                entity.setDraftPrioritySettings(draft);
            } else {
                String originalUid = entity.getDraftPrioritySettings().getUid();
                modelMapper.map(request.getDraftPrioritySettings(), entity.getDraftPrioritySettings());
                entity.getDraftPrioritySettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }
        if (request.getDraftAssignmentSettings() != null) {
            if (entity.getDraftAssignmentSettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketAssignmentSettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketAssignmentSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftAssignmentSettings(), draft);
                entity.setDraftAssignmentSettings(draft);
            } else {
                String originalUid = entity.getDraftAssignmentSettings().getUid();
                modelMapper.map(request.getDraftAssignmentSettings(), entity.getDraftAssignmentSettings());
                entity.getDraftAssignmentSettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }
        if (request.getDraftNotificationSettings() != null) {
            if (entity.getDraftNotificationSettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketNotificationSettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketNotificationSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftNotificationSettings(), draft);
                entity.setDraftNotificationSettings(draft);
            } else {
                String originalUid = entity.getDraftNotificationSettings().getUid();
                modelMapper.map(request.getDraftNotificationSettings(), entity.getDraftNotificationSettings());
                entity.getDraftNotificationSettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }
        if (request.getDraftCustomFieldSettings() != null) {
            if (entity.getDraftCustomFieldSettings() == null) {
                com.bytedesk.ticket.ticket_settings.sub.TicketCustomFieldSettingsEntity draft = new com.bytedesk.ticket.ticket_settings.sub.TicketCustomFieldSettingsEntity();
                draft.setUid(uidUtils.getUid());
                modelMapper.map(request.getDraftCustomFieldSettings(), draft);
                entity.setDraftCustomFieldSettings(draft);
            } else {
                String originalUid = entity.getDraftCustomFieldSettings().getUid();
                modelMapper.map(request.getDraftCustomFieldSettings(), entity.getDraftCustomFieldSettings());
                entity.getDraftCustomFieldSettings().setUid(originalUid);
            }
            // 不再维护 hasUnpublishedChanges 标记
        }

    // 不再使用 initialized/lastModifiedUserUid 字段
        TicketSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    /**
     * 发布草稿配置到正式配置，参考 WorkgroupSettings 的 publish 逻辑。
     * 目前 TicketSettings 已改为拆分多个子配置（basic/statusFlow/priority/assignment/notification/customField）发布 + 草稿。
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
        if (entity.getDraftBasicSettings() != null) {
            if (entity.getBasicSettings() == null) {
                entity.setBasicSettings(cloneSettings(entity.getDraftBasicSettings()));
            } else {
                copyBusinessFields(entity.getDraftBasicSettings(), entity.getBasicSettings());
            }
        }
        // ===== 流转设置 =====
        if (entity.getDraftStatusFlowSettings() != null) {
            if (entity.getStatusFlowSettings() == null) {
                entity.setStatusFlowSettings(cloneSettings(entity.getDraftStatusFlowSettings()));
            } else {
                copyBusinessFields(entity.getDraftStatusFlowSettings(), entity.getStatusFlowSettings());
            }
        }
        // ===== 优先级设置 =====
        if (entity.getDraftPrioritySettings() != null) {
            if (entity.getPrioritySettings() == null) {
                entity.setPrioritySettings(cloneSettings(entity.getDraftPrioritySettings()));
            } else {
                copyBusinessFields(entity.getDraftPrioritySettings(), entity.getPrioritySettings());
            }
        }
        // ===== 分配设置 =====
        if (entity.getDraftAssignmentSettings() != null) {
            if (entity.getAssignmentSettings() == null) {
                entity.setAssignmentSettings(cloneSettings(entity.getDraftAssignmentSettings()));
            } else {
                copyBusinessFields(entity.getDraftAssignmentSettings(), entity.getAssignmentSettings());
            }
        }
        // ===== 通知设置 =====
        if (entity.getDraftNotificationSettings() != null) {
            if (entity.getNotificationSettings() == null) {
                entity.setNotificationSettings(cloneSettings(entity.getDraftNotificationSettings()));
            } else {
                copyBusinessFields(entity.getDraftNotificationSettings(), entity.getNotificationSettings());
            }
        }
        // ===== 自定义字段设置 =====
        if (entity.getDraftCustomFieldSettings() != null) {
            if (entity.getCustomFieldSettings() == null) {
                entity.setCustomFieldSettings(cloneSettings(entity.getDraftCustomFieldSettings()));
            } else {
                copyBusinessFields(entity.getDraftCustomFieldSettings(), entity.getCustomFieldSettings());
            }
        }

    // 发布后不再使用 initialized/hasUnpublishedChanges/publishedAt 字段
        TicketSettingsEntity saved = save(entity);
        return convertToResponse(saved);
    }

    /**
     * 按 orgUid + workgroupUid 发布对应 TicketSettings（便于前端直接在工作组维度触发）。
     */
    @Transactional
    public TicketSettingsResponse publishByWorkgroup(String orgUid, String workgroupUid) {
        Optional<TicketSettingsEntity> optional = ticketSettingsRepository
                .findByOrgUidAndWorkgroupUidAndDeletedFalse(orgUid, workgroupUid);
        if (!optional.isPresent()) {
            throw new RuntimeException("TicketSettings not found by orgUid+workgroupUid");
        }
        return publish(optional.get().getUid());
    }

    /**
     * 克隆草稿配置为新的发布配置，分配新的 uid，清空 id/version。
     */
    @SuppressWarnings("unchecked")
    private <T> T cloneSettings(T source) {
        if (source == null) {
            return null;
        }
        T target = (T) modelMapper.map(source, source.getClass());
        try {
            // 通过反射设置基础标识字段（BaseEntity: setUid, setId, setVersion）
            source.getClass().getMethod("setUid", String.class).invoke(target, uidUtils.getUid());
            // id 设为 null
            try { source.getClass().getMethod("setId", Long.class).invoke(target, (Long) null); } catch (NoSuchMethodException ignore) {}
            try { source.getClass().getMethod("setVersion", Long.class).invoke(target, 0L); } catch (NoSuchMethodException ignore) {}
        } catch (Exception e) {
            log.warn("cloneSettings reflection failed: {}", e.getMessage());
        }
        return target;
    }

    /**
     * 复制业务字段：使用 modelMapper 覆盖（保留目标 uid/id/version）。
     */
    private void copyBusinessFields(Object draft, Object published) {
        if (draft == null || published == null) {
            return;
        }
        String uid = null;
        Long id = null;
        Long version = null;
        try { uid = (String) published.getClass().getMethod("getUid").invoke(published); } catch (Exception ignore) {}
        try { id = (Long) published.getClass().getMethod("getId").invoke(published); } catch (Exception ignore) {}
        try { version = (Long) published.getClass().getMethod("getVersion").invoke(published); } catch (Exception ignore) {}
        modelMapper.map(draft, published);
        try { if (uid != null) published.getClass().getMethod("setUid", String.class).invoke(published, uid); } catch (Exception ignore) {}
        try { if (id != null) published.getClass().getMethod("setId", Long.class).invoke(published, id); } catch (Exception ignore) {}
        try { if (version != null) published.getClass().getMethod("setVersion", Long.class).invoke(published, version); } catch (Exception ignore) {}
    }

    @Override
    protected TicketSettingsEntity doSave(TicketSettingsEntity entity) {
        return ticketSettingsRepository.save(entity);
    }

    @Override
    public TicketSettingsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TicketSettingsEntity entity) {
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
        }
        else {
            throw new RuntimeException("TicketSettings not found");
        }
    }

    @Override
    public void delete(TicketSettingsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TicketSettingsResponse convertToResponse(TicketSettingsEntity entity) {
        TicketSettingsResponse resp = modelMapper.map(entity, TicketSettingsResponse.class);
        // 发布版本映射
        if (entity.getBasicSettings() != null) {
            resp.setBasicSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse.builder()
                    .numberPrefix(entity.getBasicSettings().getNumberPrefix())
                    .numberLength(entity.getBasicSettings().getNumberLength())
                    .defaultPriority(entity.getBasicSettings().getDefaultPriority())
                    .validityDays(entity.getBasicSettings().getValidityDays())
                    .autoCloseHours(entity.getBasicSettings().getAutoCloseHours())
                    .enableAutoClose(entity.getBasicSettings().getEnableAutoClose())
                    .build()
            );
        }
        if (entity.getStatusFlowSettings() != null) {
            resp.setStatusFlowSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse.builder()
                    .content(entity.getStatusFlowSettings().getContent())
                    .build()
            );
        }
        if (entity.getPrioritySettings() != null) {
            resp.setPrioritySettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse.builder()
                    .content(entity.getPrioritySettings().getContent())
                    .build()
            );
        }
        if (entity.getAssignmentSettings() != null) {
            resp.setAssignmentSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse.builder()
                    .autoAssign(entity.getAssignmentSettings().getAutoAssign())
                    .assignmentType(entity.getAssignmentSettings().getAssignmentType())
                    .workingHoursEnabled(entity.getAssignmentSettings().getWorkingHoursEnabled())
                    .workingHoursStart(entity.getAssignmentSettings().getWorkingHoursStart())
                    .workingHoursEnd(entity.getAssignmentSettings().getWorkingHoursEnd())
                    .workingDays(entity.getAssignmentSettings().getWorkingDays())
                    .maxConcurrentTickets(entity.getAssignmentSettings().getMaxConcurrentTickets())
                    .build()
            );
        }
        if (entity.getNotificationSettings() != null) {
            resp.setNotificationSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse.builder()
                    .emailEnabled(entity.getNotificationSettings().getEmailEnabled())
                    .emailEvents(entity.getNotificationSettings().getEmailEvents())
                    .emailTemplates(entity.getNotificationSettings().getEmailTemplates())
                    .internalEnabled(entity.getNotificationSettings().getInternalEnabled())
                    .internalEvents(entity.getNotificationSettings().getInternalEvents())
                    .webhookEnabled(entity.getNotificationSettings().getWebhookEnabled())
                    .webhookUrl(entity.getNotificationSettings().getWebhookUrl())
                    .webhookEvents(entity.getNotificationSettings().getWebhookEvents())
                    .build()
            );
        }
        if (entity.getCustomFieldSettings() != null) {
            resp.setCustomFieldSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse.builder()
                    .content(entity.getCustomFieldSettings().getContent())
                    .build()
            );
        }
        // 草稿版本映射
        if (entity.getDraftBasicSettings() != null) {
            resp.setDraftBasicSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketBasicSettingsResponse.builder()
                    .numberPrefix(entity.getDraftBasicSettings().getNumberPrefix())
                    .numberLength(entity.getDraftBasicSettings().getNumberLength())
                    .defaultPriority(entity.getDraftBasicSettings().getDefaultPriority())
                    .validityDays(entity.getDraftBasicSettings().getValidityDays())
                    .autoCloseHours(entity.getDraftBasicSettings().getAutoCloseHours())
                    .enableAutoClose(entity.getDraftBasicSettings().getEnableAutoClose())
                    .build()
            );
        }
        if (entity.getDraftStatusFlowSettings() != null) {
            resp.setDraftStatusFlowSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketStatusFlowSettingsResponse.builder()
                    .content(entity.getDraftStatusFlowSettings().getContent())
                    .build()
            );
        }
        if (entity.getDraftPrioritySettings() != null) {
            resp.setDraftPrioritySettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketPrioritySettingsResponse.builder()
                    .content(entity.getDraftPrioritySettings().getContent())
                    .build()
            );
        }
        if (entity.getDraftAssignmentSettings() != null) {
            resp.setDraftAssignmentSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsResponse.builder()
                    .autoAssign(entity.getDraftAssignmentSettings().getAutoAssign())
                    .assignmentType(entity.getDraftAssignmentSettings().getAssignmentType())
                    .workingHoursEnabled(entity.getDraftAssignmentSettings().getWorkingHoursEnabled())
                    .workingHoursStart(entity.getDraftAssignmentSettings().getWorkingHoursStart())
                    .workingHoursEnd(entity.getDraftAssignmentSettings().getWorkingHoursEnd())
                    .workingDays(entity.getDraftAssignmentSettings().getWorkingDays())
                    .maxConcurrentTickets(entity.getDraftAssignmentSettings().getMaxConcurrentTickets())
                    .build()
            );
        }
        if (entity.getDraftNotificationSettings() != null) {
            resp.setDraftNotificationSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketNotificationSettingsResponse.builder()
                    .emailEnabled(entity.getDraftNotificationSettings().getEmailEnabled())
                    .emailEvents(entity.getDraftNotificationSettings().getEmailEvents())
                    .emailTemplates(entity.getDraftNotificationSettings().getEmailTemplates())
                    .internalEnabled(entity.getDraftNotificationSettings().getInternalEnabled())
                    .internalEvents(entity.getDraftNotificationSettings().getInternalEvents())
                    .webhookEnabled(entity.getDraftNotificationSettings().getWebhookEnabled())
                    .webhookUrl(entity.getDraftNotificationSettings().getWebhookUrl())
                    .webhookEvents(entity.getDraftNotificationSettings().getWebhookEvents())
                    .build()
            );
        }
        if (entity.getDraftCustomFieldSettings() != null) {
            resp.setDraftCustomFieldSettings(
                com.bytedesk.ticket.ticket_settings.sub.dto.TicketCustomFieldSettingsResponse.builder()
                    .content(entity.getDraftCustomFieldSettings().getContent())
                    .build()
            );
        }
        return resp;
    }

    @Override
    public TicketSettingsExcel convertToExcel(TicketSettingsEntity entity) {
        return modelMapper.map(entity, TicketSettingsExcel.class);
    }
    
    public void initTicketSettings(String orgUid) {
        // log.info("initThreadTicketSettings");
        // for (String ticketSettings : TicketSettingsInitData.getAllTicketSettingss()) {
        //     TicketSettingsRequest ticketSettingsRequest = TicketSettingsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, ticketSettings))
        //             .name(ticketSettings)
        //             .order(0)
        //             .type(TicketSettingsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(ticketSettingsRequest);
        // }
    }

    
    
}
