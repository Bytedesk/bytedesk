/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-03 10:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-03 10:15:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bytedesk.core.workflow.edge.WorkflowEdge;
import com.bytedesk.core.workflow.node.WorkflowBaseNode;
import com.bytedesk.core.workflow.node.WorkflowChoiceNode;
import com.bytedesk.core.workflow.node.WorkflowEndNode;
import com.bytedesk.core.workflow.node.WorkflowNodeMeta;
import com.bytedesk.core.workflow.node.WorkflowNodeTypeEnum;
import com.bytedesk.core.workflow.node.WorkflowStartNode;
import com.bytedesk.core.workflow.node.WorkflowTextNode;

/**
 * Workflow initialization data holder.
 * 提供用于后端初始化和示例演示的默认工作流结构。
 */
public final class WorkflowInitData {

        /** 默认流程 UID 后缀（需要与组织 UID 组合保证唯一性） */
        public static final String DEFAULT_WORKFLOW_UID_SUFFIX = "df_workflow_builder";

        /** 默认 IVR 流程 UID 后缀（非默认组织使用） */
        public static final String DEFAULT_IVR_WORKFLOW_UID_SUFFIX = "df_workflow_ivr_builder";

        /** 默认流程名称 */
        public static final String DEFAULT_WORKFLOW_NAME = "默认智能流程";

        /** 默认流程描述 */
        public static final String DEFAULT_WORKFLOW_DESCRIPTION = "FlowBuilder 示例流程";

        /** 默认 IVR 流程名称 */
        public static final String DEFAULT_IVR_WORKFLOW_NAME = "默认 IVR 自助服务流程";

        /** 默认 IVR 流程描述 */
        public static final String DEFAULT_IVR_WORKFLOW_DESCRIPTION = "IVRBuilder 示例流程，包含积分/余额查询、订单取消、服务政策播报";

        /** 默认开始节点 ID */
        public static final String DEFAULT_START_NODE_ID = "start_0";

        /** 默认 IVR 开始节点 ID */
        public static final String DEFAULT_IVR_START_NODE_ID = "ivr-start-0";

        private WorkflowInitData() {
        }

        /**
         * 构建默认示例工作流 Schema。
         */
        public static WorkflowSchema buildDefaultWorkflow() {
                List<WorkflowBaseNode> nodes = new ArrayList<>();
                List<WorkflowEdge> edges = new ArrayList<>();

                WorkflowStartNode startNode = WorkflowStartNode.builder()
                                .id(DEFAULT_START_NODE_ID)
                                .type(WorkflowNodeTypeEnum.START.getValue())
                                .name("开始接待")
                                .meta(WorkflowNodeMeta.builder()
                                                .position(WorkflowNodeMeta.Position.builder()
                                                                .x(100.0)
                                                                .y(200.0)
                                                                .build())
                                                .build())
                                .data(WorkflowBaseNode.NodeData.builder()
                                                .title("开始接待")
                                                .outputs(createOutputs("query", "string", "Hello Flow."))
                                                .build())
                                .build();
                nodes.add(startNode);

                WorkflowTextNode textNode1 = WorkflowTextNode.builder()
                                .id("text_0")
                                .type(WorkflowNodeTypeEnum.TEXT.getValue())
                                .name("文本节点")
                                .meta(WorkflowNodeMeta.builder()
                                                .position(WorkflowNodeMeta.Position.builder()
                                                                .x(320.0)
                                                                .y(200.0)
                                                                .build())
                                                .build())
                                .data(WorkflowBaseNode.NodeData.builder()
                                                .title("文本节点")
                                                .content("您好，欢迎进入默认工作流。")
                                                .build())
                                .build();
                nodes.add(textNode1);

                WorkflowChoiceNode choiceNode = WorkflowChoiceNode.builder()
                                .id("choice_0")
                                .type(WorkflowNodeTypeEnum.CHOICE.getValue())
                                .name("选择类型节点")
                                .meta(WorkflowNodeMeta.builder()
                                                .position(WorkflowNodeMeta.Position.builder()
                                                                .x(560.0)
                                                                .y(200.0)
                                                                .build())
                                                .build())
                                .data(WorkflowBaseNode.NodeData.builder()
                                                .title("选择类型节点")
                                                .content("请选择接下来要继续的内容")
                                                .options(createChoiceOptions())
                                                .build())
                                .build();
                nodes.add(choiceNode);

                WorkflowTextNode textNode2 = WorkflowTextNode.builder()
                                .id("text_1")
                                .type(WorkflowNodeTypeEnum.TEXT.getValue())
                                .name("文本节点")
                                .meta(WorkflowNodeMeta.builder()
                                                .position(WorkflowNodeMeta.Position.builder()
                                                                .x(800.0)
                                                                .y(200.0)
                                                                .build())
                                                .build())
                                .data(WorkflowBaseNode.NodeData.builder()
                                                .title("文本节点")
                                                .content("感谢您的选择，默认工作流继续执行到这里。")
                                                .build())
                                .build();
                nodes.add(textNode2);

                WorkflowEndNode endNode = WorkflowEndNode.builder()
                                .id("end_0")
                                .type(WorkflowNodeTypeEnum.END.getValue())
                                .name("结束")
                                .meta(WorkflowNodeMeta.builder()
                                                .position(WorkflowNodeMeta.Position.builder()
                                                                .x(1040.0)
                                                                .y(200.0)
                                                                .build())
                                                .build())
                                .data(WorkflowBaseNode.NodeData.builder()
                                                .title("结束")
                                                .outputs(createOutputs("result", "string", null))
                                                .build())
                                .build();
                nodes.add(endNode);

                WorkflowEdge edge1 = WorkflowEdge.builder()
                                .id("edge_start_text_0")
                                .sourceNodeID(DEFAULT_START_NODE_ID)
                                .targetNodeID("text_0")
                                .build();
                edges.add(edge1);

                WorkflowEdge edge2 = WorkflowEdge.builder()
                                .id("edge_text_choice_0")
                                .sourceNodeID("text_0")
                                .targetNodeID("choice_0")
                                .build();
                edges.add(edge2);

                WorkflowEdge edge3 = WorkflowEdge.builder()
                                .id("edge_choice_text_1")
                                .sourceNodeID("choice_0")
                                .targetNodeID("text_1")
                                .build();
                edges.add(edge3);

                WorkflowEdge edge4 = WorkflowEdge.builder()
                                .id("edge_text_end_0")
                                .sourceNodeID("text_1")
                                .targetNodeID("end_0")
                                .build();
                edges.add(edge4);

                return WorkflowSchema.builder()
                                .nodes(nodes)
                                .edges(edges)
                                .build();
        }

