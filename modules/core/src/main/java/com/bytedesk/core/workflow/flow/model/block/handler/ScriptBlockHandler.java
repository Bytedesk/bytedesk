package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ScriptBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class ScriptBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private final Map<String, ScriptEngine> engineCache;
    private final ExecutorService executorService;
    
    public ScriptBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.engineCache = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public String getType() {
        return BlockType.SCRIPT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ScriptBlockOptions options = objectMapper.convertValue(block.getOptions(), ScriptBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            // 处理脚本中的变量
            String processedCode = processTemplate(options.getCode(), context);
            
            // 准备执行环境
            Map<String, Object> env = new HashMap<>(context);
            if (options.getEnv() != null) {
                env.putAll(options.getEnv());
            }
            
            // 执行脚本
            Object scriptResult = options.isAsync() ?
                executeAsyncScript(processedCode, env, options) :
                executeScript(processedCode, env, options);
            
            // 存储执行结果
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), scriptResult);
            }
            
            result.put("scriptResult", scriptResult);
            result.put("success", true);
            
        } catch (Exception e) {
            log.error("Script execution failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ScriptBlockOptions options = objectMapper.convertValue(block.getOptions(), ScriptBlockOptions.class);
            return options.getCode() != null && 
                   options.getRuntime() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Object executeScript(String code, Map<String, Object> env, ScriptBlockOptions options) throws Exception {
        ScriptEngine engine = getScriptEngine(options.getRuntime());
        SimpleBindings bindings = new SimpleBindings();
        bindings.putAll(env);
        
        // 添加工具函数
        addUtilityFunctions(bindings);
        
        return engine.eval(code, bindings);
    }

    private Object executeAsyncScript(String code, Map<String, Object> env, ScriptBlockOptions options) throws Exception {
        Future<Object> future = executorService.submit(() -> executeScript(code, env, options));
        
        int timeout = options.getRuntimeConfig() != null && options.getRuntimeConfig().getTimeout() != null ? 
            options.getRuntimeConfig().getTimeout() : 5000;
            
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Script execution timed out after " + timeout + "ms");
        }
    }

    private ScriptEngine getScriptEngine(String runtime) {
        ScriptEngineManager manager = new ScriptEngineManager();
        switch (runtime.toUpperCase()) {
            case "NODE":
                return manager.getEngineByName("nashorn");
            case "PYTHON":
                return manager.getEngineByName("python");
            default:
                throw new IllegalArgumentException("Unsupported runtime: " + runtime);
        }
    }

    private void addUtilityFunctions(SimpleBindings bindings) {
        // bindings.put("log", (java.util.function.Consumer<Object>) log::info);
        bindings.put("sleep", (java.util.function.Consumer<Integer>) this::sleep);
        bindings.put("parseFloat", (java.util.function.Function<String, Double>) Double::parseDouble);
        bindings.put("parseInt", (java.util.function.Function<String, Integer>) Integer::parseInt);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Script execution interrupted", e);
        }
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
