package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.WaitBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.WaitCondition;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WaitBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;
    private static final int MAX_WAIT_SECONDS = 300;  // 最大等待5分钟
    
    public WaitBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.WAIT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        WaitBlockOptions options = objectMapper.convertValue(block.getOptions(), WaitBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);
        
        try {
            // 验证等待时间
            validateWaitTime(options);
            
            // 处理等待消息
            if (options.getMessage() != null) {
                result.put("waitMessage", processTemplate(options.getMessage(), context));
            }
            
            // 设置等待配置
            result.put("waitSeconds", options.getSeconds());
            result.put("showProgress", options.isShowProgress());
            result.put("blockType", "wait");
            
            // 处理等待条件
            if (options.getCondition() != null) {
                handleWaitCondition(options.getCondition(), result, context);
            } else {
                // 简单的时间等待
                if (!context.containsKey("skipWait")) {
                    TimeUnit.SECONDS.sleep(options.getSeconds());
                }
            }
            
            // 设置变量
            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }
            
            result.put("success", true);
            result.put("completed", true);
            
        } catch (InterruptedException e) {
            log.warn("Wait interrupted", e);
            Thread.currentThread().interrupt();
            result.put("error", "Wait interrupted");
            result.put("success", false);
        } catch (Exception e) {
            log.error("Wait processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            WaitBlockOptions options = objectMapper.convertValue(block.getOptions(), WaitBlockOptions.class);
            return validateWaitOptions(options);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateWaitOptions(WaitBlockOptions options) {
        if (options.getCondition() != null) {
            return validateWaitCondition(options.getCondition());
        }
        return options.getSeconds() != null && 
               options.getSeconds() > 0 && 
               options.getSeconds() <= MAX_WAIT_SECONDS;
    }

    private boolean validateWaitCondition(WaitCondition condition) {
        // if (condition.getType() == null) {
        //     return false;
        // }
        
        // switch (condition.getType().toUpperCase()) {
        //     case "EVENT":
        //         return condition.getEvent() != null;
        //     case "VARIABLE":
        //         return condition.getVariable() != null && 
        //                condition.getOperator() != null &&
        //                condition.getValue() != null;
        //     case "TIME":
        //         return condition.getTimeout() != null && 
        //                condition.getTimeout() > 0 &&
        //                condition.getTimeout() <= MAX_WAIT_SECONDS;
        //     default:
        //         return false;
        // }
        return true;
    }

    private void validateWaitTime(WaitBlockOptions options) {
        if (options.getSeconds() == null || 
            options.getSeconds() <= 0 || 
            options.getSeconds() > MAX_WAIT_SECONDS) {
            throw new IllegalArgumentException(
                String.format("Wait time must be between 1 and %d seconds", MAX_WAIT_SECONDS));
        }
    }

    private void handleWaitCondition(WaitCondition condition, Map<String, Object> result, Map<String, Object> context) {
        // switch (condition.getType().toUpperCase()) {
        //     case "EVENT":
        //         handleEventWait(condition, result);
        //         break;
        //     case "VARIABLE":
        //         handleVariableWait(condition, result, context);
        //         break;
        //     case "TIME":
        //         handleTimeWait(condition, result);
        //         break;
        //     default:
        //         throw new IllegalArgumentException("Unsupported wait condition type: " + condition.getType());
        // }
    }

    private void handleEventWait(WaitCondition condition, Map<String, Object> result) {
        // result.put("waitEvent", condition.getEvent());
        result.put("waitType", "event");
        result.put("waitTimeout", condition.getTimeout());
    }

    private void handleVariableWait(WaitCondition condition, Map<String, Object> result, Map<String, Object> context) {
        String variableName = condition.getVariable();
        String operator = condition.getOperator();
        String expectedValue = condition.getValue();
        
        // 获取当前变量值
        Object currentValue = context.get(variableName);
        
        // 检查是否已满足条件
        boolean conditionMet = evaluateCondition(currentValue, operator, expectedValue);
        
        result.put("waitVariable", variableName);
        result.put("waitOperator", operator);
        result.put("waitValue", expectedValue);
        result.put("waitType", "variable");
        result.put("waitTimeout", condition.getTimeout());
        result.put("conditionMet", conditionMet);
        
        if (conditionMet) {
            result.put("completed", true);
        }
    }

    private void handleTimeWait(WaitCondition condition, Map<String, Object> result) {
        int timeout = condition.getTimeout() != null ? condition.getTimeout() : MAX_WAIT_SECONDS;
        validateTimeout(timeout);
        
        result.put("waitTimeout", timeout);
        result.put("waitType", "time");
        
        // try {
        //     if (!context.containsKey("skipWait")) {
        //         TimeUnit.SECONDS.sleep(timeout);
        //     }
        //     result.put("completed", true);
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //     throw new RuntimeException("Time wait interrupted", e);
        // }
    }

    private boolean evaluateCondition(Object currentValue, String operator, String expectedValue) {
        if (currentValue == null) {
            return "null".equals(expectedValue) || "empty".equals(expectedValue);
        }
        
        String currentStr = String.valueOf(currentValue);
        
        switch (operator.toLowerCase()) {
            case "equals":
                return currentStr.equals(expectedValue);
            case "notEquals":
                return !currentStr.equals(expectedValue);
            case "contains":
                return currentStr.contains(expectedValue);
            case "notContains":
                return !currentStr.contains(expectedValue);
            case "greaterThan":
                return compareNumbers(currentStr, expectedValue) > 0;
            case "lessThan":
                return compareNumbers(currentStr, expectedValue) < 0;
            case "empty":
                return currentStr.isEmpty();
            case "notEmpty":
                return !currentStr.isEmpty();
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    private int compareNumbers(String value1, String value2) {
        try {
            double num1 = Double.parseDouble(value1);
            double num2 = Double.parseDouble(value2);
            return Double.compare(num1, num2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Values must be numbers for comparison: " + value1 + ", " + value2);
        }
    }

    private void validateTimeout(int timeout) {
        if (timeout <= 0 || timeout > MAX_WAIT_SECONDS) {
            throw new IllegalArgumentException(
                String.format("Timeout must be between 1 and %d seconds", MAX_WAIT_SECONDS));
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
