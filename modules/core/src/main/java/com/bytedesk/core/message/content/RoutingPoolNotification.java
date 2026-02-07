package com.bytedesk.core.message.content;

import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服侧的“路由池通知”结构化 payload。
 *
 * <p>
 * 当 routingMode=MANUAL 时，系统将访客咨询写入 routing_pool，并通知工作组内所有在线可用客服。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutingPoolNotification {

    private String routingPoolUid;
    private String threadUid;
    private String threadTopic;
    private String workgroupUid;

    /**
     * 排队/等待开始时间（ms）
     */
    private Long serverTimestamp;

    /**
     * 访客信息
     */
    private UserProtobuf user;

    public String toJson() {
        return com.alibaba.fastjson2.JSON.toJSONString(this);
    }
}
