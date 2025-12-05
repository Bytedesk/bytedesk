package com.bytedesk.core.workflow.node;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多实例/循环配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiInstanceOptions implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Boolean parallel;           // true 并行，false 串行
    private String collectionExpression; // 集合表达式
    private String elementVariable;      // 元素变量名
    private String completionCondition;  // 完成条件表达式
}
