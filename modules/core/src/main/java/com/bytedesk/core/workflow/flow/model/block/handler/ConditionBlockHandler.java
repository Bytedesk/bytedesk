/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2024-12-10 11:46:43
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-10 17:48:10
 * @FilePath: /backend/src/main/java/io/typebot/features/block/handler/ConditionBlockHandler.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ConditionBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.ConditionItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConditionBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;

    public ConditionBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.CONDITION.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ConditionBlockOptions options = objectMapper.convertValue(block.getOptions(), ConditionBlockOptions.class);

        boolean conditionMet = evaluateConditions(options.getItems(), context);

        Map<String, Object> result = new HashMap<>(context);
        result.put("conditionMet", conditionMet);
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ConditionBlockOptions options = objectMapper.convertValue(block.getOptions(), ConditionBlockOptions.class);
            return options.getItems() != null && !options.getItems().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateConditions(List<ConditionItem> items, Map<String, Object> context) {
        return items.stream().anyMatch(item -> evaluateCondition(item, context));
    }

    private boolean evaluateCondition(ConditionItem item, Map<String, Object> context) {
        Object value = context.get(item.getVariableId());
        if (value == null)
            return false;

        String stringValue = value.toString();
        String comparison = item.getValue();

        switch (item.getComparisonOperator().toLowerCase()) {
            case "equals":
                return stringValue.equals(comparison);
            case "contains":
                return stringValue.contains(comparison);
            case "greater":
                return compareNumbers(stringValue, comparison) > 0;
            case "less":
                return compareNumbers(stringValue, comparison) < 0;
            default:
                return false;
        }
    }

    private int compareNumbers(String value1, String value2) {
        try {
            double num1 = Double.parseDouble(value1);
            double num2 = Double.parseDouble(value2);
            return Double.compare(num1, num2);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
