package com.bytedesk.ticket.process;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.workflow.compiler.FlowableBpmnCompiler;
import com.bytedesk.ai.workflow.compiler.WorkflowCompiler;
import com.bytedesk.ai.workflow.compiler.WorkflowDefinitionAggregate;
import com.bytedesk.ai.workflow.WorkflowEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/flowable")
@RequiredArgsConstructor
public class FlowableDeploymentController {

    private final FlowableDeploymentService deploymentService;

    @PostMapping("/deploy")
    public ResponseEntity<?> deploy(@RequestBody DeployRequest req) {
        WorkflowCompiler compiler = new FlowableBpmnCompiler();
        WorkflowEntity wf = WorkflowEntity.builder().uid(req.key).nickname(req.name).schema(null).build();
        WorkflowDefinitionAggregate agg = WorkflowDefinitionAggregate.builder()
            .workflow(wf)
            .nodes(req.nodes)
            .edges(req.edges)
            .variables(req.variables)
            .build();
        var result = compiler.compile(agg);
        var pd = deploymentService.deploy(req.name, req.key, req.tenantId, result);
        return ResponseEntity.ok(Map.of("deploymentId", pd.getDeploymentId(), "definitionId", pd.getId(), "key", pd.getKey(), "version", pd.getVersion()));
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartRequest req) {
        String instanceId = deploymentService.startByKey(req.key, req.tenantId, req.variables);
        return ResponseEntity.ok(Map.of("instanceId", instanceId));
    }

    // DTOs
    public static class DeployRequest {
        public String name;
        public String key;
        public String tenantId;
        public List<com.bytedesk.ai.workflow_node.WorkflowNodeEntity> nodes;
        public List<com.bytedesk.ai.workflow_edge.WorkflowEdgeEntity> edges;
        public List<com.bytedesk.ai.workflow.variable.WorkflowVariableEntity> variables;
    }

    public static class StartRequest {
        public String key;
        public String tenantId;
        public Map<String, Object> variables;
    }
}
