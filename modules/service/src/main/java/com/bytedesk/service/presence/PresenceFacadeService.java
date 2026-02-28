package com.bytedesk.service.presence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bytedesk.core.socket.connection.ConnectionRestService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentStatusEnum;
import com.bytedesk.service.visitor.VisitorCallTypeEnum;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PresenceFacadeService
 * 统一封装坐席/工作组在线状态判断逻辑，逐步替换旧的 AgentEntity.connected 布尔字段。
 * 逻辑来源：ConnectionEntity 多客户端长连接会话记录 + 接待状态。
 */
@Slf4j
@Service
@AllArgsConstructor
public class PresenceFacadeService {

    private final ConnectionRestService connectionRestService;

    /** 判断坐席是否至少有一个有效在线连接 */
    public boolean isAgentOnline(AgentEntity agent) {
        if (agent == null || agent.getUserUid() == null) {
            return false;
        }
        boolean online = false;
        try {
            online = connectionRestService.isUserOnline(agent.getUserUid());
        } catch (Exception e) {
            log.debug("presence check failed for agent {}: {}", agent != null ? agent.getUid() : "null", e.getMessage());
        }
        return online;
    }

    /** 坐席是否在线且当前接待状态可用 */
    public boolean isAgentOnlineAndAvailable(AgentEntity agent) {
        return isAgentOnline(agent) && agent != null && agent.isAvailable();
    }

    /** 坐席是否在线且可接待指定 callType */
    public boolean isAgentOnlineAndAvailableForCallType(AgentEntity agent, VisitorCallTypeEnum callType) {
        if (!isAgentOnline(agent) || agent == null) {
            return false;
        }
        AgentStatusEnum status;
        try {
            status = AgentStatusEnum.fromValue(agent.getStatus());
        } catch (Exception e) {
            return false;
        }

        VisitorCallTypeEnum resolvedCallType = callType == null ? VisitorCallTypeEnum.TEXT : callType;
        if (status == AgentStatusEnum.AVAILABLE) {
            return true;
        }
        return switch (resolvedCallType) {
            case AUDIO -> status == AgentStatusEnum.AVAILABLE_AUDIO;
            case VIDEO -> status == AgentStatusEnum.AVAILABLE_VIDEO;
            case PHONE -> status == AgentStatusEnum.AVAILABLE_PHONE;
            case TEXT -> false;
        };
    }

    /** 计算工作组是否有任意在线坐席 */
    public boolean isWorkgroupOnline(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getAgents() == null || workgroup.getAgents().isEmpty()) {
            return false;
        }
        for (AgentEntity agent : workgroup.getAgents()) {
            if (isAgentOnline(agent)) {
                return true;
            }
        }
        return false;
    }

    /** 获取在线且可用的坐席列表（替换 WorkgroupEntity#getAvailableAgents 旧实现） */
    public List<AgentEntity> getAvailableAgents(WorkgroupEntity workgroup) {
        List<AgentEntity> result = new ArrayList<>();
        if (workgroup == null || workgroup.getAgents() == null) {
            return result;
        }
        for (AgentEntity agent : workgroup.getAgents()) {
            if (isAgentOnlineAndAvailable(agent)) {
                result.add(agent);
            }
        }
        return result;
    }

    /** 获取在线且可接待指定 callType 的坐席列表 */
    public List<AgentEntity> getAvailableAgentsForCallType(WorkgroupEntity workgroup, VisitorCallTypeEnum callType) {
        List<AgentEntity> result = new ArrayList<>();
        if (workgroup == null || workgroup.getAgents() == null) {
            return result;
        }
        for (AgentEntity agent : workgroup.getAgents()) {
            if (isAgentOnlineAndAvailableForCallType(agent, callType)) {
                result.add(agent);
            }
        }
        return result;
    }

    /** 在线坐席数量（统计用途） */
    public long countOnlineAgents(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getAgents() == null) {
            return 0L;
        }
        return workgroup.getAgents().stream().filter(this::isAgentOnline).count();
    }
}
