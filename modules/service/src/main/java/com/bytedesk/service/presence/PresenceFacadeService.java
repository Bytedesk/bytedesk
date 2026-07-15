package com.bytedesk.service.presence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bytedesk.core.socket.connection.ConnectionRestService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentStatusEnum;
import com.bytedesk.core.enums.VisitorCallTypeEnum;
import com.bytedesk.service.workgroup.WorkgroupEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PresenceFacadeService
 * 统一封装坐席/工作组可接待状态判断逻辑，逐步替换旧的 AgentEntity.connected 布尔字段。
 * 规则基于：ConnectionEntity 多客户端长连接会话记录 + 坐席接待状态。
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

    /** 坐席是否满足可接待条件：长连接正常且当前状态可用 */
    public boolean isAgentOnlineAndAvailable(AgentEntity agent) {
        return isAgentOnline(agent) && agent != null && agent.isAvailable();
    }

    /** 坐席是否满足指定 callType 的可接待条件 */
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

        // VisitorCallTypeEnum resolvedCallType = callType == null ? VisitorCallTypeEnum.TEXT : callType;
        if (status == AgentStatusEnum.AVAILABLE) {
            return true;
        }
        return false;
        // return switch (resolvedCallType) {
        //     case WEBRTC -> status == AgentStatusEnum.AVAILABLE;
        //     case PHONE -> status == AgentStatusEnum.AVAILABLE;//AgentStatusEnum.AVAILABLE_PHONE;
        //     case TEXT -> false;
        // };
    }

    /**
     * 计算工作组是否存在可接待坐席。
     * 规则：长连接正常，且坐席状态为 AVAILABLE，二者必须同时满足。
     */
    public boolean hasAvailableAgents(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getAgents() == null || workgroup.getAgents().isEmpty()) {
            return false;
        }
        for (AgentEntity agent : workgroup.getAgents()) {
            if (isAgentOnlineAndAvailable(agent)) {
                return true;
            }
        }
        return false;
    }

    /** 获取满足可接待条件的坐席列表（替换 WorkgroupEntity#getAvailableAgents 旧实现） */
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

    /** 获取满足指定 callType 可接待条件的坐席列表 */
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

    /**
     * 可接待坐席数量（统计用途）。
     * 规则：长连接正常，且坐席状态为 AVAILABLE。
     */
    public long countAvailableAgents(WorkgroupEntity workgroup) {
        if (workgroup == null || workgroup.getAgents() == null) {
            return 0L;
        }
        return workgroup.getAgents().stream().filter(this::isAgentOnlineAndAvailable).count();
    }
}
