package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptBlockOptions extends BlockOptions {
    private String code;
    private String runtime;  // NODE, PYTHON, etc.
    private String variableName;  // 存储执行结果的变量
    private String errorMessage;
    private boolean async;
    private Map<String, Object> env;
    private RuntimeConfig runtimeConfig;
}
