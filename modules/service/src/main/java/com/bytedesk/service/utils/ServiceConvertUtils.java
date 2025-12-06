/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-04 11:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 11:55:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.utils;

import org.modelmapper.ModelMapper;
import lombok.experimental.UtilityClass;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.settings.BaseSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsEntity;
import com.bytedesk.kbase.settings.ServiceSettingsResponseVisitor;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.core.socket.connection.ConnectionRestService;
import com.bytedesk.service.message_leave.MessageLeaveEntity;
import com.bytedesk.service.message_leave.MessageLeaveResponse;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueResponse;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberResponse;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorProtobuf;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupResponse;
import com.bytedesk.service.presence.PresenceFacadeService;

@UtilityClass
public class ServiceConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static VisitorResponse convertToVisitorResponse(VisitorEntity visitor) {
        return getModelMapper().map(visitor, VisitorResponse.class);
    }

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorEntity visitor) {
        return getModelMapper().map(visitor, VisitorProtobuf.class);
    }

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorResponse visitor) {
        VisitorProtobuf userProtobuf = getModelMapper().map(visitor, VisitorProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static VisitorProtobuf convertToVisitorProtobuf(VisitorRequest visitorRequest) {
        VisitorProtobuf userProtobuf = getModelMapper().map(visitorRequest, VisitorProtobuf.class);
        userProtobuf.setType(UserTypeEnum.VISITOR.name());
        return userProtobuf;
    }

    public static UserProtobuf convertToUserProtobuf(WorkflowEntity workflow) {
        UserProtobuf userProtobuf = getModelMapper().map(workflow, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.WORKFLOW.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufString(WorkflowEntity workflow) {
        UserProtobuf userProtobuf = convertToUserProtobuf(workflow);
        return JSON.toJSONString(userProtobuf);
    }

    /**
     * 将工作流实体转换为 UserProtobuf JSON 字符串
     * 只包含基本信息（uid, nickname, avatar）和 schema，不包含 nodes/edges
     */
    public static String convertToWorkflowProtobufString(WorkflowEntity workflow) {
        if (workflow == null) {
            return null;
        }
        // 手动构建 UserProtobuf，只传递基本信息和 schema
        UserProtobuf userProtobuf = UserProtobuf.builder()
                .uid(workflow.getUid())
                .nickname(workflow.getNickname())
                .avatar(workflow.getAvatar())
                .type(UserTypeEnum.WORKFLOW.name())
                .extra(workflow.getSchema()) // 将 schema 存储在 extra 字段中
                .build();
        return JSON.toJSONString(userProtobuf);
    }

    public static String convertToVisitorProtobufJSONString(VisitorRequest visitorRequest) {
        VisitorProtobuf userProtobuf = convertToVisitorProtobuf(visitorRequest);
        return userProtobuf.toJson();
    }

    public static MessageProtobuf convertToMessageProtobuf(MessageEntity lastMessage, ThreadEntity thread) {
        //
        MessageProtobuf messageProtobuf = getModelMapper().map(lastMessage, MessageProtobuf.class);
        messageProtobuf.setThread(ConvertUtils.convertToThreadProtobuf(thread));
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageProtobuf.setUser(user);

        return messageProtobuf;
    }

    public static MessageResponse convertToMessageResponse(MessageEntity lastMessage) {
        //
        MessageResponse messageResponse = getModelMapper().map(lastMessage, MessageResponse.class);
        //
        UserProtobuf user = JSON.parseObject(lastMessage.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }

    public static MessageResponse convertToMessageResponse(MessageUnreadEntity message) {
        MessageResponse messageResponse = getModelMapper().map(message, MessageResponse.class);

        UserProtobuf user = JSON.parseObject(message.getUser(), UserProtobuf.class);
        if (user.getExtra() == null) {
            user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
        }
        messageResponse.setUser(user);

        return messageResponse;
    }
    
    //
    public static AgentResponse convertToAgentResponse(AgentEntity agent) {
        AgentResponse resp = getModelMapper().map(agent, AgentResponse.class);
        try {
            ConnectionRestService presence = ApplicationContextHolder.getBean(ConnectionRestService.class);
            boolean online = presence.isUserOnline(agent.getUserUid());
            resp.setConnected(online);
        } catch (Exception ignore) {
            // ApplicationContext not ready or bean missing; set false as safe default
            resp.setConnected(false);
        }
        // 获取客服队列统计信息
        try {
            com.bytedesk.service.queue.QueueService queueService = ApplicationContextHolder.getBean(com.bytedesk.service.queue.QueueService.class);
            com.bytedesk.service.queue.AgentQueueStatsResponse queueStats = queueService.getAgentQueueStats(agent.getUid());
            resp.setQueueStats(queueStats);
        } catch (Exception ignore) {
            // QueueService not ready; skip queue stats
        }
        return resp;
    }

    public static UserProtobuf convertToUserProtobuf(AgentEntity agent) {
        UserProtobuf userProtobuf = getModelMapper().map(agent, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.AGENT.name());
        return userProtobuf;
    }

    public static UserProtobuf convertToUserProtobuf(WorkgroupEntity workgroup) {
        UserProtobuf userProtobuf = getModelMapper().map(workgroup, UserProtobuf.class);
        userProtobuf.setType(UserTypeEnum.WORKGROUP.name());
        return userProtobuf;
    }

    public static String convertToUserProtobufJSONString(WorkgroupEntity workgroup) {
        UserProtobuf userProtobuf = convertToUserProtobuf(workgroup);
        return userProtobuf.toJson();
    }

    public static String convertToUserProtobufJSONString(AgentEntity agent) {
        UserProtobuf userProtobuf = convertToUserProtobuf(agent);
        return userProtobuf.toJson();
    }

    //
    public static WorkgroupResponse convertToWorkgroupResponse(WorkgroupEntity workgroup) {
        WorkgroupResponse resp = getModelMapper().map(workgroup, WorkgroupResponse.class);
        try {
            PresenceFacadeService presenceFacadeService = ApplicationContextHolder.getBean(PresenceFacadeService.class);
            if (workgroup != null && workgroup.getAgents() != null) {
                // 使用 presence + convertToAgentResponse 构建响应坐席列表
                java.util.List<AgentResponse> agentResponses = new java.util.ArrayList<>();
                long connected = 0L;
                long available = 0L;
                long connectedAndAvailable = 0L;
                long offline = 0L;
                long busy = 0L;
                long away = 0L;
                for (AgentEntity agent : workgroup.getAgents()) {
                    if (agent == null) continue;
                    boolean isOnline = presenceFacadeService.isAgentOnline(agent);
                    boolean isAvailable = agent.isAvailable();
                    boolean isOnlineAndAvailable = presenceFacadeService.isAgentOnlineAndAvailable(agent);
                    // 
                    if (isOnline) connected++; else offline++;
                    if (isAvailable) available++;
                    if (isOnlineAndAvailable) connectedAndAvailable++;
                    if (agent.isBusy()) busy++;
                    if (agent.isAway()) away++;
                    agentResponses.add(convertToAgentResponse(agent));
                }
                resp.setAgents(agentResponses);
                // 留言坐席转换（保留 null 安全）
                if (workgroup.getMessageLeaveAgent() != null) {
                    resp.setMessageLeaveAgent(convertToAgentResponse(workgroup.getMessageLeaveAgent()));
                }
                resp.setConnectedAgentCount(connected)
                    .setAvailableAgentCount(available)
                    .setConnectedAndAvailableAgentCount(connectedAndAvailable)
                    .setOfflineAgentCount(offline)
                    .setBusyAgentCount(busy)
                    .setAwayAgentCount(away);
            } else {
                resp.setConnectedAgentCount(0L)
                    .setAvailableAgentCount(0L)
                    .setOfflineAgentCount(0L)
                    .setBusyAgentCount(0L)
                    .setAwayAgentCount(0L)
                    .setAgents(new java.util.ArrayList<>())
                    .setMessageLeaveAgent(null);
            }
        } catch (Exception ignore) {
            // 如果 PresenceFacadeService 不可用，保持已有映射（可能在早期启动阶段）
        }
        return resp;
    }
    
    /**
     * Build ServiceSettingsResponseVisitor object from settings container.
     * If debug=true and draft exists, prefer draft; otherwise use published.
     * Falls back to a new ServiceSettingsEntity when missing.
     */
    public static ServiceSettingsResponseVisitor buildServiceSettingsResponseVisitor(BaseSettingsEntity settingsContainer, boolean debug) {
        ServiceSettingsEntity svc = null;
        if (settingsContainer != null) {
            if (debug && settingsContainer.getDraftServiceSettings() != null) {
                svc = settingsContainer.getDraftServiceSettings();
            } else if (settingsContainer.getServiceSettings() != null) {
                svc = settingsContainer.getServiceSettings();
            }
        }
        if (svc == null) {
            svc = ServiceSettingsEntity.builder().build();
        }
        return getModelMapper().map(svc, ServiceSettingsResponseVisitor.class);
    }

    /**
     * Build ServiceSettingsResponseVisitor JSON from settings container.
     */
    public static String convertToServiceSettingsResponseVisitorJSONString(BaseSettingsEntity settingsContainer, boolean debug) {
        ServiceSettingsResponseVisitor resp = buildServiceSettingsResponseVisitor(settingsContainer, debug);
        return JSON.toJSONString(resp);
    }

    public static QueueResponse convertToQueueResponse(QueueEntity entity) {
        return getModelMapper().map(entity, QueueResponse.class);
    }

    public static MessageLeaveResponse convertToMessageLeaveResponse(MessageLeaveEntity entity) {
        MessageLeaveResponse messageLeaveResponse = getModelMapper().map(entity, MessageLeaveResponse.class);
        
        // 设置用户信息
        if (entity.getUser() != null) {
            messageLeaveResponse.setUser(UserProtobuf.fromJson(entity.getUser()));
        }
        
        // 设置回复用户信息
        if (entity.getReplyUser() != null) {
            messageLeaveResponse.setReplyUser(UserProtobuf.fromJson(entity.getReplyUser()));
        }
        
        // 设置已读用户信息
        if (entity.getReadUser() != null) {
            messageLeaveResponse.setReadUser(UserProtobuf.fromJson(entity.getReadUser()));
        }
        
        // 设置转接用户信息
        if (entity.getTransferUser() != null) {
            messageLeaveResponse.setTransferUser(UserProtobuf.fromJson(entity.getTransferUser()));
        }
        
        // 设置关闭用户信息
        if (entity.getCloseUser() != null) {
            messageLeaveResponse.setCloseUser(UserProtobuf.fromJson(entity.getCloseUser()));
        }
        
        // 设置标记垃圾留言用户信息
        if (entity.getSpamUser() != null) {
            messageLeaveResponse.setSpamUser(UserProtobuf.fromJson(entity.getSpamUser()));
        }
        
        // 设置确认用户信息
        if (entity.getConfirmUser() != null) {
            messageLeaveResponse.setConfirmUser(UserProtobuf.fromJson(entity.getConfirmUser()));
        }
        
        // 设置拒绝用户信息
        if (entity.getRejectUser() != null) {
            messageLeaveResponse.setRejectUser(UserProtobuf.fromJson(entity.getRejectUser()));
        }
        
        return messageLeaveResponse;
    }

    public static QueueMemberResponse convertToQueueMemberResponse(QueueMemberEntity entity) {
        // 
        QueueMemberResponse response = getModelMapper().map(entity, QueueMemberResponse.class);
        response.setThread(ConvertUtils.convertToThreadResponse(entity.getThread()));
        response.setStatus(entity.getStatus());
        
        // 设置首次响应时长
        response.setAgentFirstResponseLength(entity.getAgentFirstResponseLength());
        
        if (entity.getThread() != null && entity.getThread().getType() != null) {
            // 处理不同类型的队列
            if (entity.getThread().getType().equals(ThreadTypeEnum.AGENT.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getAgentQueue()));
            } else if (entity.getThread().getType().equals(ThreadTypeEnum.ROBOT.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getRobotQueue()));
            } else if (entity.getThread().getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
                response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getWorkgroupQueue()));
            }
        }
        return response;
        // 手动创建响应对象，避免 ModelMapper 的映射冲突
        // QueueMemberResponse response = new QueueMemberResponse();
        
        // // 复制基本属性（从 BaseEntity 继承的属性）
        // response.setUid(entity.getUid());
        // response.setPlatform(entity.getPlatform());
        // response.setLevel(entity.getLevel());
        // response.setCreatedAt(entity.getCreatedAt());
        // response.setUpdatedAt(entity.getUpdatedAt());
        
        // // 复制 QueueMemberEntity 特有的属性
        // response.setQueueNumber(entity.getQueueNumber());
        // response.setWaitLength((int) entity.getWaitLength());
        // response.setVisitorEnqueueAt(entity.getVisitorEnqueueAt());
        // response.setVisitorFirstMessageAt(entity.getVisitorFirstMessageAt());
        // response.setVisitorLastMessageAt(entity.getVisitorLastMessageAt());
        // response.setVisitorMessageCount(entity.getVisitorMessageCount());
        // response.setVisitorLeavedAt(entity.getVisitorLeavedAt());
        // response.setVisitorPriority(entity.getVisitorPriority());
        // response.setAgentAcceptType(entity.getAgentAcceptType());
        // response.setAgentAcceptedAt(entity.getAgentAcceptedAt());
        // response.setAgentFirstResponse(entity.getAgentFirstResponse());
        // response.setAgentFirstResponseAt(entity.getAgentFirstResponseAt());
        // response.setAgentLastResponseAt(entity.getAgentLastResponseAt());
        // response.setAgentClosedAt(entity.getAgentClosedAt());
        // response.setAgentClose(entity.getAgentClose());
        // response.setAgentAvgResponseLength(entity.getAgentAvgResponseLength());
        // response.setAgentMaxResponseLength(entity.getAgentMaxResponseLength());
        // response.setAgentMessageCount(entity.getAgentMessageCount());
        // response.setAgentTimeoutAt(entity.getAgentTimeoutAt());
        // response.setAgentTimeout(entity.getAgentTimeout());
        // response.setAgentTimeoutCount(entity.getAgentTimeoutCount());
        // response.setAgentOffline(entity.getAgentOffline());
        // response.setRobotAcceptType(entity.getRobotAcceptType());
        // response.setRobotAcceptedAt(entity.getRobotAcceptedAt());
        // response.setRobotFirstResponse(entity.getRobotFirstResponse());
        // response.setRobotFirstResponseAt(entity.getRobotFirstResponseAt());
        // response.setRobotLastResponseAt(entity.getRobotLastResponseAt());
        // response.setRobotClosedAt(entity.getRobotClosedAt());
        // response.setRobotAvgResponseLength(entity.getRobotAvgResponseLength());
        // response.setRobotMaxResponseLength(entity.getRobotMaxResponseLength());
        // response.setRobotMessageCount(entity.getRobotMessageCount());
        // response.setRobotTimeoutAt(entity.getRobotTimeoutAt());
        // response.setRobotTimeout(entity.getRobotTimeout());
        // response.setSystemFirstResponseAt(entity.getSystemFirstResponseAt());
        // response.setSystemLastResponseAt(entity.getSystemLastResponseAt());
        // response.setSystemClosedAt(entity.getSystemClosedAt());
        // response.setSystemClose(entity.getSystemClose());
        // response.setSystemMessageCount(entity.getSystemMessageCount());
        // response.setRated(entity.getRated());
        // response.setRateScore(entity.getRateScore());
        // response.setRateAt(entity.getRateAt());
        // response.setResolved(entity.getResolved());
        // response.setMessageLeave(entity.getMessageLeave());
        // response.setMessageLeaveAt(entity.getMessageLeaveAt());
        // response.setSummarized(entity.getSummarized());
        // response.setResolvedStatus(entity.getResolvedStatus());
        // response.setQualityChecked(entity.getQualityChecked());
        // response.setQualityCheckScore(entity.getQualityCheckScore());
        // response.setQualityCheckedAt(entity.getQualityCheckedAt());
        // response.setIntentionType(entity.getIntentionType());
        // response.setEmotionType(entity.getEmotionType());
        // response.setRobotToAgent(entity.getRobotToAgent());
        // response.setRobotToAgentAt(entity.getRobotToAgentAt());
        // response.setTransferStatus(entity.getTransferStatus());
        // response.setInviteStatus(entity.getInviteStatus());
        
        // // 设置线程信息
        // response.setThread(ConvertUtils.convertToThreadResponse(entity.getThread()));
        
        // // 根据线程类型设置对应的队列
        // if (entity.getThread() != null && entity.getThread().getType() != null) {
        //     if (entity.getThread().getType().equals(ThreadTypeEnum.AGENT.name())) {
        //         response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getAgentQueue()));
        //     } else if (entity.getThread().getType().equals(ThreadTypeEnum.ROBOT.name())) {
        //         response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getRobotQueue()));
        //     } else if (entity.getThread().getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
        //         response.setQueue(ServiceConvertUtils.convertToQueueResponse(entity.getWorkgroupQueue()));
        //     }
        // }
        
        // return response;
    }
    
    /**
     * 将Boolean转换为"是"或"否"
     */
    public static String booleanToString(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "是" : "否";
    }

}
