package com.bytedesk.ai.workflow.compiler;

import java.util.Objects;

/**
 * 将内部 DSL 聚合编译为 BPMN/DMN XML 的基础实现（骨架）。
 * 注意：此类不直接引用 Flowable API，仅生成 XML 字符串；
 * 真正部署由上层 Service 使用 Flowable RepositoryService 完成。
 */
public class FlowableBpmnCompiler implements WorkflowCompiler {

    @Override
    public CompileResult compile(WorkflowDefinitionAggregate aggregate) {
        // 基本校验
        if (aggregate == null || aggregate.getWorkflow() == null) {
            return CompileResult.builder()
                .success(false)
                .errors("aggregate/workflow is null")
                .build();
        }

        // TODO: 1) 对 nodes/edges/variables 做结构/语义校验（起止点、入出度、环等）
        // TODO: 2) 根据节点/边类型映射生成 BPMN XML（开始/结束/任务/网关/边条件）
        // TODO: 3) 将 LLM 节点映射为 ServiceTask + delegateExpression
        // TODO: 4) 将条件路由映射为 SequenceFlow 的 conditionExpression
        // TODO: 5) 将决策节点映射为 DecisionTask/BusinessRuleTask，并在 dmnXml 中生成决策表
        // TODO: 6) 变量入出参映射至 BPMN inputOutput 或流程变量

        String processId = safeId(aggregate.getWorkflow().getUid(), "wf");
        String processName = Objects.toString(aggregate.getWorkflow().getNickname(), processId);

        // 首批：生成包含 start/end、serviceTask(LLM)、exclusiveGateway、条件连线的最小 BPMN
        StringBuilder process = new StringBuilder();
        process.append("  <process id=\"").append(processId).append("\" name=\"")
               .append(escape(processName)).append("\" isExecutable=\"true\">\n");

        // 简化：若没有节点，至少放一个 start->end
    java.util.concurrent.atomic.AtomicBoolean hasStart = new java.util.concurrent.atomic.AtomicBoolean(false);
    java.util.concurrent.atomic.AtomicBoolean hasEnd = new java.util.concurrent.atomic.AtomicBoolean(false);

        if (aggregate.getNodes() == null || aggregate.getNodes().isEmpty()) {
            process.append("    <startEvent id=\"start\" />\n");
            process.append("    <endEvent id=\"end\" />\n");
            process.append("    <sequenceFlow id=\"flow_start_end\" sourceRef=\"start\" targetRef=\"end\" />\n");
        } else {
            // 预计算 ID 映射
            java.util.Map<String, String> nodeIdMap = new java.util.HashMap<>();
            java.util.Map<String, String> nodeTypeMap = new java.util.HashMap<>();
            java.util.Map<String, String> nodeNameMap = new java.util.HashMap<>();
            aggregate.getNodes().forEach(n -> {
                String id = safeId(n.getUid(), "n" + n.getId());
                nodeIdMap.put(n.getUid(), id);
                nodeTypeMap.put(id, n.getType());
                nodeNameMap.put(id, n.getName());
            });

            class EdgeInfo { String id; String src; String tgt; String cond; boolean def; }
            java.util.List<EdgeInfo> edgeInfos = new java.util.ArrayList<>();
            if (aggregate.getEdges() != null) {
                aggregate.getEdges().forEach(e -> {
                    EdgeInfo ei = new EdgeInfo();
                    ei.id = safeId(e.getUid(), "f" + e.getId());
                    ei.src = nodeIdMap.getOrDefault(e.getSourceNodeId(), safeId(e.getSourceNodeId(), e.getSourceNodeId()));
                    ei.tgt = nodeIdMap.getOrDefault(e.getTargetNodeId(), safeId(e.getTargetNodeId(), e.getTargetNodeId()));
                    ei.cond = e.getConditionExpression();
                    // defaultBranch 字段
                    try { ei.def = Boolean.TRUE.equals(e.getDefaultBranch()); } catch (Exception ex) { ei.def = false; }
                    edgeInfos.add(ei);
                });
            }

            // 计算网关的 default 出边
            java.util.Map<String, String> gatewayDefaultFlow = new java.util.HashMap<>();
            nodeTypeMap.forEach((safeNodeId, t) -> {
                if ("exclusiveGateway".equals(t) || "inclusiveGateway".equals(t)) {
                    for (EdgeInfo ei : edgeInfos) {
                        if (ei.def && safeNodeId.equals(ei.src)) {
                            gatewayDefaultFlow.put(safeNodeId, ei.id);
                            break;
                        }
                    }
                }
            });

            // 输出节点元素
            aggregate.getNodes().forEach(n -> {
                String id = nodeIdMap.get(n.getUid());
                String name = nodeNameMap.get(id);
                String type = nodeTypeMap.get(id);
                if ("start".equals(type)) {
                    hasStart.set(true);
                    process.append("    <startEvent id=\"").append(id).append("\" name=\"")
                           .append(escape(name)).append("\" />\n");
                } else if ("end".equals(type)) {
                    hasEnd.set(true);
                    process.append("    <endEvent id=\"").append(id).append("\" name=\"")
                           .append(escape(name)).append("\" />\n");
                } else if ("llm".equals(type)) {
                    String taskId = id;
                    process.append("    <serviceTask id=\"").append(taskId).append("\" name=\"")
                           .append(escape(name)).append("\" ")
                           .append("flowable:delegateExpression=\"#{llmTask}\" ")
                           .append("/>\n");
                } else if ("exclusiveGateway".equals(type)) {
                    String defFlow = gatewayDefaultFlow.get(id);
                    process.append("    <exclusiveGateway id=\"").append(id).append("\" name=\"")
                           .append(escape(name)).append("\"");
                    if (defFlow != null) {
                        process.append(" default=\"").append(defFlow).append("\"");
                    }
                    process.append(" />\n");
                } else if ("parallelGateway".equals(type)) {
                    process.append("    <parallelGateway id=\"").append(id).append("\" name=\"")
                           .append(escape(name)).append("\" />\n");
                } else if ("inclusiveGateway".equals(type)) {
                    String defFlow = gatewayDefaultFlow.get(id);
                    process.append("    <inclusiveGateway id=\"").append(id).append("\" name=\"")
                           .append(escape(name)).append("\"");
                    if (defFlow != null) {
                        process.append(" default=\"").append(defFlow).append("\"");
                    }
                    process.append(" />\n");
                } else {
                    // 其他类型先忽略，后续补齐
                }
            });

            // 输出连线
            for (EdgeInfo ei : edgeInfos) {
                process.append("    <sequenceFlow id=\"").append(ei.id)
                       .append("\" sourceRef=\"").append(ei.src)
                       .append("\" targetRef=\"").append(ei.tgt).append("\"");
                if (ei.cond != null && !ei.cond.isEmpty()) {
                    process.append(">\n");
                    process.append("      <conditionExpression xsi:type=\"tFormalExpression\">")
                           .append(escape(ei.cond)).append("</conditionExpression>\n");
                    process.append("    </sequenceFlow>\n");
                } else {
                    process.append(" />\n");
                }
            }

            // 若缺失起止，补一个
            if (!hasStart.get()) {
                process.append("    <startEvent id=\"auto_start\" />\n");
            }
            if (!hasEnd.get()) {
                process.append("    <endEvent id=\"auto_end\" />\n");
            }
        }

        process.append("  </process>\n");

        String bpmnXml = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            " xmlns:flowable=\"http://flowable.org/bpmn\"\n" +
            " targetNamespace=\"http://www.flowable.org/processdef\">\n" +
            process.toString() +
            "</definitions>";

    return CompileResult.builder()
            .success(true)
            .bpmnXml(bpmnXml)
            .warnings("partial mapping generated: start/end/serviceTask(llm)/exclusiveGateway/sequenceFlow")
            .build();
    }

    private static String safeId(String id, String def) {
        if (id == null || id.isEmpty()) return def;
        // BPMN ID 需合法化
    return id.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