        private static List<Map<String, Object>> createChoiceOptions() {
                List<Map<String, Object>> options = new ArrayList<>();

                Map<String, Object> option1 = new HashMap<>();
                option1.put("label", "继续了解产品");
                option1.put("value", "product");
                options.add(option1);

                Map<String, Object> option2 = new HashMap<>();
                option2.put("label", "继续了解服务");
                option2.put("value", "service");
                options.add(option2);

                return options;
        }

        private static Map<String, Object> createOutputs(String propertyName, String type, String defaultValue) {
                Map<String, Object> outputs = new HashMap<>();
                Map<String, Object> properties = new HashMap<>();
                Map<String, Object> property = new HashMap<>();

                property.put("type", type);
                if (defaultValue != null) {
                        property.put("default", defaultValue);
                }

                properties.put(propertyName, property);
                outputs.put("type", "object");
                outputs.put("properties", properties);

                return outputs;
        }

        public static String buildDefaultIvrWorkflowSchemaJson() {
                return """
                                {
                                        "nodes": [
                                                {
                                                        "id": "%s",
                                                        "type": "start",
                                                        "meta": {
                                                                "position": { "x": 80, "y": 180 }
                                                        },
                                                        "data": {
                                                                "title": "来电进入",
                                                                "description": "默认 IVR 演示流程入口"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-text-welcome",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 360, "y": 160 }
                                                        },
                                                        "data": {
                                                                "title": "欢迎语",
                                                                "content": "您好，欢迎致电微语智能语音服务。按 1 可进行积分余额查询，按 2 可查询订单信息，按 3 可收听服务政策播报，按 0 可转人工服务。",
                                                                "description": "默认 IVR 欢迎播报"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-keyboard-main",
                                                        "type": "keyboard",
                                                        "meta": {
                                                                "position": { "x": 700, "y": 150 }
                                                        },
                                                        "data": {
                                                                "title": "业务导航",
                                                                "content": "请输入业务按键进行导航。",
                                                                "description": "主菜单按键导航",
                                                                "options": [
                                                                        {
                                                                                "id": "ivr-option-balance",
                                                                                "key": "1",
                                                                                "label": "按 1 查询积分/余额",
                                                                                "outgoingEdgeId": "ivr-text-balance"
                                                                        },
                                                                        {
                                                                                "id": "ivr-option-order-cancel",
                                                                                "key": "2",
                                                                                "label": "按 2 查询订单信息",
                                                                                "outgoingEdgeId": "ivr-http-order-query"
                                                                        },
                                                                        {
                                                                                "id": "ivr-option-policy",
                                                                                "key": "3",
                                                                                "label": "按 3 收听服务政策",
                                                                                "outgoingEdgeId": "ivr-text-policy"
                                                                        },
                                                                        {
                                                                                "id": "ivr-option-human",
                                                                                "key": "0",
                                                                                "label": "按 0 转人工服务",
                                                                                "outgoingEdgeId": "ivr-transfer-human"
                                                                        }
                                                                ]
                                                        }
                                                },
                                                {
                                                        "id": "ivr-text-balance",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1060, "y": 10 }
                                                        },
                                                        "data": {
                                                                "title": "积分/余额查询",
                                                                "content": "这里是积分与余额查询演示节点。后续可对接会员中心接口，播报当前积分、账户余额和最近一次积分变动。",
                                                                "description": "演示自助积分与余额查询"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-http-order-query",
                                                        "type": "http",
                                                        "meta": {
                                                                "position": { "x": 1060, "y": 210 }
                                                        },
                                                        "data": {
                                                                "title": "订单信息查询",
                                                                "content": "正在为您查询订单信息，请稍候。",
                                                                "description": "演示通过 HTTP 接口查询订单信息并播报结果",
                                                                "apiUrl": "/visitor/api/v1/ivr/demo/order?callerIdNumber=${callerIdNumber}",
                                                                "httpMethod": "GET",
                                                                "responseTemplate": "为您查询到演示订单 ${orderNumber}，当前状态：${orderStatus}，收件人：${receiverName}。",
                                                                "failurePrompt": "订单查询服务暂时不可用，请稍后再试。",
                                                                "timeoutMs": 3000
                                                        }
                                                },
                                                {
                                                        "id": "ivr-text-policy",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1060, "y": 410 }
                                                        },
                                                        "data": {
                                                                "title": "服务政策播报",
                                                                "content": "这里是服务政策播报演示节点。可用于播报售后政策、服务时间、隐私说明以及节假日服务安排。",
                                                                "description": "演示服务政策自动播报"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-transfer-human",
                                                        "type": "transfer",
                                                        "meta": {
                                                                "position": { "x": 1060, "y": 610 }
                                                        },
                                                        "data": {
                                                                "title": "转人工服务",
                                                                "content": "正在为您转接人工坐席，请稍候。",
                                                                "description": "默认转人工演示节点",
                                                                "transferDestination": "5003",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-end-0",
                                                        "type": "end",
                                                        "meta": {
                                                                "position": { "x": 1420, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "结束",
                                                                "description": "结束默认 IVR 演示流程"
                                                        }
                                                }
                                        ],
                                        "edges": [
                                                {
                                                        "sourceNodeID": "%s",
                                                        "targetNodeID": "ivr-text-welcome",
                                                        "sourcePortID": "defaultOutput",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-text-welcome",
                                                        "targetNodeID": "ivr-keyboard-main",
                                                        "sourcePortID": "defaultOutput",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-keyboard-main",
                                                        "targetNodeID": "ivr-text-balance",
                                                        "sourcePortID": "keyboard-option-ivr-option-balance",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-keyboard-main",
                                                        "targetNodeID": "ivr-http-order-query",
                                                        "sourcePortID": "keyboard-option-ivr-option-order-cancel",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-keyboard-main",
                                                        "targetNodeID": "ivr-text-policy",
                                                        "sourcePortID": "keyboard-option-ivr-option-policy",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-keyboard-main",
                                                        "targetNodeID": "ivr-transfer-human",
                                                        "sourcePortID": "keyboard-option-ivr-option-human",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-text-balance",
                                                        "targetNodeID": "ivr-end-0",
                                                        "sourcePortID": "defaultOutput",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-http-order-query",
                                                        "targetNodeID": "ivr-end-0",
                                                        "sourcePortID": "defaultOutput",
                                                        "targetPortID": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeID": "ivr-text-policy",
                                                        "targetNodeID": "ivr-end-0",
                                                        "sourcePortID": "defaultOutput",
                                                        "targetPortID": "defaultInput"
                                                }
                                        ]
                                }
                                """
                                .formatted(DEFAULT_IVR_START_NODE_ID, DEFAULT_IVR_START_NODE_ID);
        }

}
