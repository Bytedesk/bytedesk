/*
 * @Author: Copilot
 * @Description: Workflow choice node for chat-style branching prompts
 */
package com.bytedesk.core.workflow.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.workflow.WorkflowExecutionContext;
import com.bytedesk.core.workflow.WorkflowNodeExecutionResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowChoiceNode extends WorkflowBaseNode {

    private static final long serialVersionUID = 1L;

    private List<ChoiceOption> options;

    public static WorkflowChoiceNode fromJson(String json) {
        return JSON.parseObject(json, WorkflowChoiceNode.class);
    }

    @Override
    public WorkflowNodeExecutionResult execute(WorkflowExecutionContext context) {
        Map<String, Object> output = new HashMap<>();
        output.put("text", resolvePrompt());
        output.put("options", resolveOptions());
        return WorkflowNodeExecutionResult.success("Choice node pending", null, output);
    }

    public String resolvePrompt() {
        if (getData() != null) {
            if (StringUtils.hasText(getData().getContent())) {
                return getData().getContent();
            }
            if (StringUtils.hasText(getData().getTitle())) {
                return getData().getTitle();
            }
        }
        if (StringUtils.hasText(getDescription())) {
            return getDescription();
        }
        return StringUtils.hasText(getName()) ? getName() : "请选择";
    }

    public List<ChoiceOption> resolveOptions() {
        if (options != null && !options.isEmpty()) {
            return options;
        }
        List<ChoiceOption> result = new ArrayList<>();
        if (getData() == null || getData().getOptions() == null) {
            return result;
        }
        for (Map<String, Object> item : getData().getOptions()) {
            if (item == null || item.isEmpty()) {
                continue;
            }
            String label = String.valueOf(item.getOrDefault("label", item.get("title")));
            String value = String.valueOf(item.getOrDefault("value", label));
            if (!StringUtils.hasText(label)) {
                continue;
            }
            result.add(ChoiceOption.builder().label(label).value(value).build());
        }
        return result;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceOption {

        private String label;

        private String value;
    }
}