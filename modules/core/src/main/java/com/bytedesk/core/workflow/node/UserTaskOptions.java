package com.bytedesk.core.workflow.node;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserTask 映射所需的最小元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskOptions implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String assignee;
    private List<String> candidateUsers;
    private List<String> candidateGroups;
    private String formKey;
    private String dueDate; // ISO8601 string or expression
}
