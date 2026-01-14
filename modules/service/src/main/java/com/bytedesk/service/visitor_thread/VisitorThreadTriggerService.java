package com.bytedesk.service.visitor_thread;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.thread.ActiveThreadCache;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.kbase.settings_trigger.TriggerSettingsEntity;
import com.bytedesk.kbase.trigger.TriggerEntity;
import com.bytedesk.kbase.trigger.TriggerKeyConsts;
import com.bytedesk.kbase.trigger.config.TriggerConfigRegistry;
import com.bytedesk.kbase.trigger.config.VisitorNoResponseProactiveMessageConfig;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorThreadTriggerService {

    private final VisitorThreadService visitorThreadService;

    private final WorkgroupRestService workgroupRestService;

    private final AgentRestService agentRestService;

    private final RobotRestService robotRestService;

    private final IMessageSendService messageSendService;

    private final ThreadRestService threadRestService;

    public void processProactiveTrigger(ThreadEntity thread) {
        handleProactiveTrigger(thread);
    }

    public void processProactiveTriggerFromCache(ActiveThreadCache cache) {
        handleProactiveTriggerFromCache(cache);
    }

    /**
     * 处理主动触发消息
     */
    private void handleProactiveTrigger(ThreadEntity thread) {
        String topic = thread.getTopic();

        if (ThreadTypeEnum.WORKGROUP.name().equals(thread.getType())) {
            handleWorkgroupProactiveTrigger(thread, topic);
        } else if (ThreadTypeEnum.AGENT.name().equals(thread.getType())) {
            handleAgentProactiveTrigger(thread, topic);
        } else if (ThreadTypeEnum.ROBOT.name().equals(thread.getType())) {
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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(cache.getUid(), config.timeoutSeconds())) {
                    return;
                }

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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(),
                        config.timeoutSeconds())) {
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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(),
                        config.timeoutSeconds())) {
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
                Optional<NoResponseProactiveTriggerConfig> configOpt = resolveNoResponseProactiveTriggerConfig(
                        triggerSettings);
                if (configOpt.isEmpty()) {
                    return;
                }
                NoResponseProactiveTriggerConfig config = configOpt.get();

                if (!visitorThreadService.consumeVisitorNoResponseTriggerPermit(thread.getUid(),
                        config.timeoutSeconds())) {
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
