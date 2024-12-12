package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RedirectBlockOptions extends BlockOptions {
    private String url;              // 重定向URL
    private boolean isNewTab;        // 是否在新标签页打开
    private boolean waitForRedirect; // 是否等待重定向完成
    private Integer delay;           // 延迟时间(毫秒)
    private String variableName;     // 存储重定向状态的变量名
} 
