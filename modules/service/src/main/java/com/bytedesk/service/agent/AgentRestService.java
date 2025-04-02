/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 14:50:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionRequest;
import com.bytedesk.core.action.ActionRestService;
import com.bytedesk.core.action.ActionTypeEnum;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.socket.mqtt.MqttConnectionService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.constant.I18ServiceConsts;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.service.settings.ServiceSettingsService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentRestService extends BaseRestService<AgentEntity, AgentRequest, AgentResponse> {

    private final AgentRepository agentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final MemberRestService memberService;

    private final UserService userService;

    private final AuthService authService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final ActionRestService actionService;

    private final ServiceSettingsService serviceSettingsService;

    private final MqttConnectionService mqttConnectionService;

    private final ThreadRestService threadRestService;

    public Page<AgentResponse> queryByOrg(AgentRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AgentEntity> spec = AgentSpecification.search(request);
        Page<AgentEntity> agentPage = agentRepository.findAll(spec, pageable);
        return agentPage.map(this::convertToResponse);
    }

    @Override
    public Page<AgentResponse> queryByUser(AgentRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByUser(request);
    }

    public AgentResponse query(AgentRequest request) {
        UserEntity user = authService.getUser();
        Optional<AgentEntity> agentOptional = findByUserUidAndOrgUid(user.getUid(), request.getOrgUid());
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        return convertToResponse(agentOptional.get());
    }

    public AgentResponse queryDetail(String uid) {
        Optional<AgentEntity> agentOptional = findByUid(uid);
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        return convertToResponse(agentOptional.get());
    }

    @Transactional
    public AgentResponse create(AgentRequest request) {
        //
        Optional<MemberEntity> memberOptional = memberService.findByUid(request.getMemberUid());
        if (!memberOptional.isPresent() || memberOptional.get().getUser() == null) {
            throw new RuntimeException("member uid: " + request.getMemberUid() + " not found");
        }
        // 一个user仅能绑定一个坐席
        if (existsByUserUidAndOrgUid(memberOptional.get().getUser().getUid(), request.getOrgUid())) {
            throw new RuntimeException(I18ServiceConsts.I18N_AGENT_EXISTS);
        }
        // 
        MemberEntity member = memberOptional.get();
        UserEntity user = member.getUser();
        userService.addRoleAgent(member.getUser());
        //
        AgentEntity agent = AgentEntity.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .build();
        if (StringUtils.hasText(request.getUid())) {
            agent.setUid(request.getUid());
        } else {
            agent.setUid(uidUtils.getUid());
        }
        agent.setOrgUid(request.getOrgUid());
        agent.setMember(member);
        agent.setUserUid(user.getUid());
        // 
        Set<String> userIds = mqttConnectionService.getConnectedUserUids();
        if (userIds.contains(agent.getUserUid())) {
            agent.setConnected(true);
        }
        // 保存Agent并检查返回值
        AgentEntity savedAgent = save(agent);
        if (savedAgent == null) {
            throw new RuntimeException("Failed to save agent.");
        }
        // 返回保存后的Agent
        return convertToResponse(savedAgent);
    }

    public void createFromMember(String mobile, String orgUid, String agentUid) {
        //
        Optional<MemberEntity> memberOptional = memberService.findByMobileAndOrgUid(mobile, orgUid);
        if (!memberOptional.isPresent()) {
            return;
        }
        MemberEntity member = memberOptional.get();
        // 创建默认客服
        AgentRequest agentRequest = AgentRequest.builder()
                .uid(agentUid)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .mobile(member.getMobile())
                .memberUid(member.getUid())
                .orgUid(orgUid)
                .build();
        // agentRequest.setUid(agentUid);
        // agentRequest.setOrgUid(orgUid);
        //
        // List<String> worktimeUids = new ArrayList<>();
        // String worktimeUid = worktimeService.createDefault();
        // worktimeUids.add(worktimeUid);
        // agentRequest.getServiceSettings().setWorktimeUids(worktimeUids);
        //
        create(agentRequest);
    }

    @Transactional
    public AgentResponse update(AgentRequest request) {
        //
        Optional<AgentEntity> agentOptional = findByUid(request.getUid());
        if (!agentOptional.isPresent()) {
            // 如果找不到对应的Agent，则返回null
            throw new RuntimeException("null agent found with uid: " + request.getUid());
        }
        //
        AgentEntity agent = agentOptional.get();
        // 更新Agent的信息
        // modelMapper.map(agentRequest, agent); // 需要排除 connected 字段，否则会改变真实连接状态
        agent.setNickname(request.getNickname());
        agent.setAvatar(request.getAvatar());
        agent.setMobile(request.getMobile());
        agent.setEmail(request.getEmail());
        agent.setDescription(request.getDescription());
        agent.setStatus(request.getStatus()); // 更新接待状态
        agent.setMaxThreadCount(request.getMaxThreadCount());
        // 暂不允许修改绑定成员
        // agent.setMember(memberOptional.get());
        // agent.setUserUid(memberOptional.get().getUser().getUid());
        // 
        MessageLeaveSettings messageLeaveSettings = serviceSettingsService.formatAgentMessageLeaveSettings(request);
        agent.setMessageLeaveSettings(messageLeaveSettings);
        // 
        // 一对一人工客服，不支持机器人接待
        // RobotSettings robotSettings = serviceSettingsService.formatAgentRobotSettings(request);
        // agent.setRobotSettings(robotSettings);
        // 
        ServiceSettings serviceSettings = serviceSettingsService.formatAgentServiceSettings(request);
        agent.setServiceSettings(serviceSettings);
        // 
        AutoReplySettings autoReplySettings = serviceSettingsService.formatAgentAutoReplySettings(request);
        agent.setAutoReplySettings(autoReplySettings);
        // 
        QueueSettings queueSettings = serviceSettingsService.formatAgentQueueSettings(request);
        agent.setQueueSettings(queueSettings);
        // 
        InviteSettings inviteSettings = serviceSettingsService.formatAgentInviteSettings(request);
        agent.setInviteSettings(inviteSettings);
        // 保存Agent，并检查返回值
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            // 如果保存失败，可以选择抛出异常或记录日志，这里以抛出异常为例
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        // 转换并返回AgentResponse
        return convertToResponse(updatedAgent);
    }

    // updateAvatar
    @Transactional
    public AgentResponse updateAvatar(AgentRequest request) {
        AgentEntity agent = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        agent.setAvatar(request.getAvatar());
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        return convertToResponse(updatedAgent);
    }

    @Transactional
    public AgentResponse updateStatus(AgentRequest request) {
        // agentRepository.updateStatusByUid(request.getStatus(), request.getUid());
        AgentEntity agent = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        agent.setStatus(request.getStatus()); // 更新接待状态
        // 
        int currentThreadCount = threadRestService.countByThreadTopicAndStateNot(agent.getUid(), ThreadStatusEnum.CLOSED.name());
        agent.setCurrentThreadCount(currentThreadCount);
        log.info("agent: {}", agent.toString());
        // 
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to save agent.");
        }
        // 
        bytedeskEventPublisher.publishEvent(new AgentUpdateStatusEvent(this, updatedAgent));

        return convertToResponse(updatedAgent);
    }

    public AgentResponse syncCurrentThreadCount(AgentRequest request) {

        if (StringUtils.hasText(request.getUid())) {
            AgentEntity agent = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
            int currentThreadCount = threadRestService.countByThreadTopicAndState(agent.getUid(), ThreadStatusEnum.STARTED.name());
            agent.setCurrentThreadCount(currentThreadCount);
            // 更新Agent的信息
            AgentEntity updatedAgent = save(agent);
            if (updatedAgent == null) {
                throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
            }
            return convertToResponse(updatedAgent);
        }
        
        return null;
    }

    @Transactional
    public AgentResponse updateAutoReply(AgentRequest request) {
        AgentEntity agent = findByUid(request.getUid()).orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        //
        AutoReplySettings autoReplySettings = modelMapper.map(request.getAutoReplySettings(),
                AutoReplySettings.class);
        agent.setAutoReplySettings(autoReplySettings);
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        return convertToResponse(updatedAgent);
    }

    /**
     * 更新坐席在线状态
     * 
     * @param userUid 用户ID
     * @param connected 是否在线
     */
    @Async
    @Transactional
    public void updateConnect(String userUid, boolean connected) {
        // 参数uid是userUid，非agent uid，所以无法直接更新
        agentRepository.updateConnectedByUserUid(connected, userUid);
        // TODO: redis cache agent online status
    }

    @Cacheable(value = "agent", key = "#uid", unless = "#result == null")
    public Optional<AgentEntity> findByUid(String uid) {
        return agentRepository.findByUid(uid);
    }

    @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUid(String userUid) {
        return agentRepository.findByUserUid(userUid);
    }

    @Cacheable(value = "agent", key = "#mobile", unless = "#result == null")
    public Optional<AgentEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        return agentRepository.findByMobileAndOrgUidAndDeletedFalse(mobile, orgUid);
    }

    @Cacheable(value = "agent", key = "#email", unless = "#result == null")
    public Optional<AgentEntity> findByEmailAndOrgUid(String email, String orgUid) {
        return agentRepository.findByEmailAndOrgUidAndDeletedFalse(email, orgUid);
    }

    @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.findByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
    }

    public Boolean existsByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.existsByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
    }

    public List<AgentEntity> findAllConnected() {
        return findByConnected(true);
    }

    public List<AgentEntity> findByConnected(boolean connected) {
        return agentRepository.findByConnectedAndDeletedFalse(connected);
    }

    @Caching(put = {
            @CachePut(value = "agent", key = "#agent.uid"),
            @CachePut(value = "agent", key = "#agent.mobile", unless = "#agent.mobile == null"),
            @CachePut(value = "agent", key = "#agent.email", unless = "#agent.email == null"),
    })
    public AgentEntity save(AgentEntity agent) {
        try {
            return agentRepository.save(agent);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 乐观锁冲突处理逻辑
            handleOptimisticLockingFailureException(e, agent);
        }
        return null;
    }

    @CacheEvict(value = "agent", key = "#uid")
    public void deleteByUid(String uid) {
        agentRepository.updateDeletedByUid(true, uid);
    }

    @Override
    public void delete(AgentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentResponse convertToResponse(AgentEntity entity) {
        return ServiceConvertUtils.convertToAgentResponse(entity);
    }

    private static final int MAX_RETRY_ATTEMPTS = 3; // 设定最大重试次数
    private static final long RETRY_DELAY_MS = 5000; // 设定重试间隔（毫秒）
    private final Queue<AgentEntity> retryQueue = new LinkedList<>();

    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentEntity agent) {
        retryQueue.add(agent);
        processRetryQueue();
    }

    private void processRetryQueue() {
        while (!retryQueue.isEmpty()) {
            AgentEntity agent = retryQueue.poll(); // 从队列中取出一个元素
            if (agent == null) {
                break; // 队列为空，跳出循环
            }

            int retryCount = 0;
            while (retryCount < MAX_RETRY_ATTEMPTS) {
                try {
                    // 尝试更新Topic对象
                    agentRepository.save(agent);
                    // 更新成功，无需进一步处理
                    log.info("Optimistic locking succeeded for agent: {}", agent.getUid());
                    break; // 跳出内部循环
                } catch (ObjectOptimisticLockingFailureException ex) {
                    // 捕获乐观锁异常
                    log.error("Optimistic locking failure for agent: {}, retry count: {}", agent.getUid(),
                            retryCount + 1);
                    // 等待一段时间后重试
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted while waiting for retry", ie);
                        return;
                    }
                    retryCount++; // 增加重试次数

                    // 如果还有重试机会，则将agent放回队列末尾
                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        // FIXME: 发现会一直失败，暂时不重复处理
                        // retryQueue.add(agent);
                    } else {
                        // 所有重试都失败了
                        handleFailedRetries(agent);
                    }
                }
            }
        }
    }

    private void handleFailedRetries(AgentEntity agent) {
        String agentJSON = JSONObject.toJSONString(agent);
        ActionRequest actionRequest = ActionRequest.builder()
                .title("agent")
                .action("save")
                .description("All retry attempts failed for optimistic locking")
                .extra(agentJSON)
                .build();
        actionRequest.setType(ActionTypeEnum.FAILED.name());
        actionService.create(actionRequest);
        // bytedeskEventPublisher.publishActionCreateEvent(actionRequest);
        log.error("All retry attempts failed for optimistic locking of agent: {}", agent.getUid());
        // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
        // notifyUserOfFailure(agent);
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
        //     // log.info("worktimeUid {}", worktimeUid);
        //     Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
        //     if (worktimeOptional.isPresent()) {
        //         WorktimeEntity worktimeEntity = worktimeOptional.get();
        //         agent.getServiceSettings().getWorktimes().add(worktimeEntity);
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
        //             log.info("agent faqUid added {}", faqUid);

        //             agent.getServiceSettings().getFaqs().add(faqEntity);
        //         } else {
        //             throw new RuntimeException("agent faq " + faqUid + " not found");
        //         }
        //     }
        // } else {
        //     log.info("agent faq uids is null");
        // }
        // // log.info("agent {}", agent);
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
        //             agent.getServiceSettings().getQuickFaqs().add(quickFaqEntity);
        //         } else {
        //             throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
        //         }
        //     }
        // }

}
