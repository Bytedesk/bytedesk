/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:46:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 13:24:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.ChoiceBlockOptions;
import com.bytedesk.core.workflow.flow.model.block.model.options.ChoiceItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ChoiceBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;

    public ChoiceBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.CHOICE_INPUT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        ChoiceBlockOptions options = objectMapper.convertValue(block.getOptions(), ChoiceBlockOptions.class);

        if (options.isDynamicItems() && options.getDynamicVariableId() != null) {
            Object dynamicItems = context.get(options.getDynamicVariableId());
            if (dynamicItems instanceof List) {
                options.setItems(convertDynamicItems((List<?>) dynamicItems));
            }
        }

        Map<String, Object> result = new HashMap<>(context);
        result.put("choices", options.getItems());
        result.put("isMultiple", options.isMultipleChoice());
        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            ChoiceBlockOptions options = objectMapper.convertValue(block.getOptions(), ChoiceBlockOptions.class);
            return options.getItems() != null && !options.getItems().isEmpty() ||
                    (options.isDynamicItems() && options.getDynamicVariableId() != null);
        } catch (Exception e) {
            return false;
        }
    }

    private List<ChoiceItem> convertDynamicItems(List<?> items) {
        return items.stream()
                .<ChoiceItem>map(item -> {
                    ChoiceItem choice = new ChoiceItem();
                    choice.setId(String.valueOf(item.hashCode()));
                    choice.setContent(item.toString());
                    choice.setValue(item.toString());
                    return choice;
                })
                .collect(Collectors.toList());
    }
}
