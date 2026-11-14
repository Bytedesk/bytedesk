package com.bytedesk.service.routing_strategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.VisitorCallTypeEnum;
import com.bytedesk.core.tag.TagEntity;
import com.bytedesk.core.tag.TagRepository;
import com.bytedesk.core.tag.TagTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.agent.AgentCapacityService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup_routing.WorkgroupRoutingModeEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkgroupTagRoutingService {

    private final TagRepository tagRepository;
    private final PresenceFacadeService presenceFacadeService;
    private final AgentCapacityService agentCapacityService;

    public Optional<AgentEntity> selectAgent(ThreadEntity thread, WorkgroupEntity workgroup,
            VisitorCallTypeEnum callType) {
        if (callType != VisitorCallTypeEnum.TEXT || Boolean.TRUE.equals(thread.getTagRoutingEvaluated())
                || thread.getTagRoutingEvaluated() == null || thread.isChatting() || thread.isClosed()
                || workgroup.getSettings() == null
                || !Boolean.TRUE.equals(workgroup.getSettings().getTagRoutingEnabled())
                || WorkgroupRoutingModeEnum.MANUAL.name().equalsIgnoreCase(workgroup.getRoutingMode())) {
            return Optional.empty();
        }
        List<String> tags = thread.getRoutingTagSnapshot();
        if (tags == null || tags.isEmpty()) {
            return Optional.empty();
        }
        // 绑定关系存储在共享设置模板：所有引用该模板的工作组共享同一份绑定
        Map<String, String> bindings = workgroup.getSettings().getTagAgentBindings();
        if (bindings == null || bindings.isEmpty()) {
            return Optional.empty();
        }
        for (String uid : tags) {
            String agentUid = bindings.get(uid);
            if (!StringUtils.hasText(agentUid)) {
                continue;
            }
            Optional<TagEntity> tag = tagRepository.findByUidAndOrgUidAndTypeAndDeletedFalse(
                    uid, workgroup.getOrgUid(), TagTypeEnum.ROUTE.name());
            if (tag.isEmpty()) {
                continue;
            }
            for (AgentEntity agent : workgroup.getAgents()) {
                if (agentUid.equals(agent.getUid()) && workgroup.getOrgUid().equals(agent.getOrgUid())
                        && !agent.isDeleted()
                        && presenceFacadeService.isAgentOnlineAndAvailableForCallType(agent, callType)
                        && agentCapacityService.hasCapacity(agent)) {
                    return Optional.of(agent);
                }
            }
        }
        return Optional.empty();
    }
}
