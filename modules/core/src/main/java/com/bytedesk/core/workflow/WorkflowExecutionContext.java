/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-03 10:12:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-03 10:12:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Getter;

/**
 * Lightweight context shared with workflow nodes while executing.
 */
@Getter
@Builder
public class WorkflowExecutionContext {

    private final WorkflowEntity workflow;
    private final WorkflowRequest request;
    private final JSONObject workflowJson;
    private final JSONArray nodes;
    private final JSONArray edges;

    public JSONObject findNodeById(String nodeId) {
        if (nodes == null || nodes.isEmpty() || !StringUtils.hasText(nodeId)) {
            return null;
        }
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if (nodeId.equals(node.getString("id"))) {
                return node;
            }
        }
        return null;
    }

    public String findNextNodeId(String nodeId) {
        return findNextNodeId(nodeId, null);
    }

    public String findNextNodeId(String nodeId, String sourcePortId) {
        if (edges == null || edges.isEmpty() || !StringUtils.hasText(nodeId)) {
            return null;
        }
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            if (nodeId.equals(edge.getString("sourceNodeId"))) {
                if (!StringUtils.hasText(sourcePortId) || sourcePortId.equals(edge.getString("sourcePortId"))) {
                    return edge.getString("targetNodeId");
                }
            }
        }
        return null;
    }

    public String findTargetNodeIdByEdgeId(String edgeId) {
        if (edges == null || edges.isEmpty() || !StringUtils.hasText(edgeId)) {
            return null;
        }
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            if (edgeId.equals(edge.getString("id"))) {
                return edge.getString("targetNodeId");
            }
        }
        return null;
    }
}
