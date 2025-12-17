package com.bytedesk.ticket.process;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import com.bytedesk.core.workflow.compiler.CompileResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcessDeploymentService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    public ProcessDefinition deploy(String name, String key, String tenantId, CompileResult result) {
        if (result == null || !result.isSuccess() || result.getBpmnXml() == null) {
            throw new IllegalArgumentException("invalid compile result");
        }

        DeploymentBuilder builder = repositoryService.createDeployment()
            .name(name)
            .tenantId(tenantId)
            .addString(key + ".bpmn20.xml", result.getBpmnXml());

        if (result.getDmnXml() != null && !result.getDmnXml().isEmpty()) {
            builder.addString(key + ".dmn", result.getDmnXml());
        }

        Deployment deployment = builder.deploy();

        return repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.getId())
            .latestVersion()
            .singleResult();
    }

    public String startByKey(String processDefinitionKey, String tenantId, Map<String, Object> variables) {
        if (variables == null) variables = new HashMap<>();
        return runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, variables, tenantId)
                .getId();
    }
}
