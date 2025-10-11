package com.bytedesk.ai.workflow.node;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ServiceTask 映射所需的最小元数据
 * 建议放入 WorkflowNodeEntity.nodeData 的 JSON 中
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTaskOptions implements Serializable {
    private static final long serialVersionUID = 1L;

    private String delegateBeanName; // e.g. "llmTask"
    private Boolean async;           // default false
    private Integer retry;           // default 0
    private Long backoffMillis;      // default 0
    private Long timeoutMillis;      // default null
}
