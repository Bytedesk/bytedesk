/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:46:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.Iterator;
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

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.service.settings.RobotSettings;
import com.bytedesk.service.settings.ServiceSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkgroupRestService extends BaseRestService<WorkgroupEntity, WorkgroupRequest, WorkgroupResponse> {

    private final WorkgroupRepository workgroupRepository;

    private final AgentRestService agentService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final ServiceSettingsService serviceSettingsService;

    private final AuthService authService;

    public Page<WorkgroupResponse> queryByOrg(WorkgroupRequest request) {
        Pageable pageable = request.getPageable();
        Specification<WorkgroupEntity> specs = WorkgroupSpecification.search(request);
        Page<WorkgroupEntity> workgroupPage = workgroupRepository.findAll(specs, pageable);
        return workgroupPage.map(this::convertToResponse);
    }

    @Override
    public Page<WorkgroupResponse> queryByUser(WorkgroupRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user should not be null");
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    public WorkgroupResponse queryByUid(WorkgroupRequest request) {
        Optional<WorkgroupEntity> workgroupOptional = findByUid(request.getUid());
        if (!workgroupOptional.isPresent()) {
            throw new RuntimeException(request.getUid() + " is not found.");
        }
        WorkgroupEntity workgroup = workgroupOptional.get();
        return convertToResponse(workgroup);
    }

    @Transactional
    public WorkgroupResponse create(WorkgroupRequest request) {
        // 判断uid是否已经存储，如果已经存在，则不创建新的workgroup
        if (StringUtils.hasText(request.getUid()) && findByUid(request.getUid()).isPresent()) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
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
        MessageLeaveSettings messageLeaveSettings = serviceSettingsService.formatWorkgroupMessageLeaveSettings(request);
        workgroup.setMessageLeaveSettings(messageLeaveSettings);
        //
        RobotSettings robotSettings = serviceSettingsService.formatWorkgroupRobotSettings(request);
        workgroup.setRobotSettings(robotSettings);
        //
        ServiceSettings serviceSettings = serviceSettingsService.formatWorkgroupServiceSettings(request);
        workgroup.setServiceSettings(serviceSettings);
        //
        QueueSettings queueSettings = serviceSettingsService.formatWorkgroupQueueSettings(request);
        workgroup.setQueueSettings(queueSettings);
        //
        // InviteSettings inviteSettings = serviceSettingsService.formatWorkgroupInviteSettings(request);
        // workgroup.setInviteSettings(inviteSettings);
        //
        Iterator<String> agentIterator = request.getAgentUids().iterator();
        while (agentIterator.hasNext()) {
            String agentUid = agentIterator.next();
            Optional<AgentEntity> agentOptional = agentService.findByUid(agentUid);
            if (agentOptional.isPresent()) {
                AgentEntity agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                throw new RuntimeException(agentUid + " is not found.");
            }
        }
        // 如果未设置messageLeaveAgentUid，则使用第一个agent作为messageLeaveAgentUid
        if (StringUtils.hasText(request.getMessageLeaveAgentUid())) {
            Optional<AgentEntity> agentOptional = agentService.findByUid(request.getMessageLeaveAgentUid());
            if (agentOptional.isPresent()) {
                workgroup.setMessageLeaveAgent(agentOptional.get());
            }
        } else {
            workgroup.setMessageLeaveAgent(workgroup.getAgents().get(0));
        }
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
        workgroup.setRoutingMode(request.getRoutingMode());
        workgroup.setStatus(request.getStatus());
        //
        MessageLeaveSettings messageLeaveSettings = serviceSettingsService.formatWorkgroupMessageLeaveSettings(request);
        workgroup.setMessageLeaveSettings(messageLeaveSettings);
        //
        RobotSettings robotSettings = serviceSettingsService.formatWorkgroupRobotSettings(request);
        workgroup.setRobotSettings(robotSettings);
        //
        ServiceSettings serviceSettings = serviceSettingsService.formatWorkgroupServiceSettings(request);
        workgroup.setServiceSettings(serviceSettings);
        //
        QueueSettings queueSettings = serviceSettingsService.formatWorkgroupQueueSettings(request);
        workgroup.setQueueSettings(queueSettings);
        //
        // InviteSettings inviteSettings = serviceSettingsService.formatWorkgroupInviteSettings(request);
        // workgroup.setInviteSettings(inviteSettings);
        //
        workgroup.getAgents().clear();
        Iterator<String> iterator = request.getAgentUids().iterator();
        while (iterator.hasNext()) {
            String agentUid = iterator.next();
            Optional<AgentEntity> agentOptional = agentService.findByUid(agentUid);
            if (agentOptional.isPresent()) {
                AgentEntity agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                throw new RuntimeException(agentUid + " is not found.");
            }
        }
        // 如果未设置messageLeaveAgentUid，则使用第一个agent作为messageLeaveAgentUid
        if (StringUtils.hasText(request.getMessageLeaveAgentUid())) {
            Optional<AgentEntity> agentOptional = agentService.findByUid(request.getMessageLeaveAgentUid());
            if (agentOptional.isPresent()) {
                workgroup.setMessageLeaveAgent(agentOptional.get());
            }
        } else {
            workgroup.setMessageLeaveAgent(workgroup.getAgents().get(0));
        }
        //
        WorkgroupEntity updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }

        return convertToResponse(updatedWorkgroup);
    }

    // updateAvatar
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

    @Cacheable(value = "workgroup", key = "#uid", unless = "#result == null")
    public Optional<WorkgroupEntity> findByUid(String uid) {
        return workgroupRepository.findByUid(uid);
    }

    @Override
    public WorkgroupEntity save(WorkgroupEntity workgroup) {
        try {
            return doSave(workgroup);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, workgroup);
        }
    }
    
    @Override
    protected WorkgroupEntity doSave(WorkgroupEntity entity) {
        return workgroupRepository.save(entity);
    }

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
                // 配置信息
                latestEntity.setMessageLeaveSettings(entity.getMessageLeaveSettings());
                latestEntity.setRobotSettings(entity.getRobotSettings());
                latestEntity.setServiceSettings(entity.getServiceSettings());
                latestEntity.setQueueSettings(entity.getQueueSettings());
                latestEntity.setInviteSettings(entity.getInviteSettings());
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
        return modelMapper.map(entity, WorkgroupResponse.class);
    }


}
