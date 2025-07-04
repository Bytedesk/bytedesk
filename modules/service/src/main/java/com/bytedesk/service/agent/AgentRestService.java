/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 08:57:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.socket.mqtt.MqttConnectionService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.constant.I18ServiceConsts;
import com.bytedesk.service.message_leave.settings.MessageLeaveSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
import com.bytedesk.service.settings.ServiceSettingsService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;

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
            return null;
        }
        return convertToResponse(agentOptional.get());
    }

    public AgentResponse queryByUid(AgentRequest request) {
        Optional<AgentEntity> agentOptional = findByUid(request.getUid());
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        return convertToResponse(agentOptional.get());
    }

    @Transactional
    public AgentResponse initVisitor(AgentRequest request) {
        //
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
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
        initVisitor(agentRequest);
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
        agent.setTimeoutRemindTime(request.getTimeoutRemindTime());
        // 暂不允许修改绑定成员
        // agent.setMember(memberOptional.get());
        // agent.setUserUid(memberOptional.get().getUser().getUid());
        //
        MessageLeaveSettings messageLeaveSettings = serviceSettingsService.formatAgentMessageLeaveSettings(request);
        agent.setMessageLeaveSettings(messageLeaveSettings);
        //
        // 一对一人工客服，不支持机器人接待
        // RobotSettings robotSettings =
        // serviceSettingsService.formatAgentRobotSettings(request);
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
        // InviteSettings inviteSettings =
        // serviceSettingsService.formatAgentInviteSettings(request);
        // agent.setInviteSettings(inviteSettings);
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
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
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
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        agent.setStatus(request.getStatus()); // 更新接待状态
        //
        // int currentThreadCount =
        // threadRestService.countByThreadTopicAndStateNot(agent.getUid(),
        // ThreadProcessStatusEnum.CLOSED.name());
        // agent.setCurrentThreadCount(currentThreadCount);
        log.info("update agent: {} status", agent.getNickname());
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to save agent.");
        }
        //
        bytedeskEventPublisher.publishEvent(new AgentUpdateStatusEvent(this, updatedAgent));

        return convertToResponse(updatedAgent);
    }

    // 在定时任务中执行
    public void updateConnect() {
        List<AgentEntity> agents = findAllConnected();
        Set<String> userIds = mqttConnectionService.getConnectedUserUids();
        // 遍历agents，判断是否在线，如果不在，则更新为离线状态
        for (AgentEntity agent : agents) {
            String userUid = agent.getUserUid();
            if (!userIds.contains(userUid)) {
                log.info("agent updateConnect uid {} offline", userUid);
                updateConnect(userUid, false);
            }
        }
        // 遍历userIds，更新为在线状态
        for (String userUid : userIds) {
            updateConnect(userUid, true);
        }
    }

    public ThreadResponse acceptByAgent(ThreadRequest threadRequest) {
        UserEntity user = authService.getUser();
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUid(user.getUid());
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        //
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("accept thread " + threadRequest.getUid() + " not found");
        }
        ThreadEntity thread = threadOptional.get();
        AgentEntity agent = agentOptional.get();
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        thread.setAgent(agentProtobuf.toJson());
        thread.setOwner(agent.getMember().getUser());
        //
        ThreadEntity updateThread = threadRestService.save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        // 通知queue更新，queue member更新, 增加agent接待数量
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, updateThread));
        bytedeskEventPublisher.publishEvent(new ThreadAcceptEvent(this, updateThread));

        return threadRestService.convertToResponse(updateThread);
    }

    public AgentResponse syncCurrentThreadCount(AgentRequest request) {

        if (StringUtils.hasText(request.getUid())) {
            AgentEntity agent = findByUid(request.getUid())
                    .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
            // int currentThreadCount =
            // threadRestService.countByThreadTopicAndStateNot(agent.getUid(),
            // ThreadProcessStatusEnum.CLOSED.name());
            // agent.setCurrentThreadCount(currentThreadCount);
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
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
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
     * @param userUid   用户ID
     * @param connected 是否在线
     */
    @Async
    @Transactional
    public void updateConnect(String userUid, boolean connected) {
        // 参数uid是userUid，非agent uid，所以无法直接更新
        agentRepository.updateConnectedByUserUid(connected, userUid);
        // TODO: redis cache agent online status
    }

    @Cacheable(value = "agent", key = "#entity.uid", unless = "#result == null")
    @Override
    protected AgentEntity doSave(AgentEntity entity) {
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        if (entity.getMember() != null) {
            entity.getMember().getUser(); // 触发加载
        }
        return agentRepository.save(entity);
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

    @Override
    public AgentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentEntity agent) {
        try {
            log.warn("处理坐席乐观锁冲突: {} - {}", agent.getUid(), e.getMessage());
            
            // 获取最新版本的实体
            Optional<AgentEntity> latest = findByUid(agent.getUid());
            if (latest.isPresent()) {
                AgentEntity latestEntity = latest.get();
                // 将当前实体的变更应用到最新实体上
                // 注意：这里需要根据业务场景决定哪些字段需要保留，哪些需要覆盖
                log.info("最新坐席信息: {}", latestEntity.getNickname());
                
                // 以下是示例，实际需要根据业务需求调整
                // 保留原始实体ID和版本信息
                // agent.setId(latestEntity.getId());
                // agent.setVersion(latestEntity.getVersion());
                
                // 重新尝试保存
                return doSave(agent);
            } else {
                log.error("处理乐观锁冲突时找不到坐席: {}", agent.getUid());
            }
        } catch (Exception ex) {
            log.error("无法处理坐席乐观锁冲突", ex);
            throw new RuntimeException("无法处理坐席乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Cacheable(value = "agent", key = "#uid", unless = "#result == null")
    public Optional<AgentEntity> findByUid(String uid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUid(uid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUid(String userUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUid(userUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    @Cacheable(value = "agent", key = "#mobile", unless = "#result == null")
    public Optional<AgentEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByMobileAndOrgUidAndDeletedFalse(mobile, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    @Cacheable(value = "agent", key = "#email", unless = "#result == null")
    public Optional<AgentEntity> findByEmailAndOrgUid(String email, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByEmailAndOrgUidAndDeletedFalse(email, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUidAndOrgUid(String userUid, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    public Boolean existsByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.existsByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return agentRepository.existsByUid(uid);
    }

    public List<AgentEntity> findAllConnected() {
        return findByConnected(true);
    }

    public List<AgentEntity> findByConnected(boolean connected) {
        return agentRepository.findByConnectedAndDeletedFalse(connected);
    }

}
