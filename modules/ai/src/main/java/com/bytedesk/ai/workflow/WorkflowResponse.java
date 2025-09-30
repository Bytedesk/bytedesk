/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 12:28:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow;

import java.util.List;

import com.bytedesk.ai.workflow_edge.WorkflowEdgeResponse;
import com.bytedesk.ai.workflow_node.WorkflowNodeResponse;
import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;


    private String nickname;

    private String avatar;

    private String description;

    private String schema;

    private List<WorkflowNodeResponse> nodes;

    private List<WorkflowEdgeResponse> edges;

    private String type;

    private String categoryUid;

    private String currentNode;

}
