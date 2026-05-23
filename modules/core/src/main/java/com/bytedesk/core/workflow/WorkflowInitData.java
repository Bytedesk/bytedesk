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

        /** 默认满意度 IVR 流程 UID 后缀（非默认组织使用） */
        public static final String DEFAULT_IVR_SATISFACTION_WORKFLOW_UID_SUFFIX = "df_workflow_ivr_satisfaction_builder";

        /** 默认密码验证 IVR 流程 UID 后缀（非默认组织使用） */
        public static final String DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_UID_SUFFIX = "df_workflow_ivr_password_verification_builder";

        /** 默认机器人对话 IVR 流程 UID 后缀（非默认组织使用） */
        public static final String DEFAULT_IVR_BOT_WORKFLOW_UID_SUFFIX = "df_workflow_ivr_bot_builder";

        /** 默认流程名称 */
        public static final String DEFAULT_WORKFLOW_NAME = "默认智能流程";

        /** 默认流程描述 */
        public static final String DEFAULT_WORKFLOW_DESCRIPTION = "FlowBuilder 示例流程";

        /** 默认 IVR 流程名称 */
        public static final String DEFAULT_IVR_WORKFLOW_NAME = "默认 IVR 自助服务流程";

        /** 默认 IVR 流程描述 */
        public static final String DEFAULT_IVR_WORKFLOW_DESCRIPTION = "IVRBuilder 示例流程，包含积分/余额查询、订单取消、服务政策播报";

        /** 默认满意度 IVR 流程名称 */
        public static final String DEFAULT_IVR_SATISFACTION_WORKFLOW_NAME = "默认满意度回访 IVR 流程";

        /** 默认满意度 IVR 流程描述 */
        public static final String DEFAULT_IVR_SATISFACTION_WORKFLOW_DESCRIPTION = "IVRBuilder 满意度回访示例流程，包含评分收集和留言补充";

        /** 默认密码验证 IVR 流程名称 */
        public static final String DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_NAME = "默认密码验证 IVR 流程";

        /** 默认密码验证 IVR 流程描述 */
        public static final String DEFAULT_IVR_PASSWORD_VERIFICATION_WORKFLOW_DESCRIPTION = "IVRBuilder 验密示例流程，包含身份校验和失败转人工";

        /** 默认机器人对话 IVR 流程名称 */
        public static final String DEFAULT_IVR_BOT_WORKFLOW_NAME = "默认机器人对话 IVR 流程";

        /** 默认机器人对话 IVR 流程描述 */
        public static final String DEFAULT_IVR_BOT_WORKFLOW_DESCRIPTION = "IVRBuilder 机器人对话示例流程，支持转入多轮或不限轮语音机器人";

        /** 默认开始节点 ID */
        public static final String DEFAULT_START_NODE_ID = "start_0";

        /** 默认 IVR 开始节点 ID */
        public static final String DEFAULT_IVR_START_NODE_ID = "ivr-start-0";

        /** 默认满意度 IVR 开始节点 ID */
        public static final String DEFAULT_IVR_SATISFACTION_START_NODE_ID = "ivr-satisfaction-start-0";

        /** 默认密码验证 IVR 开始节点 ID */
        public static final String DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID = "ivr-password-start-0";

        /** 默认机器人对话 IVR 开始节点 ID */
        public static final String DEFAULT_IVR_BOT_START_NODE_ID = "ivr-bot-start-0";

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
                                .sourceNodeId(DEFAULT_START_NODE_ID)
                                .targetNodeId("text_0")
                                .build();
                edges.add(edge1);

                WorkflowEdge edge2 = WorkflowEdge.builder()
                                .id("edge_text_choice_0")
                                .sourceNodeId("text_0")
                                .targetNodeId("choice_0")
                                .build();
                edges.add(edge2);

                WorkflowEdge edge3 = WorkflowEdge.builder()
                                .id("edge_choice_text_1")
                                .sourceNodeId("choice_0")
                                .targetNodeId("text_1")
                                .build();
                edges.add(edge3);

                WorkflowEdge edge4 = WorkflowEdge.builder()
                                .id("edge_text_end_0")
                                .sourceNodeId("text_1")
                                .targetNodeId("end_0")
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
                                                                "position": { "x": 120, "y": 180 }
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
                                                                "position": { "x": 460, "y": 140 }
                                                        },
                                                        "data": {
                                                                "title": "欢迎语",
                                                                "content": "您好，欢迎致电微语智能语音服务。按 1 可进行积分余额查询，按 2 可查询订单信息，按 3 可收听服务政策播报，按 4 可体验机器人对话，按 0 可转人工服务。",
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
                                                                                "id": "ivr-option-bot",
                                                                                "key": "4",
                                                                                "label": "按 4 体验机器人对话",
                                                                                "outgoingEdgeId": "ivr-text-bot-menu"
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
                                                        "id": "ivr-text-bot-menu",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1060, "y": 560 }
                                                        },
                                                        "data": {
                                                                "title": "机器人模式说明",
                                                                "content": "您已进入机器人对话演示。按 1 体验多轮语音机器人，按 2 体验不限轮语音机器人，按 0 转人工服务。",
                                                                "description": "机器人子菜单欢迎语"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-keyboard-bot-menu",
                                                        "type": "keyboard",
                                                        "meta": {
                                                                "position": { "x": 1360, "y": 560 }
                                                        },
                                                        "data": {
                                                                "title": "机器人模式导航",
                                                                "content": "请输入机器人模式按键。",
                                                                "description": "机器人子菜单按键导航",
                                                                "options": [
                                                                        {
                                                                                "id": "ivr-bot-option-multi",
                                                                                "key": "1",
                                                                                "label": "按 1 体验多轮语音机器人",
                                                                                "outgoingEdgeId": "ivr-bot-node-multi"
                                                                        },
                                                                        {
                                                                                "id": "ivr-bot-option-unlimited",
                                                                                "key": "2",
                                                                                "label": "按 2 体验不限轮语音机器人",
                                                                                "outgoingEdgeId": "ivr-bot-node-unlimited"
                                                                        },
                                                                        {
                                                                                "id": "ivr-bot-option-human",
                                                                                "key": "0",
                                                                                "label": "按 0 转人工服务",
                                                                                "outgoingEdgeId": "ivr-transfer-human"
                                                                        }
                                                                ]
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-node-multi",
                                                        "type": "bot",
                                                        "meta": {
                                                                "position": { "x": 1720, "y": 420 }
                                                        },
                                                        "data": {
                                                                "title": "多轮语音机器人",
                                                                "content": "正在为您接入多轮语音机器人，请稍候。",
                                                                "description": "在默认 IVR 内直接接入 9201 多轮语音机器人",
                                                                "transferDestination": "9201",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-node-unlimited",
                                                        "type": "bot",
                                                        "meta": {
                                                                "position": { "x": 1720, "y": 620 }
                                                        },
                                                        "data": {
                                                                "title": "不限轮语音机器人",
                                                                "content": "正在为您接入不限轮语音机器人，请稍候。",
                                                                "description": "在默认 IVR 内直接接入 9203 不限轮语音机器人",
                                                                "transferDestination": "9203",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-transfer-human",
                                                        "type": "transfer",
                                                        "meta": {
                                                                "position": { "x": 1720, "y": 820 }
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
                                                                "position": { "x": 2080, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "结束",
                                                                "description": "结束默认 IVR 演示流程"
                                                        }
                                                }
                                        ],
                                        "edges": [
                                                {
                                                        "sourceNodeId": "%s",
                                                        "targetNodeId": "ivr-text-welcome",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-text-welcome",
                                                        "targetNodeId": "ivr-keyboard-main",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-main",
                                                        "targetNodeId": "ivr-text-balance",
                                                        "sourcePortId": "keyboard-option-ivr-option-balance",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-main",
                                                        "targetNodeId": "ivr-http-order-query",
                                                        "sourcePortId": "keyboard-option-ivr-option-order-cancel",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-main",
                                                        "targetNodeId": "ivr-text-policy",
                                                        "sourcePortId": "keyboard-option-ivr-option-policy",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-main",
                                                        "targetNodeId": "ivr-text-bot-menu",
                                                        "sourcePortId": "keyboard-option-ivr-option-bot",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-main",
                                                        "targetNodeId": "ivr-transfer-human",
                                                        "sourcePortId": "keyboard-option-ivr-option-human",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-text-balance",
                                                        "targetNodeId": "ivr-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-http-order-query",
                                                        "targetNodeId": "ivr-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-text-policy",
                                                        "targetNodeId": "ivr-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-text-bot-menu",
                                                        "targetNodeId": "ivr-keyboard-bot-menu",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-bot-menu",
                                                        "targetNodeId": "ivr-bot-node-multi",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-multi",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-bot-menu",
                                                        "targetNodeId": "ivr-bot-node-unlimited",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-unlimited",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-keyboard-bot-menu",
                                                        "targetNodeId": "ivr-transfer-human",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-human",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-node-multi",
                                                        "targetNodeId": "ivr-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-node-unlimited",
                                                        "targetNodeId": "ivr-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                }
                                        ]
                                }
                                """
                                .formatted(DEFAULT_IVR_START_NODE_ID, DEFAULT_IVR_START_NODE_ID);
        }

        public static String buildDefaultSatisfactionIvrWorkflowSchemaJson() {
                return """
                                {
                                        "nodes": [
                                                {
                                                        "id": "%s",
                                                        "type": "start",
                                                        "meta": {
                                                                "position": { "x": 120, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "满意度回访开始",
                                                                "description": "默认满意度回访 IVR 入口"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-text-welcome",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 440, "y": 210 }
                                                        },
                                                        "data": {
                                                                "title": "满意度邀请",
                                                                "content": "您好，本次通话即将结束。请对本次服务进行评价，按 1 表示非常满意，按 2 表示满意，按 3 表示一般，按 4 表示不满意，按 5 表示非常不满意。",
                                                                "description": "满意度评分播报"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-keyboard-main",
                                                        "type": "keyboard",
                                                        "meta": {
                                                                "position": { "x": 700, "y": 200 }
                                                        },
                                                        "data": {
                                                                "title": "满意度评分",
                                                                "content": "请输入满意度评分。",
                                                                "description": "收集 1 到 5 的满意度评分",
                                                                "options": [
                                                                        {
                                                                                "id": "ivr-satisfaction-option-5",
                                                                                "key": "1",
                                                                                "label": "按 1 表示非常满意",
                                                                                "outgoingEdgeId": "ivr-satisfaction-text-positive"
                                                                        },
                                                                        {
                                                                                "id": "ivr-satisfaction-option-4",
                                                                                "key": "2",
                                                                                "label": "按 2 表示满意",
                                                                                "outgoingEdgeId": "ivr-satisfaction-text-positive"
                                                                        },
                                                                        {
                                                                                "id": "ivr-satisfaction-option-3",
                                                                                "key": "3",
                                                                                "label": "按 3 表示一般",
                                                                                "outgoingEdgeId": "ivr-satisfaction-text-neutral"
                                                                        },
                                                                        {
                                                                                "id": "ivr-satisfaction-option-2",
                                                                                "key": "4",
                                                                                "label": "按 4 表示不满意",
                                                                                "outgoingEdgeId": "ivr-satisfaction-text-negative"
                                                                        },
                                                                        {
                                                                                "id": "ivr-satisfaction-option-1",
                                                                                "key": "5",
                                                                                "label": "按 5 表示非常不满意",
                                                                                "outgoingEdgeId": "ivr-satisfaction-text-negative"
                                                                        }
                                                                ]
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-text-positive",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1080, "y": 40 }
                                                        },
                                                        "data": {
                                                                "title": "高分反馈",
                                                                "content": "感谢您的好评，祝您生活愉快，再见。",
                                                                "description": "高分评价结束语"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-text-neutral",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1080, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "中立反馈",
                                                                "content": "感谢您的评价，我们会持续优化服务体验。",
                                                                "description": "中立评价结束语"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-text-negative",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1080, "y": 400 }
                                                        },
                                                        "data": {
                                                                "title": "低分反馈",
                                                                "content": "很抱歉本次服务未达预期。请在滴声后留下您的建议，我们将安排专人回访。",
                                                                "description": "低分评价与留言提示"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-satisfaction-end-0",
                                                        "type": "end",
                                                        "meta": {
                                                                "position": { "x": 1430, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "结束",
                                                                "description": "结束默认满意度回访流程"
                                                        }
                                                }
                                        ],
                                        "edges": [
                                                {
                                                        "sourceNodeId": "%s",
                                                        "targetNodeId": "ivr-satisfaction-text-welcome",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-text-welcome",
                                                        "targetNodeId": "ivr-satisfaction-keyboard-main",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-keyboard-main",
                                                        "targetNodeId": "ivr-satisfaction-text-positive",
                                                        "sourcePortId": "keyboard-option-ivr-satisfaction-option-5",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-keyboard-main",
                                                        "targetNodeId": "ivr-satisfaction-text-positive",
                                                        "sourcePortId": "keyboard-option-ivr-satisfaction-option-4",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-keyboard-main",
                                                        "targetNodeId": "ivr-satisfaction-text-neutral",
                                                        "sourcePortId": "keyboard-option-ivr-satisfaction-option-3",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-keyboard-main",
                                                        "targetNodeId": "ivr-satisfaction-text-negative",
                                                        "sourcePortId": "keyboard-option-ivr-satisfaction-option-2",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-keyboard-main",
                                                        "targetNodeId": "ivr-satisfaction-text-negative",
                                                        "sourcePortId": "keyboard-option-ivr-satisfaction-option-1",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-text-positive",
                                                        "targetNodeId": "ivr-satisfaction-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-text-neutral",
                                                        "targetNodeId": "ivr-satisfaction-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-satisfaction-text-negative",
                                                        "targetNodeId": "ivr-satisfaction-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                }
                                        ]
                                }
                                """
                                .formatted(DEFAULT_IVR_SATISFACTION_START_NODE_ID, DEFAULT_IVR_SATISFACTION_START_NODE_ID);
        }

        public static String buildDefaultPasswordVerificationIvrWorkflowSchemaJson() {
                return """
                                {
                                        "nodes": [
                                                {
                                                        "id": "%s",
                                                        "type": "start",
                                                        "meta": {
                                                                "position": { "x": 120, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "密码验证开始",
                                                                "description": "默认密码验证 IVR 入口"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-password-text-welcome",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 440, "y": 210 }
                                                        },
                                                        "data": {
                                                                "title": "验证提示",
                                                                "content": "您好，为保障账户安全，请输入 6 位服务密码并按井号键结束。",
                                                                "description": "密码验证前置播报"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-password-collect-main",
                                                        "type": "keyboard",
                                                        "meta": {
                                                                "position": { "x": 700, "y": 200 }
                                                        },
                                                        "data": {
                                                                "title": "输入服务密码",
                                                                "content": "请输入 6 位服务密码。",
                                                                "description": "收集密码并分流到验证结果",
                                                                "options": [
                                                                        {
                                                                                "id": "ivr-password-option-success",
                                                                                "key": "1",
                                                                                "label": "演示验证成功",
                                                                                "outgoingEdgeId": "ivr-password-text-success"
                                                                        },
                                                                        {
                                                                                "id": "ivr-password-option-failure",
                                                                                "key": "2",
                                                                                "label": "演示验证失败",
                                                                                "outgoingEdgeId": "ivr-password-transfer-human"
                                                                        }
                                                                ]
                                                        }
                                                },
                                                {
                                                        "id": "ivr-password-text-success",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 1080, "y": 100 }
                                                        },
                                                        "data": {
                                                                "title": "验证成功",
                                                                "content": "验证成功，您的身份已确认，稍后将继续原有服务流程。",
                                                                "description": "密码验证成功播报"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-password-transfer-human",
                                                        "type": "transfer",
                                                        "meta": {
                                                                "position": { "x": 1080, "y": 340 }
                                                        },
                                                        "data": {
                                                                "title": "验证失败转人工",
                                                                "content": "验证未通过，正在为您转接人工坐席，请稍候。",
                                                                "description": "密码验证失败时转人工",
                                                                "transferDestination": "5003",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-password-end-0",
                                                        "type": "end",
                                                        "meta": {
                                                                "position": { "x": 1430, "y": 180 }
                                                        },
                                                        "data": {
                                                                "title": "结束",
                                                                "description": "结束默认密码验证流程"
                                                        }
                                                }
                                        ],
                                        "edges": [
                                                {
                                                        "sourceNodeId": "%s",
                                                        "targetNodeId": "ivr-password-text-welcome",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-password-text-welcome",
                                                        "targetNodeId": "ivr-password-collect-main",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-password-collect-main",
                                                        "targetNodeId": "ivr-password-text-success",
                                                        "sourcePortId": "keyboard-option-ivr-password-option-success",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-password-collect-main",
                                                        "targetNodeId": "ivr-password-transfer-human",
                                                        "sourcePortId": "keyboard-option-ivr-password-option-failure",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-password-text-success",
                                                        "targetNodeId": "ivr-password-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                }
                                        ]
                                }
                                """
                                .formatted(DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID,
                                                DEFAULT_IVR_PASSWORD_VERIFICATION_START_NODE_ID);
        }

        public static String buildDefaultBotIvrWorkflowSchemaJson() {
                return """
                                {
                                        "nodes": [
                                                {
                                                        "id": "%s",
                                                        "type": "start",
                                                        "meta": {
                                                                "position": { "x": 120, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "机器人对话开始",
                                                                "description": "默认机器人对话 IVR 入口"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-text-welcome",
                                                        "type": "text",
                                                        "meta": {
                                                                "position": { "x": 440, "y": 210 }
                                                        },
                                                        "data": {
                                                                "title": "机器人欢迎语",
                                                                "content": "您好，欢迎进入机器人语音服务演示。按 1 体验多轮语音机器人，按 2 体验不限轮语音机器人，按 0 转人工服务。",
                                                                "description": "引导用户选择机器人通话模式"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-keyboard-main",
                                                        "type": "keyboard",
                                                        "meta": {
                                                                "position": { "x": 720, "y": 200 }
                                                        },
                                                        "data": {
                                                                "title": "机器人模式选择",
                                                                "content": "请输入业务按键，选择机器人对话模式。",
                                                                "description": "按键进入多轮、无限轮或人工服务",
                                                                "options": [
                                                                        {
                                                                                "id": "ivr-bot-option-multi",
                                                                                "key": "1",
                                                                                "label": "按 1 体验多轮语音机器人",
                                                                                "outgoingEdgeId": "ivr-bot-node-multi"
                                                                        },
                                                                        {
                                                                                "id": "ivr-bot-option-unlimited",
                                                                                "key": "2",
                                                                                "label": "按 2 体验不限轮语音机器人",
                                                                                "outgoingEdgeId": "ivr-bot-node-unlimited"
                                                                        },
                                                                        {
                                                                                "id": "ivr-bot-option-human",
                                                                                "key": "0",
                                                                                "label": "按 0 转人工服务",
                                                                                "outgoingEdgeId": "ivr-bot-transfer-human"
                                                                        }
                                                                ]
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-node-multi",
                                                        "type": "bot",
                                                        "meta": {
                                                                "position": { "x": 1120, "y": 40 }
                                                        },
                                                        "data": {
                                                                "title": "多轮语音机器人",
                                                                "content": "正在为您接入多轮语音机器人，请稍候。",
                                                                "description": "接入 9201 多轮语音机器人演示分机",
                                                                "transferDestination": "9201",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-node-unlimited",
                                                        "type": "bot",
                                                        "meta": {
                                                                "position": { "x": 1120, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "不限轮语音机器人",
                                                                "content": "正在为您接入不限轮语音机器人，请稍候。",
                                                                "description": "接入 9203 不限轮语音机器人演示分机",
                                                                "transferDestination": "9203",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-transfer-human",
                                                        "type": "transfer",
                                                        "meta": {
                                                                "position": { "x": 1120, "y": 400 }
                                                        },
                                                        "data": {
                                                                "title": "转人工服务",
                                                                "content": "正在为您转接人工坐席，请稍候。",
                                                                "description": "机器人演示流程中的人工兜底节点",
                                                                "transferDestination": "5003",
                                                                "transferContext": "default"
                                                        }
                                                },
                                                {
                                                        "id": "ivr-bot-end-0",
                                                        "type": "end",
                                                        "meta": {
                                                                "position": { "x": 1450, "y": 220 }
                                                        },
                                                        "data": {
                                                                "title": "结束",
                                                                "description": "结束默认机器人对话流程"
                                                        }
                                                }
                                        ],
                                        "edges": [
                                                {
                                                        "sourceNodeId": "%s",
                                                        "targetNodeId": "ivr-bot-text-welcome",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-text-welcome",
                                                        "targetNodeId": "ivr-bot-keyboard-main",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-keyboard-main",
                                                        "targetNodeId": "ivr-bot-node-multi",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-multi",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-keyboard-main",
                                                        "targetNodeId": "ivr-bot-node-unlimited",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-unlimited",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-keyboard-main",
                                                        "targetNodeId": "ivr-bot-transfer-human",
                                                        "sourcePortId": "keyboard-option-ivr-bot-option-human",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-node-multi",
                                                        "targetNodeId": "ivr-bot-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-node-unlimited",
                                                        "targetNodeId": "ivr-bot-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                },
                                                {
                                                        "sourceNodeId": "ivr-bot-transfer-human",
                                                        "targetNodeId": "ivr-bot-end-0",
                                                        "sourcePortId": "defaultOutput",
                                                        "targetPortId": "defaultInput"
                                                }
                                        ]
                                }
                                """
                                .formatted(DEFAULT_IVR_BOT_START_NODE_ID, DEFAULT_IVR_BOT_START_NODE_ID);
        }

}
