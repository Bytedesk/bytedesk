package com.bytedesk.core.message.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访客侧的“路由池等待”结构化内容。
 *
 * <p>
 * 该消息用于 routingMode=MANUAL 时：会话进入路由池等待客服手动接入。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutingPoolContent {

    /**
     * 展示文本（可选）
     */
    private String content;

    /**
     * 路由池条目 uid
     */
    private String routingPoolUid;

    /**
     * 关联 thread uid
     */
    private String threadUid;

    /**
     * 服务器时间戳（ms）
     */
    private Long serverTimestamp;

    public String toJson() {
        return com.alibaba.fastjson2.JSON.toJSONString(this);
    }
}
