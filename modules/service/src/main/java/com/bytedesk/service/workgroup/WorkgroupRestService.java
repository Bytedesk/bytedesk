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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
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
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Workgroup Management Service - Workgroup and team management service")
public class WorkgroupRestService extends BaseRestService<WorkgroupEntity, WorkgroupRequest, WorkgroupResponse> {

    private final WorkgroupRepository workgroupRepository;

    private final AgentRestService agentRestService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final WorkgroupSettingsRestService workgroupSettingsRestService;

    @Transactional(readOnly = true)
    public List<String> findWorkgroupUidsByUserUid(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            return new ArrayList<>();
        }

        Optional<AgentEntity> agentOptional = agentRestService.findByUserUid(userUid);
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
            // 确保messageLeaveAgent被初始化
            if (workgroup.getMessageLeaveAgent() != null) {
                workgroup.getMessageLeaveAgent().getUid(); // 触发加载
            }
        }
        return workgroupOptional;
    }
    
    @CachePut(value = "workgroup", key = "#entity.uid", unless = "#result == null")
    @Override
    protected WorkgroupEntity doSave(WorkgroupEntity entity) {
        // 确保agents不为null，避免缓存后出现NPE
        if (entity.getAgents() == null) {
            entity.setAgents(new ArrayList<>());
            log.warn("保存前检测到工作组 {} 的客服列表为null，已初始化为空列表", entity.getUid());
        }
        WorkgroupEntity savedEntity = workgroupRepository.save(entity);
        
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        if (savedEntity.getAgents() != null) {
            savedEntity.getAgents().size(); // 触发加载
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
        return modelMapper.map(entity, WorkgroupResponse.class);
    }

    @Override
    protected Specification<WorkgroupEntity> createSpecification(WorkgroupRequest request) {
        return WorkgroupSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkgroupEntity> executePageQuery(Specification<WorkgroupEntity> spec, Pageable pageable) {
        return workgroupRepository.findAll(spec, pageable);
    }


}
