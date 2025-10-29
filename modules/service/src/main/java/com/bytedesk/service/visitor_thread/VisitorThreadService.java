/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:08:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 11:56:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.ai.workflow.WorkflowEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorThreadService
        extends BaseRestService<VisitorThreadEntity, VisitorThreadRequest, VisitorThreadResponse> {

    private final VisitorThreadRepository visitorThreadRepository;

    private final ThreadRestService threadRestService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final QueueMemberRestService queueMemberRestService;

    private final AgentRestService agentRestService;

    private final IMessageSendService messageSendService;

    @Cacheable(value = "visitor_thread", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorThreadEntity> findByUid(String uid) {
        return visitorThreadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return visitorThreadRepository.existsByUid(uid);
    }

    @Cacheable(value = "visitor_thread", key = "#topic", unless = "#result == null")
    public Optional<VisitorThreadEntity> findFirstByTopic(String topic) {
        return visitorThreadRepository.findFirstByTopic(topic);
    }

    public ThreadEntity createWorkgroupThread(VisitorRequest visitorRequest, WorkgroupEntity workgroup, String topic) {
        //
        String user = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        String workgroupString = ServiceConvertUtils.convertToUserProtobufJSONString(workgroup);
        //
        String extra = "";
        // 处理渠道相关额外信息
        if (visitorRequest.isSocial()) {
            extra = visitorRequest.getExtra();
        } else {
            extra = workgroup.getSettings() != null 
                ? ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(workgroup.getSettings().getServiceSettings())
                : ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(new com.bytedesk.kbase.settings.ServiceSettingsEntity());
        }
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.WORKGROUP.name())
                .user(user)
                .workgroup(workgroupString)
                .extra(extra)
                .channel(visitorRequest.getChannel())
                .orgUid(workgroup.getOrgUid())
                .build();
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitWorkgroupThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread,
            WorkgroupEntity workgroup) {
        //
        if (visitorRequest.isSocial()) {
            String threadExtra = visitorRequest.getExtra();
            thread.setExtra(threadExtra);
        } else {
            String extra = workgroup.getSettings() != null 
                ? ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(workgroup.getSettings().getServiceSettings())
                : ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(new ServiceSettingsEntity());
            thread.setExtra(extra);
        }
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity createAgentThread(VisitorRequest visitorRequest, AgentEntity agent, String topic) {
        //
        // 考虑到客服信息发生变化，更新客服信息
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        // 访客信息
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        // 考虑到配置可能变化,更新配置
        String extra = ServiceConvertUtils
                .convertToServiceSettingsResponseVisitorJSONString(agent.getSettings() != null 
                    ? agent.getSettings().getServiceSettings() 
                    : new com.bytedesk.kbase.settings.ServiceSettingsEntity());
        //
        String orgUid = agent.getOrgUid();
        //
        // 为避免空指针异常，先检查agent.getMember()是否为空
        UserEntity owner = null;
        if (agent.getMember() != null) {
            owner = agent.getMember().getUser();
        } else {
            log.warn("Agent member is null for agent uid: {}", agent.getUid());
        }

        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.AGENT.name())
                .agent(agentProtobuf.toJson())
                .userUid(agent.getUid()) // 客服uid
                .owner(owner) // 使用安全获取的owner值
                .user(visitor)
                .extra(extra)
                .channel(visitorRequest.getChannel())
                .orgUid(orgUid)
                .build();
        //
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitAgentThreadExtra(ThreadEntity thread, AgentEntity agent) {
        // 考虑到配置可能变化，更新配置
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(agent.getSettings() != null 
            ? agent.getSettings().getServiceSettings() 
            : new ServiceSettingsEntity());
        thread.setExtra(extra);
        if (StringUtils.hasText(thread.getTransfer()) && !BytedeskConsts.EMPTY_JSON_STRING.equals(thread.getTransfer())) {
            // 如果有转接信息，则使用转接信息
            UserProtobuf transferUser = thread.getTransferProtobuf();
            transferUser.setType(UserTypeEnum.AGENT.name());
            thread.setAgent(transferUser.toJson());
        } else {
            UserProtobuf agentProtobuf = agent.toUserProtobuf();
            thread.setAgent(agentProtobuf.toJson()); // 人工客服
        }
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity createRobotThread(VisitorRequest visitorRequest, RobotEntity robot, String topic) {
        //
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robot);
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        String extra = ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(robot.getSettings() != null 
            ? robot.getSettings().getServiceSettings() 
            : new ServiceSettingsEntity());
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.ROBOT.name())
                // .agent(robotString) // 人工客服
                .robot(robotString) // 机器人
                .userUid(robot.getUid()) // 机器人uid
                .user(visitor)
                .channel(visitorRequest.getChannel())
                .orgUid(robot.getOrgUid())
                .extra(extra)
                .build();
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitRobotThreadExtra(ThreadEntity thread, RobotEntity robot) {
        //
        String extra = ServiceConvertUtils
                .convertToServiceSettingsResponseVisitorJSONString(robot.getSettings() != null 
                    ? robot.getSettings().getServiceSettings() 
                    : new ServiceSettingsEntity());
        thread.setExtra(extra);
        // 使用agent的serviceSettings配置
        String robotString = ConvertAiUtils.convertToRobotProtobufString(robot);
        // thread.setAgent(robotString); // 人工客服
        thread.setRobot(robotString); // 机器人
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        //
        return savedEntity;
    }

    public ThreadEntity createWorkflowThread(VisitorRequest visitorRequest, WorkflowEntity workflow, String topic) {
        //
        String workflowString = ServiceConvertUtils.convertToUserProtobufString(workflow);
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.WORKFLOW.name())
                .robot(workflowString) // 工作流
                .userUid(workflow.getUid()) // 工作流uid
                .user(visitor)
                .channel(visitorRequest.getChannel())
                .orgUid(workflow.getOrgUid())
                .build();
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitWorkflowThreadExtra(ThreadEntity thread, WorkflowEntity workflow) {
        //
        String workflowString = ServiceConvertUtils.convertToUserProtobufString(workflow);
        thread.setRobot(workflowString); // 工作流
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        //
        return savedEntity;
    }

    public VisitorThreadEntity update(ThreadEntity thread) {
        Optional<VisitorThreadEntity> visitorThreadOpt = findByUid(thread.getUid());
        if (visitorThreadOpt.isPresent()) {
            VisitorThreadEntity visitorThread = visitorThreadOpt.get();
            visitorThread.setStatus(thread.getStatus());
            //
            return save(visitorThread);
        }
        return null;
    }

    @Override
    public VisitorThreadResponse create(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public VisitorThreadResponse update(VisitorThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * TODO: 频繁查库，待优化
     * 1. 超时关闭会话
     * 2. 超时未回复会话，发送会话超时提醒
     */
    @Async
    public void autoRemindAgentOrCloseThread(List<ThreadEntity> threads) {
        // log.info("autoCloseThread size {}", threads.size());
        threads.forEach(this::processThreadTimeout);
    }

    /**
     * 处理单个线程的超时逻辑
     */
    private void processThreadTimeout(ThreadEntity thread) {
        long diffInMinutes = calculateThreadTimeoutMinutes(thread);
        if (diffInMinutes < 0) {
            return; // 时间异常，跳过处理
        }

        // 处理自动关闭
        handleAutoClose(thread, diffInMinutes);
        
        // 处理超时提醒
        handleTimeoutReminder(thread, diffInMinutes);
    }

    /**
     * 计算线程超时分钟数
     */
    private long calculateThreadTimeoutMinutes(ThreadEntity thread) {
        // 使用BdDateUtils.toTimestamp确保时区一致性，都使用Asia/Shanghai时区
        long currentTimeMillis = BdDateUtils.toTimestamp(BdDateUtils.now());
        long updatedAtMillis = BdDateUtils.toTimestamp(thread.getUpdatedAt());
        
        // 移除Math.abs()，确保时间顺序正确
        long diffInMilliseconds = currentTimeMillis - updatedAtMillis;
        
        // 如果updatedAt在未来，说明时间有问题，跳过处理
        if (diffInMilliseconds < 0) {
            log.warn("Thread {} updatedAt is in the future, skipping auto close check", thread.getUid());
            return -1;
        }
        
        // 转换为分钟
        return TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
    }

    /**
     * 处理自动关闭逻辑
     */
    private void handleAutoClose(ThreadEntity thread, long diffInMinutes) {
        ServiceSettingsResponseVisitor settings = parseThreadSettings(thread);
        double autoCloseValue = getAutoCloseMinutes(settings);
        
        if (diffInMinutes > autoCloseValue) {
            threadRestService.autoClose(thread);
        }
    }

    /**
     * 解析线程设置
     */
    private ServiceSettingsResponseVisitor parseThreadSettings(ThreadEntity thread) {
        if (!StringUtils.hasText(thread.getExtra())) {
            return null;
        }
        
        try {
            return JSON.parseObject(thread.getExtra(), ServiceSettingsResponseVisitor.class);
        } catch (Exception e) {
            log.warn("Failed to parse thread extra JSON for thread {}: {}", thread.getUid(), e.getMessage());
            return null;
        }
    }

    /**
     * 获取自动关闭分钟数
     */
    private double getAutoCloseMinutes(ServiceSettingsResponseVisitor settings) {
        if (settings == null) {
            return 30.0; // 默认30分钟
        }
        
        Double autoCloseMinutes = settings.getAutoCloseMin();
        return (autoCloseMinutes != null) ? autoCloseMinutes : 30.0;
    }

    /**
     * 处理超时提醒逻辑
     */
    private void handleTimeoutReminder(ThreadEntity thread, long diffInMinutes) {
        UserProtobuf agentProtobuf = thread.getAgentProtobuf();
        if (agentProtobuf == null || !StringUtils.hasText(agentProtobuf.getUid())) {
            return;
        }

        Optional<AgentEntity> agentOpt = agentRestService.findByUid(agentProtobuf.getUid());
        if (!agentOpt.isPresent()) {
            return;
        }

        AgentEntity agent = agentOpt.get();
        if (diffInMinutes <= agent.getTimeoutRemindTime()) {
            return;
        }

        processAgentTimeoutReminder(thread, agent);
    }

    /**
     * 处理客服超时提醒
     */
    private void processAgentTimeoutReminder(ThreadEntity thread, AgentEntity agent) {
        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (!queueMemberOpt.isPresent()) {
            return;
        }

        QueueMemberEntity queueMember = queueMemberOpt.get();
        if (queueMember.getAgentTimeout()) {
            return; // 已经超时，不再处理
        }

        if (shouldSendTimeoutReminder(queueMember)) {
            sendRemindMessage(queueMember, thread, agent);
        }
    }

    /**
     * 判断是否应该发送超时提醒
     */
    private boolean shouldSendTimeoutReminder(QueueMemberEntity queueMember) {
        // 访客最后发送消息时间为空，不需要提醒
        if (queueMember.getVisitorLastMessageAt() == null) {
            return false;
        }

        // 客服最后回复时间为空，需要提醒
        if (queueMember.getAgentLastResponseAt() == null) {
            return true;
        }

        // 访客最后发送消息时间 大于 客服最后回复时间，需要提醒
        return queueMember.getVisitorLastMessageAt().isAfter(queueMember.getAgentLastResponseAt());
    }

    private void sendRemindMessage(QueueMemberEntity queueMember, ThreadEntity thread, AgentEntity agent) {
        // 只设置首次超时时间，后续不再更新
        if (queueMember.getAgentTimeoutAt() == null) {
            queueMember.setAgentTimeoutAt(BdDateUtils.now());
            queueMember.setAgentTimeout(true);
        }
        // 更新超时次数
        queueMember.setAgentTimeoutCount(queueMember.getAgentTimeoutCount() + 1);
        // 保存队列成员信息
        queueMemberRestService.save(queueMember);
        // 发送会话超时提醒
        MessageProtobuf messageProtobuf = MessageUtils.createAgentReplyTimeoutMessage(thread,
                agent.getTimeoutRemindTip());
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    @Override
    protected VisitorThreadEntity doSave(VisitorThreadEntity entity) {
        return visitorThreadRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(VisitorThreadRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public VisitorThreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorThreadEntity entity) {
        try {
            Optional<VisitorThreadEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                VisitorThreadEntity latestEntity = latest.get();
                // 合并关键信息
                latestEntity.setStatus(entity.getStatus());
                return visitorThreadRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突", ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public VisitorThreadResponse convertToResponse(VisitorThreadEntity entity) {
        return modelMapper.map(entity, VisitorThreadResponse.class);
    }

    @Override
    protected Specification<VisitorThreadEntity> createSpecification(VisitorThreadRequest request) {
        return VisitorThreadSpecification.search(request, authService);
    }

    @Override
    protected Page<VisitorThreadEntity> executePageQuery(Specification<VisitorThreadEntity> spec, Pageable pageable) {
        return visitorThreadRepository.findAll(spec, pageable);
    }

}
