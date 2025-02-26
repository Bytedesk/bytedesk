/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 10:05:28
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.leave_msg.settings.LeaveMsgSettings;
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

    public Page<WorkgroupResponse> queryByOrg(WorkgroupRequest workgroupRequest) {
        Pageable pageable = PageRequest.of(workgroupRequest.getPageNumber(),
                workgroupRequest.getPageSize(), Sort.Direction.DESC,
                "id");
        Specification<WorkgroupEntity> specs = WorkgroupSpecification.search(workgroupRequest);
        Page<WorkgroupEntity> workgroupPage = workgroupRepository.findAll(specs, pageable);
        return workgroupPage.map(this::convertToResponse);
    }

    @Override
    public Page<WorkgroupResponse> queryByUser(WorkgroupRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Transactional
    public WorkgroupResponse create(WorkgroupRequest request) {
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
        LeaveMsgSettings leaveMsgSettings = serviceSettingsService.formatWorkgroupLeaveMsgSettings(request);
        workgroup.setLeaveMsgSettings(leaveMsgSettings);
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
        InviteSettings inviteSettings = serviceSettingsService.formatWorkgroupInviteSettings(request);
        workgroup.setInviteSettings(inviteSettings);
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
            return workgroupRepository.save(workgroup);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            WorkgroupEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public WorkgroupResponse convertToResponse(WorkgroupEntity entity) {
        return modelMapper.map(entity, WorkgroupResponse.class);
    }

            // if (request.getServiceSettings() == null
        //         || request.getServiceSettings().getWorktimeUids() == null
        //         || request.getServiceSettings().getWorktimeUids().isEmpty()) {
        //     ServiceSettingsRequest serviceSettings = ServiceSettingsRequest.builder().build();
        //     List<String> worktimeUids = new ArrayList<>();
        //     String worktimeUid = worktimeService.createDefault();
        //     worktimeUids.add(worktimeUid);
        //     serviceSettings.setWorktimeUids(worktimeUids);
        //     request.setServiceSettings(serviceSettings);
        // }
        // //
        // Iterator<String> worktimeIterator = request.getServiceSettings().getWorktimeUids().iterator();
        // while (worktimeIterator.hasNext()) {
        //     String worktimeUid = worktimeIterator.next();
        //     Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
        //     if (worktimeOptional.isPresent()) {
        //         WorktimeEntity worktimeEntity = worktimeOptional.get();

        //         workgroup.getServiceSettings().getWorktimes().add(worktimeEntity);
        //     } else {
        //         throw new RuntimeException(worktimeUid + " is not found.");
        //     }
        // }
        // //
        // if (request.getServiceSettings() != null
        //         && request.getServiceSettings().getFaqUids() != null
        //         && request.getServiceSettings().getFaqUids().size() > 0) {
        //     Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
        //     while (iterator.hasNext()) {
        //         String faqUid = iterator.next();
        //         Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
        //         if (faqOptional.isPresent()) {
        //             FaqEntity faqEntity = faqOptional.get();
        //             workgroup.getServiceSettings().getFaqs().add(faqEntity);
        //         } else {
        //             throw new RuntimeException("faq " + faqUid + " not found");
        //         }
        //     }
        // }
        // if (request.getServiceSettings() != null
        //         && request.getServiceSettings().getQuickFaqUids() != null
        //         && request.getServiceSettings().getQuickFaqUids().size() > 0) {
        //     Iterator<String> iterator = request.getServiceSettings().getQuickFaqUids().iterator();
        //     while (iterator.hasNext()) {
        //         String quickFaqUid = iterator.next();
        //         Optional<FaqEntity> quickFaqOptional = faqService.findByUid(quickFaqUid);
        //         if (quickFaqOptional.isPresent()) {
        //             FaqEntity quickFaqEntity = quickFaqOptional.get();
        //             log.info("quickFaqUid added {}", quickFaqUid);
        //             workgroup.getServiceSettings().getQuickFaqs().add(quickFaqEntity);
        //         } else {
        //             throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
        //         }
        //     }
        // }

}
