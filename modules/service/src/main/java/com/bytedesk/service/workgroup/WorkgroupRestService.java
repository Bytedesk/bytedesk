/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:07:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;
import java.util.Objects;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;

import org.modelmapper.ModelMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Workgroup Management Service - Workgroup and team management service")
public class WorkgroupRestService extends BaseRestService<WorkgroupEntity, WorkgroupRequest, WorkgroupResponse> {

    private final WorkgroupRepository workgroupRepository;

    private final ThreadRepository threadRepository;

    private final ThreadRestService threadRestService;

    private final AgentRestService agentRestService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final WorkgroupSettingsRestService workgroupSettingsRestService;

    private final OrganizationRestService organizationRestService;

    private OrganizationEntity requireOrganization(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            throw new IllegalArgumentException("orgUid is required");
        }
        return organizationRestService.findByUid(orgUid)
                .orElseThrow(() -> new RuntimeException("Organization with UID: " + orgUid + " not found."));
    }

    private int resolveMaxWorkgroups(OrganizationEntity organization) {
        return organization.getMaxWorkgroups() != null ? organization.getMaxWorkgroups() : 20;
    }

    private void assertWorkgroupCapacityAvailable(String orgUid) {
        if (authService.getUser() != null && authService.getUser().isSuperUser()) {
            return;
        }
        OrganizationEntity organization = requireOrganization(orgUid);
        int maxWorkgroups = resolveMaxWorkgroups(organization);
        long current = workgroupRepository.countByOrgUidAndDeletedFalse(orgUid);
        if (current >= maxWorkgroups) {
            throw new RuntimeException("Organization workgroup limit exceeded");
        }
    }

    @Override
    public WorkgroupResponse queryByUid(WorkgroupRequest request) {
        WorkgroupEntity workgroup = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + request.getUid()));

        // 安全兜底：若请求携带 orgUid，则强制校验资源归属组织，避免凭 UID 跨组织探测
        if (StringUtils.hasText(request.getOrgUid()) && !request.getOrgUid().equals(workgroup.getOrgUid())) {
            throw new RuntimeException("workgroup org mismatch");
        }

        return convertToResponse(workgroup);
    }

    @Transactional
    public WorkgroupResponse create(WorkgroupRequest request) {
        // 判断uid是否已经存储，如果已经存在，则不创建新的workgroup
        if (StringUtils.hasText(request.getUid()) && findByUid(request.getUid()).isPresent()) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        assertWorkgroupCapacityAvailable(request.getOrgUid());
        //
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .nickname(request.getNickname())
                .build();
        if (!StringUtils.hasText(request.getUid())) {
            workgroup.setUid(uidUtils.getUid());
        } else {
            workgroup.setUid(request.getUid());
        }
        workgroup.setOrgUid(request.getOrgUid());
        //
        // 绑定工作组配置：优先使用请求中的 settingsUid，否则使用组织默认配置
        if (StringUtils.hasText(request.getSettingsUid())) {
            workgroupSettingsRestService.findByUid(request.getSettingsUid())
                .ifPresentOrElse(workgroup::setSettings,
                    () -> workgroup.setSettings(
                        workgroupSettingsRestService.getOrCreateDefault(request.getOrgUid())));
        } else {
            workgroup.setSettings(workgroupSettingsRestService.getOrCreateDefault(request.getOrgUid()));
        }
        //
        if (request.getAgentUids() != null) {
            Iterator<String> agentIterator = request.getAgentUids().iterator();
            while (agentIterator.hasNext()) {
                String agentUid = agentIterator.next();
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agentEntity = agentOptional.get();
                    workgroup.getAgents().add(agentEntity);
                } else {
                    throw new RuntimeException(agentUid + " is not found.");
                }
            }
        }

        // workgroup admins (optional)
        if (request.getAdminUids() != null && !request.getAdminUids().isEmpty()) {
            Iterator<String> adminIterator = request.getAdminUids().iterator();
            while (adminIterator.hasNext()) {
                String adminUid = adminIterator.next();
                Optional<AgentEntity> adminOptional = agentRestService.findByUid(adminUid);
                if (adminOptional.isPresent()) {
                    workgroup.getAdmins().add(adminOptional.get());
                } else {
                    throw new RuntimeException(adminUid + " is not found.");
                }
            }
        }
        // messageLeaveAgent 兜底：优先使用请求指定，其次使用第一个客服，最后保持为空
        if (StringUtils.hasText(request.getMessageLeaveAgentUid())) {
            Optional<AgentEntity> agentOptional = agentRestService.findByUid(request.getMessageLeaveAgentUid());
            agentOptional.ifPresent(workgroup::setMessageLeaveAgent);
        } else if (workgroup.getAgents() != null && !workgroup.getAgents().isEmpty()) {
            workgroup.setMessageLeaveAgent(workgroup.getAgents().get(0));
        } // else: 保持为 null
        //
        WorkgroupEntity updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }

        return convertToResponse(updatedWorkgroup);
    }

    @Transactional
    public WorkgroupResponse update(WorkgroupRequest request) {
        Optional<WorkgroupEntity> workgroupOptional = findByUid(request.getUid());
        if (!workgroupOptional.isPresent()) {
            throw new RuntimeException(request.getUid() + " is not found.");
        }
        //
        WorkgroupEntity workgroup = workgroupOptional.get();
        //
        // modelMapper.map(workgroupRequest, workgroup); // 会把orgUid给清空为null
        workgroup.setNickname(request.getNickname());
        workgroup.setAvatar(request.getAvatar());
        workgroup.setDescription(request.getDescription());
        // workgroup.setRoutingMode(request.getRoutingMode());
        workgroup.setStatus(request.getStatus());
        //
        // 如果前端未传 agentUids，则不改动现有客服列表；只有在明确传入时才覆盖
        boolean agentsChanged = false;
        if (request.getAgentUids() != null && !request.getAgentUids().isEmpty()) {
            agentsChanged = true;
            workgroup.getAgents().clear();
            Iterator<String> iterator = request.getAgentUids().iterator();
            while (iterator.hasNext()) {
                String agentUid = iterator.next();
                Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
                if (agentOptional.isPresent()) {
                    AgentEntity agentEntity = agentOptional.get();
                    workgroup.getAgents().add(agentEntity);
                } else {
                    throw new RuntimeException(agentUid + " is not found.");
                }
            }
        }

        // 如果前端未传 adminUids(null)，则不改动现有管理员列表；传入空数组([])则清空
        if (request.getAdminUids() != null) {
            workgroup.getAdmins().clear();
            Iterator<String> iterator = request.getAdminUids().iterator();
            while (iterator.hasNext()) {
                String adminUid = iterator.next();
                Optional<AgentEntity> adminOptional = agentRestService.findByUid(adminUid);
                if (adminOptional.isPresent()) {
                    workgroup.getAdmins().add(adminOptional.get());
                } else {
                    throw new RuntimeException(adminUid + " is not found.");
                }
            }
        }

        // 同步更新 settings 引用（支持仅更新 settings）
        if (StringUtils.hasText(request.getSettingsUid())) {
            workgroupSettingsRestService.findByUid(request.getSettingsUid()).ifPresent(workgroup::setSettings);
        }

        // robust 设置留言处理坐席：
        if (StringUtils.hasText(request.getMessageLeaveAgentUid())) {
            agentRestService.findByUid(request.getMessageLeaveAgentUid()).ifPresent(workgroup::setMessageLeaveAgent);
        } else if (agentsChanged) {
            // 当客服列表被修改但未显式指定留言坐席时：若列表非空取第一个，否则置空，避免越界
            if (workgroup.getAgents() != null && !workgroup.getAgents().isEmpty()) {
                workgroup.setMessageLeaveAgent(workgroup.getAgents().get(0));
            } else {
                workgroup.setMessageLeaveAgent(null);
            }
        } // 未修改客服且未指定留言坐席时，保留原有设置
        //
        WorkgroupEntity updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }

        return convertToResponse(updatedWorkgroup);
    }

    @Transactional
    public WorkgroupResponse updateAvatar(WorkgroupRequest request) {
        WorkgroupEntity workgroup = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + request.getUid()));
        workgroup.setAvatar(request.getAvatar());
        //
        WorkgroupEntity updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }
        
        return convertToResponse(updatedWorkgroup);
    }

    @Transactional
    public WorkgroupResponse updateStatus(WorkgroupRequest request) {
        WorkgroupEntity workgroup = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + request.getUid()));
        workgroup.setStatus(request.getStatus());

        WorkgroupEntity updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }
        return convertToResponse(updatedWorkgroup);
    }

    // 待同步agent entity状态之后，开启缓存
    // @Cacheable(value = "workgroup", key = "#uid", unless = "#result == null")
    public Optional<WorkgroupEntity> findByUid(String uid) {
        Optional<WorkgroupEntity> workgroupOptional = workgroupRepository.findByUid(uid);
        // 确保从数据库加载时所有依赖属性都被初始化，防止延迟加载的问题
        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            // 确保settings被初始化
            if (workgroup.getSettings() != null) {
                workgroup.getSettings().toString(); // 触发加载
                // 触发嵌套的settings加载
                if (workgroup.getSettings().getRobotSettings() != null) {
                    workgroup.getSettings().getRobotSettings().toString();
                }
                if (workgroup.getSettings().getMessageLeaveSettings() != null) {
                    workgroup.getSettings().getMessageLeaveSettings().toString();
                }
                if (workgroup.getSettings().getServiceSettings() != null) {
                    workgroup.getSettings().getServiceSettings().toString();
                }
                if (workgroup.getSettings().getQueueSettings() != null) {
                    workgroup.getSettings().getQueueSettings().toString();
                }
            }
            // 确保agents被初始化
            if (workgroup.getAgents() != null) {
                workgroup.getAgents().size(); // 触发加载
                if (workgroup.getAgents().isEmpty()) {
                    log.warn("工作组 {} 没有客服成员", workgroup.getUid());
                }
            } else {
                // 如果agents为null，初始化为空列表，避免后续NPE
                workgroup.setAgents(new ArrayList<>());
                log.warn("工作组 {} 的客服列表为null，已初始化为空列表", workgroup.getUid());
            }

            // 确保admins被初始化
            if (workgroup.getAdmins() != null) {
                workgroup.getAdmins().size(); // 触发加载
            } else {
                workgroup.setAdmins(new ArrayList<>());
            }
            // 确保messageLeaveAgent被初始化
            if (workgroup.getMessageLeaveAgent() != null) {
                workgroup.getMessageLeaveAgent().getUid(); // 触发加载
            }
        }
        return workgroupOptional;
    }

    @Transactional(readOnly = true)
    public List<WorkgroupEntity> findByOrgUid(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return new ArrayList<>();
        }
        List<WorkgroupEntity> workgroups = workgroupRepository.findByOrgUidAndDeletedFalse(orgUid);
        return workgroups != null ? workgroups : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public Optional<WorkgroupEntity> findAnyByOrgUid(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return Optional.empty();
        }
        List<WorkgroupEntity> workgroups = findByOrgUid(orgUid);
        if (workgroups == null || workgroups.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(workgroups.get(0));
    }
    
    @CachePut(value = "workgroup", key = "#entity.uid", unless = "#result == null")
    @Override
    protected WorkgroupEntity doSave(WorkgroupEntity entity) {
        // 确保agents不为null，避免缓存后出现NPE
        if (entity.getAgents() == null) {
            entity.setAgents(new ArrayList<>());
            log.warn("保存前检测到工作组 {} 的客服列表为null，已初始化为空列表", entity.getUid());
        }
        // 确保admins不为null
        if (entity.getAdmins() == null) {
            entity.setAdmins(new ArrayList<>());
        }
        WorkgroupEntity savedEntity = workgroupRepository.save(entity);
        
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        if (savedEntity.getAgents() != null) {
            savedEntity.getAgents().size(); // 触发加载
        }
        if (savedEntity.getAdmins() != null) {
            savedEntity.getAdmins().size();
        }
        
        return savedEntity;
    }

    @CacheEvict(value = "workgroup", key = "#uid")
    public void deleteByUid(String uid) {
        Optional<WorkgroupEntity> workgroupOptional = findByUid(uid);
        workgroupOptional.ifPresent(workgroup -> {
            workgroup.setDeleted(true);
            save(workgroup);
        });
    }
    
    @Override
    public void delete(WorkgroupRequest request) {
        deleteByUid(request.getUid());
    }

    /**
     * 批量设置某个 agent 作为管理员(监控/接管权限)的工作组列表。
     *
     * 规则：只对每个 workgroup 增删该 agent 一条关系，不影响其它管理员。
     */
    @Transactional
    public Map<String, Object> updateAdminWorkgroupsForAgent(WorkgroupAdminRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }
        if (!StringUtils.hasText(request.getAgentUid())) {
            throw new IllegalArgumentException("agentUid is required");
        }

        AgentEntity agent = agentRestService.findByUid(request.getAgentUid())
                .orElseThrow(() -> new RuntimeException(request.getAgentUid() + " is not found."));

        Set<String> targetUids = new HashSet<>(
                Optional.ofNullable(request.getWorkgroupUids()).orElse(Collections.emptyList())
                        .stream().filter(StringUtils::hasText).collect(Collectors.toSet()));

        List<WorkgroupEntity> existingAdminWorkgroups = workgroupRepository.findByAdminAgentUid(request.getAgentUid());
        if (existingAdminWorkgroups == null) {
            existingAdminWorkgroups = new ArrayList<>();
        }

        // 仅处理当前 org 的 workgroup，避免跨组织误操作
        List<WorkgroupEntity> existingInOrg = existingAdminWorkgroups.stream()
                .filter(Objects::nonNull)
                .filter(w -> request.getOrgUid().equals(w.getOrgUid()))
            .filter(w -> !w.isDeleted())
                .collect(Collectors.toList());

        Set<String> existingUids = existingInOrg.stream().map(WorkgroupEntity::getUid).collect(Collectors.toSet());

        Set<String> toAdd = new HashSet<>(targetUids);
        toAdd.removeAll(existingUids);

        Set<String> toRemove = new HashSet<>(existingUids);
        toRemove.removeAll(targetUids);

        int added = 0;
        int removed = 0;

        // remove
        for (WorkgroupEntity workgroup : existingInOrg) {
            if (!toRemove.contains(workgroup.getUid())) {
                continue;
            }
            if (workgroup.getAdmins() == null) {
                workgroup.setAdmins(new ArrayList<>());
            }
            boolean changed = workgroup.getAdmins().removeIf(a -> a != null && request.getAgentUid().equals(a.getUid()));
            if (changed) {
                save(workgroup);
                removed++;
            }
        }

        // add
        for (String workgroupUid : toAdd) {
            WorkgroupEntity workgroup = findByUid(workgroupUid)
                    .orElseThrow(() -> new RuntimeException("workgroup not found with uid: " + workgroupUid));
            if (!request.getOrgUid().equals(workgroup.getOrgUid())) {
                throw new RuntimeException("workgroup org mismatch: " + workgroupUid);
            }
            if (workgroup.isDeleted()) {
                continue;
            }
            if (workgroup.getAdmins() == null) {
                workgroup.setAdmins(new ArrayList<>());
            }
            boolean exists = workgroup.getAdmins().stream().anyMatch(a -> a != null && request.getAgentUid().equals(a.getUid()));
            if (!exists) {
                workgroup.getAdmins().add(agent);
                save(workgroup);
                added++;
            }
        }

        return Map.of(
                "agentUid", request.getAgentUid(),
                "orgUid", request.getOrgUid(),
                "assignedWorkgroupUids", targetUids,
                "added", added,
                "removed", removed);
    }

    @Override
    public WorkgroupEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            WorkgroupEntity entity) {
        try {
            Optional<WorkgroupEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkgroupEntity latestEntity = latest.get();
                // 合并需要保留的数据，保留基本信息
                latestEntity.setNickname(entity.getNickname());
                latestEntity.setAvatar(entity.getAvatar());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setStatus(entity.getStatus());
                // TODO: Settings should be managed through WorkgroupSettingsEntity
                // latestEntity.setMessageLeaveSettings(entity.getMessageLeaveSettings());
                // latestEntity.setRobotSettings(entity.getRobotSettings());
                // latestEntity.setServiceSettings(entity.getServiceSettings());
                // latestEntity.setQueueSettings(entity.getQueueSettings());
                // latestEntity.setInviteSettings(entity.getInviteSettings());
                // Update settings reference instead
                if (entity.getSettings() != null) {
                    latestEntity.setSettings(entity.getSettings());
                }
                return workgroupRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突", ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public WorkgroupResponse convertToResponse(WorkgroupEntity entity) {
        return ServiceConvertUtils.convertToWorkgroupResponse(entity);
    }

    public WorkgroupExcel convertToExcel(WorkgroupEntity entity) {
        return modelMapper.map(entity, WorkgroupExcel.class);
    }

    @Override
    protected Specification<WorkgroupEntity> createSpecification(WorkgroupRequest request) {
        return WorkgroupSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkgroupEntity> executePageQuery(Specification<WorkgroupEntity> spec, Pageable pageable) {
        return workgroupRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> findWorkgroupUidsByUserUid(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            return new ArrayList<>();
        }

        Optional<AgentEntity> agentOptional;
        try {
            agentOptional = agentRestService.findByUserUid(userUid);
        } catch (IllegalStateException e) {
            log.warn("findWorkgroupUidsByUserUid skipped: {}, userUid={}", e.getMessage(), userUid);
            return new ArrayList<>();
        }
        if (!agentOptional.isPresent()) {
            return new ArrayList<>();
        }

        List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agentOptional.get().getUid());
        List<String> workgroupUids = new ArrayList<>();
        for (WorkgroupEntity workgroup : workgroups) {
            if (workgroup != null && !workgroup.isDeleted() && StringUtils.hasText(workgroup.getUid())) {
                workgroupUids.add(workgroup.getUid());
            }
        }
        return workgroupUids;
    }

    @Transactional(readOnly = true)
    public List<WorkgroupResponse> queryAdminWorkgroups(String agentUid, String orgUid) {
        if (!StringUtils.hasText(agentUid)) {
            return new ArrayList<>();
        }

        List<WorkgroupEntity> workgroups = workgroupRepository.findByAdminAgentUid(agentUid);
        return workgroups.stream()
                .filter(w -> w != null && !w.isDeleted())
                .filter(w -> !StringUtils.hasText(orgUid) || orgUid.equals(w.getOrgUid()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ThreadResponse> queryAdminOngoingThreads(
            String agentUid,
            String orgUid,
            int pageNumber,
            int pageSize) {

        int safePageNumber = Math.max(0, pageNumber);
        int safePageSize = pageSize <= 0 ? 100 : pageSize;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(safePageNumber, safePageSize);

        if (!StringUtils.hasText(agentUid) || !StringUtils.hasText(orgUid)) {
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Set<String> adminWorkgroupUids = workgroupRepository.findByAdminAgentUid(agentUid).stream()
                .filter(w -> w != null && !w.isDeleted())
                .filter(w -> orgUid.equals(w.getOrgUid()))
                .map(WorkgroupEntity::getUid)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        if (adminWorkgroupUids.isEmpty()) {
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<String> ongoingStatuses = Arrays.asList(
                ThreadProcessStatusEnum.CHATTING.name(),
                ThreadProcessStatusEnum.NEW.name());

        List<ThreadEntity> candidates = threadRepository
                .findByOrgUidAndTypeAndStatusInAndDeletedFalseOrderByUpdatedAtDescCreatedAtDesc(
                        orgUid,
                        ThreadTypeEnum.WORKGROUP.name(),
                        ongoingStatuses);

        List<ThreadResponse> matched = candidates.stream()
                .filter(t -> {
                    try {
                        if (t == null || t.getWorkgroup() == null) {
                            return false;
                        }
                        UserProtobuf workgroup = UserProtobuf.fromJson(t.getWorkgroup());
                        return workgroup != null
                                && StringUtils.hasText(workgroup.getUid())
                                && adminWorkgroupUids.contains(workgroup.getUid());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(threadRestService::convertToResponse)
                .collect(Collectors.toList());

        int start = Math.min(safePageNumber * safePageSize, matched.size());
        int end = Math.min(start + safePageSize, matched.size());
        List<ThreadResponse> pageContent = matched.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, matched.size());
    }


}
