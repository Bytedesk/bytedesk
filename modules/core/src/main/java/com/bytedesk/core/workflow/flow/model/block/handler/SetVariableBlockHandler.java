package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ExpressionType;
import com.bytedesk.core.workflow.flow.model.block.model.options.SetVariableBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SetVariableBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private final ScriptEngine scriptEngine;
    
    public SetVariableBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public String getType() {
        return BlockType.SET_VARIABLE.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        SetVariableBlockOptions options = objectMapper.convertValue(block.getOptions(), SetVariableBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            Object value = evaluateExpression(options, context);
            if (options.getVariableId() != null) {
                result.put(options.getVariableId(), value);
                result.put("success", true);
                result.put("message", "Variable set successfully");
            }
        } catch (Exception e) {
            log.error("Variable evaluation failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            SetVariableBlockOptions options = objectMapper.convertValue(block.getOptions(), SetVariableBlockOptions.class);
            return options.getVariableId() != null && 
                   options.getExpressionToEvaluate() != null &&
                   options.getType() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Object evaluateExpression(SetVariableBlockOptions options, Map<String, Object> context) throws Exception {
        String expression = options.getExpressionToEvaluate();
        ExpressionType type = ExpressionType.fromString(options.getType().toString());
        
        switch (type) {
            case STATIC:
                return expression;
                
            case JAVASCRIPT:
                if (!options.isCode()) {
                    return expression;
                }
                return evaluateJavaScript(expression, context);
                
            case TEMPLATE:
                return processTemplate(expression, context);
                
            default:
                throw new UnsupportedOperationException(
                    "Unsupported expression type: " + type);
        }
    }

    private Object evaluateJavaScript(String script, Map<String, Object> context) throws Exception {
        SimpleBindings bindings = new SimpleBindings();
        // 将上下文变量注入到脚本引擎
        bindings.putAll(context);
        
        // 添加一些工具函数
        bindings.put("parseFloat", (java.util.function.Function<String, Double>) Double::parseDouble);
        bindings.put("parseInt", (java.util.function.Function<String, Integer>) Integer::parseInt);
        bindings.put("Date", java.time.LocalDateTime.class);
        
        return scriptEngine.eval(script, bindings);
    }

    private String processTemplate(String template, Map<String, Object> context) {
        if (template == null) return null;
        
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }
} 
