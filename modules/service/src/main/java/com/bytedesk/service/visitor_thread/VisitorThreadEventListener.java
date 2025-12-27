/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-01 13:47:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.thread.ActiveThreadCache;
import com.bytedesk.core.thread.ActiveThreadCacheService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsEntity;
import com.bytedesk.kbase.trigger.TriggerEntity;
import com.bytedesk.kbase.trigger.TriggerKeyConsts;
import com.bytedesk.kbase.trigger.config.TriggerConfigRegistry;
import com.bytedesk.kbase.trigger.config.VisitorNoResponseProactiveMessageConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorThreadEventListener {

    private final VisitorThreadService visitorThreadService;

    private final WorkgroupRestService workgroupRestService;

    private final AgentRestService agentRestService;

    private final RobotRestService robotRestService;

    private final IMessageSendService messageSendService;

    private final ThreadRestService threadRestService;

    private final QueueMemberRestService queueMemberRestService;

    private final ActiveThreadCacheService activeThreadCacheService;

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("visitor onThreadCloseEvent: {}", thread.getUid());

        // 使用closeType替代autoClose
        String closeType = thread.getCloseType();
        boolean autoClose = ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType);

        // 更新队列成员状态
        updateQueueMemberOnClose(thread, autoClose);

        // 如果是自动关闭且客服离线，不发送消息
        if (autoClose && isAgentOffline(thread)) {
            return;
        }

        // 获取关闭提示语
        String content = getCloseTip(thread, closeType);

        // 发送消息
        MessageProtobuf messageProtobuf = autoClose
                ? MessageUtils.createAutoCloseMessage(thread, content)
                : MessageUtils.createAgentCloseMessage(thread, content);
        messageSendService.sendProtobufMessage(messageProtobuf);
    }

    /**
     * 优化：从 Redis 缓存查询活跃会话，避免频繁查询数据库
     * 缓存在会话创建时写入，会话关闭时移除
     */
    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);        
        // 从缓存获取活跃会话列表
        List<ActiveThreadCache> cachedThreads = activeThreadCacheService.getAllActiveServiceThreads();
        
        if (cachedThreads.isEmpty()) {
            // 缓存为空时，从数据库加载并重建缓存（仅在系统启动或缓存失效时发生）
            List<ThreadEntity> dbThreads = threadRestService.findServiceThreadStateStarted();
            if (!dbThreads.isEmpty()) {
                log.info("Rebuilding active thread cache from database, count: {}", dbThreads.size());
                activeThreadCacheService.rebuildCacheFromDatabase(dbThreads);
                // 使用数据库数据处理
                visitorThreadService.autoRemindAgentOrCloseThread(dbThreads);
                dbThreads.forEach(this::processProactiveTrigger);
            }
            return;
        }
        
        // 从缓存获取完整的 ThreadEntity 列表用于处理（需要完整的 ThreadEntity 信息）
        List<ThreadEntity> threads = cachedThreads.stream()
                .map(cache -> threadRestService.findByUid(cache.getUid()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(thread -> !thread.isClosed()) // 再次过滤确保未关闭
                .toList();

        // 自动关闭线程
        visitorThreadService.autoRemindAgentOrCloseThread(threads);

        // 处理主动触发逻辑（使用缓存数据进行时间判断，减少数据库查询）
        cachedThreads.forEach(this::processProactiveTriggerFromCache);
    }

    /**
     * 使用缓存数据处理主动触发逻辑
     */
    private void processProactiveTriggerFromCache(ActiveThreadCache cache) {
        // 主动触发改为使用 QueueMember.visitorLastMessageAt 判断，避免被客服/系统消息刷新 updatedAt 干扰
        handleProactiveTriggerFromCache(cache);
    }

    /**
     * 处理单个线程的主动触发逻辑
     */
    private void processProactiveTrigger(ThreadEntity thread) {
        handleProactiveTrigger(thread);
    }

    /**
     * 更新队列成员状态
     */
    private void updateQueueMemberOnClose(ThreadEntity thread, boolean autoClose) {
        Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        if (queueMemberOptional.isPresent()) {
            QueueMemberEntity queueMember = queueMemberOptional.get();
            if (autoClose) {
                queueMember.setSystemClosedAt(BdDateUtils.now());
                queueMember.setSystemClose(true);
            } else {
                queueMember.setAgentClosedAt(BdDateUtils.now());
                queueMember.setAgentClose(true);
            }
            queueMemberRestService.saveAsyncBestEffort(queueMember);
        }
    }

    /**
     * 检查客服是否离线
     */
    private boolean isAgentOffline(ThreadEntity thread) {
        Optional<QueueMemberEntity> queueMemberOptional = queueMemberRestService.findByThreadUid(thread.getUid());
        return queueMemberOptional.isPresent() && queueMemberOptional.get().getAgentOffline();
    }

    /**
     * 获取关闭提示语
     */
    private String getCloseTip(ThreadEntity thread, String closeType) {
        String topic = thread.getTopic();
        boolean autoClose = ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType);
        if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            return getWorkgroupCloseTip(topic, autoClose, closeType);
        } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            return getAgentCloseTip(topic, autoClose, closeType);
        } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
            return getRobotCloseTip(topic);
        }
        return resolveGenericCloseTip(closeType);
    }

    /**
     * 获取工作组关闭提示语
     */
    private String getWorkgroupCloseTip(String topic, boolean autoClose, String closeType) {
        String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);

        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            if (workgroup.getSettings() != null && workgroup.getSettings().getServiceSettings() != null) {
                if (ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
                    return workgroup.getSettings().getServiceSettings().getAgentCloseTip();
                }
                return autoClose ? workgroup.getSettings().getServiceSettings().getAutoCloseTip()
                        : workgroup.getSettings().getServiceSettings().getAgentCloseTip();
            }
        }
        return resolveGenericCloseTip(closeType);
    }

    /**
     * 获取客服关闭提示语
     */
    private String getAgentCloseTip(String topic, boolean autoClose, String closeType) {
        String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);

        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            if (agent.getSettings() != null && agent.getSettings().getServiceSettings() != null) {
                if (ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
                    return agent.getSettings().getServiceSettings().getAgentCloseTip();
                }
                return autoClose ? agent.getSettings().getServiceSettings().getAutoCloseTip()
                        : agent.getSettings().getServiceSettings().getAgentCloseTip();
            }
        }
        return resolveGenericCloseTip(closeType);
    }

    /**
     * 获取机器人关闭提示语
     */
    private String getRobotCloseTip(String topic) {
        String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);

        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            if (robot.getSettings() != null && robot.getSettings().getServiceSettings() != null) {
                return robot.getSettings().getServiceSettings().getAutoCloseTip();
            }
        }

        return I18Consts.I18N_AUTO_CLOSE_TIP;
    }

    private String resolveGenericCloseTip(String closeType) {
        if (ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
            return I18Consts.I18N_AUTO_CLOSE_TIP;
        } else if (ThreadCloseTypeEnum.AGENT.name().equalsIgnoreCase(closeType)) {
            return I18Consts.I18N_AGENT_CLOSE_TIP;
        } else if (ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
            return I18Consts.I18N_AGENT_CLOSE_TIP; // 访客关闭复用客服关闭提示
        } else if (ThreadCloseTypeEnum.SYSTEM.name().equalsIgnoreCase(closeType)) {
            return I18Consts.I18N_AGENT_CLOSE_TIP;
        }
        return "会话已结束";
    }

    /**
     * 处理主动触发消息
     */
    private void handleProactiveTrigger(ThreadEntity thread) {
        String topic = thread.getTopic();

        if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            handleWorkgroupProactiveTrigger(thread, topic);
        } else if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            handleAgentProactiveTrigger(thread, topic);
        } else if (thread.getType().equals(ThreadTypeEnum.ROBOT.name())) {
            handleRobotProactiveTrigger(thread, topic);
        }
    }

    /**
     * 使用缓存数据处理主动触发消息
     * 根据类型从缓存中判断是否需要触发，仅在需要时才查询数据库获取完整信息
     */
    private void handleProactiveTriggerFromCache(ActiveThreadCache cache) {
        String topic = cache.getTopic();
        String type = cache.getType();

        if (ThreadTypeEnum.WORKGROUP.name().equals(type)) {
            handleWorkgroupProactiveTriggerFromCache(cache, topic);
        } else if (ThreadTypeEnum.AGENT.name().equals(type)) {
            handleAgentProactiveTriggerFromCache(cache, topic);
        } else if (ThreadTypeEnum.ROBOT.name().equals(type)) {
            handleRobotProactiveTriggerFromCache(cache, topic);
        }
    }

    /**
     * 处理工作组主动触发（使用缓存）
     */
    private void handleWorkgroupProactiveTriggerFromCache(ActiveThreadCache cache, String topic) {
        String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);

        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            if (workgroup.getSettings() != null && workgroup.getSettings().getTriggerSettings() != null) {
                var triggerSettings = workgroup.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

                // 需要发送消息时才从数据库获取完整的 ThreadEntity
                threadRestService.findByUid(cache.getUid()).ifPresent(thread -> {
                    log.info("visitor_thread quartz one min event thread: {} trigger workgroup proactive message",
                            thread.getUid());
                    String message = config.message();
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                    messageSendService.sendProtobufMessage(messageProtobuf);
                });
            }
        }
    }

    /**
     * 处理客服主动触发（使用缓存）
     */
    private void handleAgentProactiveTriggerFromCache(ActiveThreadCache cache, String topic) {
        String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);

        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            if (agent.getSettings() != null && agent.getSettings().getTriggerSettings() != null) {
                var triggerSettings = agent.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

                // 需要发送消息时才从数据库获取完整的 ThreadEntity
                threadRestService.findByUid(cache.getUid()).ifPresent(thread -> {
                    log.info("visitor_thread quartz one min event thread: {} trigger agent proactive message",
                            thread.getUid());
                    String message = config.message();
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                    messageSendService.sendProtobufMessage(messageProtobuf);
                });
            }
        }
    }

    /**
     * 处理机器人主动触发（使用缓存）
     */
    private void handleRobotProactiveTriggerFromCache(ActiveThreadCache cache, String topic) {
        String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);

        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            if (robot.getSettings() != null && robot.getSettings().getTriggerSettings() != null) {
                var triggerSettings = robot.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

                // 需要发送消息时才从数据库获取完整的 ThreadEntity
                threadRestService.findByUid(cache.getUid()).ifPresent(thread -> {
                    log.info("visitor_thread quartz one min event thread: {} trigger robot proactive message",
                            thread.getUid());
                    String message = config.message();
                    MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                    messageSendService.sendProtobufMessage(messageProtobuf);
                });
            }
        }
    }

    /**
     * 处理工作组主动触发
     */
    private void handleWorkgroupProactiveTrigger(ThreadEntity thread, String topic) {
        String workgroupUid = TopicUtils.getWorkgroupUidFromThreadTopic(topic);
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(workgroupUid);

        if (workgroupOptional.isPresent()) {
            WorkgroupEntity workgroup = workgroupOptional.get();
            if (workgroup.getSettings() != null && workgroup.getSettings().getTriggerSettings() != null) {
                var triggerSettings = workgroup.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(), config.timeoutSeconds())) {
                    return;
                }

                log.info("visitor_thread quartz one min event thread: {} trigger workgroup proactive message",
                        thread.getUid());
                String message = config.message();
                MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
        }
    }

    /**
     * 处理客服主动触发
     */
    private void handleAgentProactiveTrigger(ThreadEntity thread, String topic) {
        String agentUid = TopicUtils.getAgentUidFromThreadTopic(topic);
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);

        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            if (agent.getSettings() != null && agent.getSettings().getTriggerSettings() != null) {
                var triggerSettings = agent.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(), config.timeoutSeconds())) {
                    return;
                }

                log.info("visitor_thread quartz one min event thread: {} trigger agent proactive message",
                        thread.getUid());
                String message = config.message();
                MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
        }
    }

    /**
     * 处理机器人主动触发
     */
    private void handleRobotProactiveTrigger(ThreadEntity thread, String topic) {
        String robotUid = TopicUtils.getRobotUidFromThreadTopic(topic);
        Optional<RobotEntity> robotOptional = robotRestService.findByUid(robotUid);

        if (robotOptional.isPresent()) {
            RobotEntity robot = robotOptional.get();
            if (robot.getSettings() != null && robot.getSettings().getTriggerSettings() != null) {
                var triggerSettings = robot.getSettings().getTriggerSettings();
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(), config.timeoutSeconds())) {
                    return;
                }

                log.info("visitor_thread quartz one min event thread: {} trigger robot proactive message",
                        thread.getUid());
                String message = config.message();
                MessageProtobuf messageProtobuf = MessageUtils.createSystemMessage(thread, message);
                messageSendService.sendProtobufMessage(messageProtobuf);
            }
        }
    }

    private record NoResponseProactiveTriggerConfig(int timeoutSeconds, String message) {
    }

    private Optional<NoResponseProactiveTriggerConfig> resolveNoResponseProactiveTriggerConfig(
            TriggerSettingsEntity triggerSettings) {
        if (triggerSettings == null || triggerSettings.getTriggers() == null || triggerSettings.getTriggers().isEmpty()) {
            return Optional.empty();
        }

        TriggerEntity trigger = triggerSettings.getTriggers().stream()
                .filter(t -> t != null && TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE.equals(t.getTriggerKey()))
                .findFirst()
                .orElse(null);

        if (trigger == null || !Boolean.TRUE.equals(trigger.getEnabled())) {
            return Optional.empty();
        }

        int timeoutSeconds = VisitorNoResponseProactiveMessageConfig.DEFAULT_NO_RESPONSE_TIMEOUT;
        String message = VisitorNoResponseProactiveMessageConfig.DEFAULT_PROACTIVE_MESSAGE;

        if (StringUtils.hasText(trigger.getConfig())) {
            Object parsed = TriggerConfigRegistry
                    .parse(trigger.getTriggerKey(), trigger.getConfig())
                    .orElse(null);
            if (parsed instanceof VisitorNoResponseProactiveMessageConfig payload) {
                if (payload.noResponseTimeout != null && payload.noResponseTimeout > 0) {
                    timeoutSeconds = payload.noResponseTimeout;
                }
                if (StringUtils.hasText(payload.proactiveMessage)) {
                    message = payload.proactiveMessage;
                }
            }
        }

        return Optional.of(new NoResponseProactiveTriggerConfig(timeoutSeconds, message));
    }

}
