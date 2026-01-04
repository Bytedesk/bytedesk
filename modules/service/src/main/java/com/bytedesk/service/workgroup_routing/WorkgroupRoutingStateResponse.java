package com.bytedesk.service.workgroup_routing;

import java.util.List;

import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作组路由状态（用于前端可视化）。
 * - routingMode: 当前路由模式
 * - nextAgentUid/nextAgent: 预计算出的下一个将被分配的客服
 * - availableAgents: 当前可用客服列表（在线且可接待）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkgroupRoutingStateResponse {

    private String workgroupUid;

    private String routingMode;

    private String nextAgentUid;

    private UserProtobuf nextAgent;

    private List<UserProtobuf> availableAgents;
}
