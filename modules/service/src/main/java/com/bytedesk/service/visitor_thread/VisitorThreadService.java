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

import java.time.Duration;
import java.time.ZonedDateTime;
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
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.utils.ConvertAiUtils;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.agent_settings.AgentSettingsEntity;
import com.bytedesk.service.agent_settings.AgentSettingsRestService;
import com.bytedesk.service.queue_settings.QueueSettingsEntity;
import com.bytedesk.service.robot_to_agent_settings.RobotToAgentSettingsEntity;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsRestService;
import com.bytedesk.ai.robot_settings.RobotSettingsEntity;
import com.bytedesk.ai.robot_settings.RobotSettingsRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.kbase.settings_service.ServiceSettingsResponseVisitor;
import com.bytedesk.service.utils.ThreadMessageUtil;

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

    private final MessageRestService messageRestService;

    // For debug preview: allow overriding settings by uid when debug=true
    private final AgentSettingsRestService agentSettingsRestService;
    private final RobotSettingsRestService robotSettingsRestService;
    private final WorkgroupSettingsRestService workgroupSettingsRestService;
    private final WorkgroupRestService workgroupRestService;

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

    /**
     * 主动触发：判断“访客长时间未发送消息”是否达到阈值，并用 QueueMember.lastNotifiedAt 做节流。
     *
     * 规则：
     * - 优先使用 QueueMember.visitorLastMessageAt 作为基准时间
     * - 若为空，则使用 visitorEnqueueAt 作为兜底
     * - 达到 noResponseTimeoutSeconds 才允许触发
     * - 触发后写入 lastNotifiedAt，避免每分钟重复触发
     */
    public boolean consumeVisitorNoResponseTriggerPermit(String threadUid, int noResponseTimeoutSeconds) {
        if (!StringUtils.hasText(threadUid) || noResponseTimeoutSeconds <= 0) {
            return false;
        }

        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(threadUid);
        if (!queueMemberOpt.isPresent()) {
            return false;
        }

        QueueMemberEntity queueMember = queueMemberOpt.get();
        ZonedDateTime now = BdDateUtils.now();

        ZonedDateTime baseTime = queueMember.getVisitorLastMessageAt();
        if (baseTime == null) {
            baseTime = queueMember.getVisitorEnqueueAt();
        }
        if (baseTime == null) {
            return false;
        }

        long silentSeconds = Duration.between(baseTime, now).getSeconds();
        if (silentSeconds < 0) {
            return false;
        }
        if (silentSeconds < noResponseTimeoutSeconds) {
            return false;
        }

        if (queueMember.getLastNotifiedAt() != null) {
            long sinceNotifySeconds = Duration.between(queueMember.getLastNotifiedAt(), now).getSeconds();
            if (sinceNotifySeconds >= 0 && sinceNotifySeconds < noResponseTimeoutSeconds) {
                return false;
            }
        }

        queueMember.setLastNotifiedAt(now);
        queueMemberRestService.saveAsyncBestEffort(queueMember);
        return true;
    }

    public ThreadEntity createWorkgroupThread(VisitorRequest visitorRequest, WorkgroupEntity workgroup, String topic) {
        //
        String user = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        String workgroupString = ServiceConvertUtils.convertToUserProtobufJSONString(workgroup);
        //
        String extra = buildWorkgroupExtra(visitorRequest, workgroup);
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

    public ThreadEntity reInitWorkgroupThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread, WorkgroupEntity workgroup) {
        //
        String extra = buildWorkgroupExtra(visitorRequest, workgroup);
        thread.setExtra(extra);
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }
    
    public ThreadEntity createAgentThread(VisitorRequest visitorRequest, AgentEntity agent, String topic) {
        //
        // 为避免从缓存加载导致 member/user 为空，这里需要保证 owner 永不为 null
        // 如果发现关联为空，则从数据库重新加载一次
        AgentEntity effectiveAgent = agent;
        if (effectiveAgent == null || !StringUtils.hasText(effectiveAgent.getUid())) {
            throw new IllegalArgumentException("agent and agent.uid must not be null");
        }

        if (effectiveAgent.getMember() == null || effectiveAgent.getMember().getUser() == null) {
            Optional<AgentEntity> refreshedOpt = agentRestService.findByUidFromDatabase(effectiveAgent.getUid());
            if (refreshedOpt.isPresent()) {
                effectiveAgent = refreshedOpt.get();
            }
        }

        UserEntity owner = (effectiveAgent.getMember() != null) ? effectiveAgent.getMember().getUser() : null;
        if (owner == null) {
            throw new IllegalStateException(
                    "Agent owner must not be null (agent.uid=" + effectiveAgent.getUid() + ")");
        }

        // 考虑到客服信息发生变化，更新客服信息
        UserProtobuf agentProtobuf = effectiveAgent.toUserProtobuf();
        // 访客信息
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        // 生成 thread.extra（支持 debug 下 settingsUid 覆盖）
        String extra = buildAgentExtra(visitorRequest, effectiveAgent);
        //
        String orgUid = effectiveAgent.getOrgUid();

        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.AGENT.name())
                .agent(agentProtobuf.toJson())
                .userUid(effectiveAgent.getUid()) // 客服uid
                .owner(owner)
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

    public ThreadEntity reInitAgentThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread, AgentEntity agent) {
        // 生成 thread.extra（支持 debug 下 settingsUid 覆盖）
        String extra = buildAgentExtra(visitorRequest, agent);
        thread.setExtra(extra);
        if (StringUtils.hasText(thread.getTransfer())
                && !BytedeskConsts.EMPTY_JSON_STRING.equals(thread.getTransfer())) {
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
        // 使用精简版存储机器人信息，避免 prompt 过长导致字段超限
        String robotString = ConvertAiUtils.convertToRobotProtobufBasicString(robot);
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        // 生成 thread.extra（支持 debug 下 settingsUid 覆盖）
        String extra = buildRobotExtra(visitorRequest, robot);
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

    public ThreadEntity reInitRobotThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread, RobotEntity robot) {
        //
        String extra = buildRobotExtra(visitorRequest, robot);
        thread.setExtra(extra);
        // 使用精简版存储机器人信息，避免 prompt 过长导致字段超限
        String robotString = ConvertAiUtils.convertToRobotProtobufBasicString(robot);
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
        String workflowString = ServiceConvertUtils.convertToWorkflowProtobufString(workflow);
        String visitor = ServiceConvertUtils.convertToVisitorProtobufJSONString(visitorRequest);
        // 生成 thread.extra（工作流暂时不支持 debug 预览）
        String extra = buildWorkflowExtra(visitorRequest, workflow);
        //
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .topic(topic)
                .type(ThreadTypeEnum.WORKFLOW.name())
                .workflow(workflowString) // 工作流
                .userUid(workflow.getUid()) // 工作流uid
                .user(visitor)
                .channel(visitorRequest.getChannel())
                .orgUid(workflow.getOrgUid())
                .extra(extra)
                .build();
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        return savedEntity;
    }

    public ThreadEntity reInitWorkflowThreadExtra(VisitorRequest visitorRequest, ThreadEntity thread, WorkflowEntity workflow) {
        //
        String extra = buildWorkflowExtra(visitorRequest, workflow);
        thread.setExtra(extra);
        // 设置工作流信息
        String workflowString = ServiceConvertUtils.convertToWorkflowProtobufString(workflow);
        thread.setWorkflow(workflowString); // 工作流
        // 保存
        ThreadEntity savedEntity = threadRestService.save(thread);
        if (savedEntity == null) {
            throw new RuntimeException("Could not save visitor thread");
        }
        //
        return savedEntity;
    }

    /**
     * 根据请求与工作流实体构建 thread.extra
     * - 工作流暂时不支持 debug 预览
     */
    private String buildWorkflowExtra(VisitorRequest visitorRequest, WorkflowEntity workflow) {
        // 工作流暂时没有设置实体，返回空的 JSON 对象
        return BytedeskConsts.EMPTY_JSON_STRING;
    }

    /**
     * 根据请求与工作组实体构建 thread.extra
     * - 社交渠道：直接使用请求中的 extra
     * - 普通渠道：构建 ServiceSettingsResponseVisitor JSON
     *   若 debug=true 且 settingsUid 非空，则优先使用指定 settings 进行预览
     */
    private String buildWorkgroupExtra(VisitorRequest visitorRequest, WorkgroupEntity workgroup) {
        // 社交渠道直接透传
        if (visitorRequest.isSocial()) {
            return visitorRequest.getExtra();
        }

        boolean debug = Boolean.TRUE.equals(visitorRequest.getDebug());
        boolean draft = Boolean.TRUE.equals(visitorRequest.getDraft());
        boolean preview = debug || draft;

        // 默认使用实体上的 settings
        WorkgroupSettingsEntity settings = workgroup.getSettings();
        // debug 预览指定 settings
        if (debug && StringUtils.hasText(visitorRequest.getSettingsUid())) {
            try {
                settings = workgroupSettingsRestService
                        .findByUid(visitorRequest.getSettingsUid())
                        .orElse(settings);
            } catch (Exception e) {
                log.warn("Debug preview: workgroup settingsUid not found: {}", visitorRequest.getSettingsUid());
            }
        }

        ServiceSettingsResponseVisitor extra = ServiceConvertUtils.buildServiceSettingsResponseVisitor(settings, preview);

        RobotToAgentSettingsEntity robotToAgentSettings = null;
        if (settings != null) {
            if (preview && settings.getDraftRobotToAgentSettings() != null) {
                robotToAgentSettings = settings.getDraftRobotToAgentSettings();
            } else {
                robotToAgentSettings = settings.getRobotToAgentSettings();
            }
        }

        boolean allowVisitorManualTransfer = robotToAgentSettings != null
                && Boolean.TRUE.equals(robotToAgentSettings.getEnabled())
                && Boolean.TRUE.equals(robotToAgentSettings.getAllowVisitorManualTransfer());
        extra.setAllowVisitorManualTransfer(allowVisitorManualTransfer);
        if (allowVisitorManualTransfer && robotToAgentSettings != null
                && StringUtils.hasText(robotToAgentSettings.getManualTransferLabel())) {
            extra.setManualTransferLabel(robotToAgentSettings.getManualTransferLabel());
        } else {
            extra.setManualTransferLabel(null);
        }

        return JSON.toJSONString(extra);
    }

    /**
     * 根据请求与客服实体构建 thread.extra
     * - 普通渠道：构建 ServiceSettingsResponseVisitor JSON
     *   若 debug=true 且 settingsUid 非空，则优先使用指定 settings 进行预览
     */
    private String buildAgentExtra(VisitorRequest visitorRequest, AgentEntity agent) {
        AgentSettingsEntity settings = agent.getSettings();
        if (Boolean.TRUE.equals(visitorRequest.getDebug()) && StringUtils.hasText(visitorRequest.getSettingsUid())) {
            try {
                settings = agentSettingsRestService
                        .findByUid(visitorRequest.getSettingsUid())
                        .orElse(settings);
            } catch (Exception e) {
                log.warn("Debug preview: agent settingsUid not found: {}", visitorRequest.getSettingsUid());
            }
        }
        boolean preview = Boolean.TRUE.equals(visitorRequest.getDebug()) || Boolean.TRUE.equals(visitorRequest.getDraft());
        return ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(
            settings, preview);
    }

    /**
     * 根据请求与机器人实体构建 thread.extra
     * - 普通渠道：构建 ServiceSettingsResponseVisitor JSON
     *   若 debug=true 且 settingsUid 非空，则优先使用指定 settings 进行预览
     */
    private String buildRobotExtra(VisitorRequest visitorRequest, RobotEntity robot) {
        RobotSettingsEntity settings = robot.getSettings();
        if (Boolean.TRUE.equals(visitorRequest.getDebug()) && StringUtils.hasText(visitorRequest.getSettingsUid())) {
            try {
                settings = robotSettingsRestService
                        .findByUid(visitorRequest.getSettingsUid())
                        .orElse(settings);
            } catch (Exception e) {
                log.warn("Debug preview: robot settingsUid not found: {}", visitorRequest.getSettingsUid());
            }
        }
        boolean preview = Boolean.TRUE.equals(visitorRequest.getDebug()) || Boolean.TRUE.equals(visitorRequest.getDraft());
        return ServiceConvertUtils.convertToServiceSettingsResponseVisitorJSONString(
            settings, preview);
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
     * 自动提醒客服或关闭会话
     */
    @Async
    public void autoRemindAgentOrCloseThread(List<ThreadEntity> threads) {
        // log.info("autoCloseThread size {}", threads.size());
        threads.forEach(this::processThreadTimeout);
    }

    /**
     * 处理单个Thread会话的超时逻辑
     */
    private void processThreadTimeout(ThreadEntity thread) {
        // 处理排队等待超时，如果已处理则直接返回，不再执行后续逻辑
        if (handleQueueWaitTimeout(thread)) {
            return;
        }

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
        queueMemberRestService.saveAsyncBestEffort(queueMember);
        // 发送会话超时提醒
        MessageProtobuf messageProtobuf = MessageUtils.createAgentReplyTimeoutMessage(thread,
                agent.getTimeoutRemindTip());
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    /**
     * 处理排队等待超时逻辑
     * @return true 如果会话已被处理（触发了离线留言），false 如果未处理
     */
    private boolean handleQueueWaitTimeout(ThreadEntity thread) {
        if (!thread.isQueuing()) {
            return false;
        }

        Optional<QueueMemberEntity> queueMemberOpt = queueMemberRestService.findByThreadUid(thread.getUid());
        if (!queueMemberOpt.isPresent()) {
            return false;
        }

        QueueMemberEntity queueMember = queueMemberOpt.get();
        if (Boolean.TRUE.equals(queueMember.getMessageLeave()) || queueMember.getVisitorEnqueueAt() == null) {
            return false;
        }

        QueueSettingsEntity queueSettings = resolveQueueSettings(thread);
        int maxWaitSeconds = resolveQueueMaxWaitSeconds(queueSettings);
        if (maxWaitSeconds <= 0) {
            return false;
        }

        long waitedSeconds = Duration.between(queueMember.getVisitorEnqueueAt(), BdDateUtils.now()).getSeconds();
        if (waitedSeconds < maxWaitSeconds) {
            return false;
        }

        triggerQueueLeaveMessage(thread, queueMember);
        return true;
    }

    public MessageProtobuf handleQueueOverflowLeaveMessage(ThreadEntity thread, QueueMemberEntity queueMember) {
        Assert.notNull(thread, "ThreadEntity must not be null");
        Assert.notNull(queueMember, "QueueMemberEntity must not be null");
        return triggerQueueLeaveMessage(thread, queueMember);
    }

    private QueueSettingsEntity resolveQueueSettings(ThreadEntity thread) {
        try {
            if (thread.isAgentType()) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(thread.getTopic());
                return agentRestService.findByUid(agentUid)
                        .map(this::resolveQueueSettingsFromAgent)
                        .orElse(null);
            } else if (thread.isWorkgroupType()) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(thread.getTopic());
                return workgroupRestService.findByUid(workgroupUid)
                        .map(this::resolveQueueSettingsFromWorkgroup)
                        .orElse(null);
            }
        } catch (Exception e) {
            log.debug("Failed to resolve queue settings for thread {}: {}", thread.getUid(), e.getMessage());
        }
        return null;
    }

    private QueueSettingsEntity resolveQueueSettingsFromAgent(AgentEntity agent) {
        if (agent.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = agent.getSettings().getQueueSettings();
        if (settings == null) {
            settings = agent.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private QueueSettingsEntity resolveQueueSettingsFromWorkgroup(WorkgroupEntity workgroup) {
        if (workgroup.getSettings() == null) {
            return null;
        }
        QueueSettingsEntity settings = workgroup.getSettings().getQueueSettings();
        if (settings == null) {
            settings = workgroup.getSettings().getDraftQueueSettings();
        }
        return settings;
    }

    private int resolveQueueMaxWaitSeconds(QueueSettingsEntity queueSettings) {
        if (queueSettings == null) {
            return QueueSettingsEntity.DEFAULT_MAX_WAIT_TIME_SECONDS;
        }
        return queueSettings.resolveMaxWaitTimeSeconds();
    }

    private MessageProtobuf triggerQueueLeaveMessage(ThreadEntity thread, QueueMemberEntity queueMember) {
        String leaveMessageTip = resolveLeaveMessageTip(thread);

        thread.setOffline().setContent(leaveMessageTip);
        ThreadEntity savedThread = threadRestService.save(thread);

        queueMember.setMessageLeave(true);
        queueMember.setMessageLeaveAt(BdDateUtils.now());
        queueMember.setVisitorLeavedAt(BdDateUtils.now());
        queueMemberRestService.saveAsyncBestEffort(queueMember);

        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage(leaveMessageTip, savedThread);
        messageRestService.save(message);
        MessageProtobuf protobuf = ServiceConvertUtils.convertToMessageProtobuf(message, savedThread);
        messageSendService.sendProtobufMessage(protobuf);
        return protobuf;
    }

    private String resolveLeaveMessageTip(ThreadEntity thread) {
        try {
            if (thread.isAgentType()) {
                String agentUid = TopicUtils.getAgentUidFromThreadTopic(thread.getTopic());
                return agentRestService.findByUid(agentUid)
                        .map(this::resolveLeaveMessageTip)
                        .orElse(I18Consts.I18N_MESSAGE_LEAVE_TIP);
            } else if (thread.isWorkgroupType()) {
                String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(thread.getTopic());
                return workgroupRestService.findByUid(workgroupUid)
                        .map(this::resolveLeaveMessageTip)
                        .orElse(I18Consts.I18N_MESSAGE_LEAVE_TIP);
            }
        } catch (Exception e) {
            log.debug("Failed to resolve leave message tip for thread {}: {}", thread.getUid(), e.getMessage());
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
    }

    private String resolveLeaveMessageTip(AgentEntity agent) {
        if (agent.getSettings() != null
                && agent.getSettings().getMessageLeaveSettings() != null
                && StringUtils.hasText(agent.getSettings().getMessageLeaveSettings().getMessageLeaveTip())) {
            return agent.getSettings().getMessageLeaveSettings().getMessageLeaveTip();
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
    }

    private String resolveLeaveMessageTip(WorkgroupEntity workgroup) {
        if (workgroup.getSettings() != null
                && workgroup.getSettings().getMessageLeaveSettings() != null
                && StringUtils.hasText(workgroup.getSettings().getMessageLeaveSettings().getMessageLeaveTip())) {
            return workgroup.getSettings().getMessageLeaveSettings().getMessageLeaveTip();
        }
        return I18Consts.I18N_MESSAGE_LEAVE_TIP;
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
