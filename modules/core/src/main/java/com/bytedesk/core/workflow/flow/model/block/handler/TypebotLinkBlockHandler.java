/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:59:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 11:02:27
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

import com.bytedesk.core.workflow.flow.FlowRestService;
import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.TypebotLinkBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TypebotLinkBlockHandler implements BlockHandler {

    private final ObjectMapper objectMapper;

    private final FlowRestService botService;

    public TypebotLinkBlockHandler(ObjectMapper objectMapper, FlowRestService botService) {
        this.objectMapper = objectMapper;
        this.botService = botService;
    }

    @Override
    public String getType() {
        return BlockType.TYPEBOT_LINK.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        TypebotLinkBlockOptions options = objectMapper.convertValue(block.getOptions(), TypebotLinkBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);

        try {
            // 验证链接的Typebot是否存在
            // botService.validateFlowAccess(options.getTypebotId(), context);

            // 处理变量映射
            Map<String, Object> mappedVariables = new HashMap<>();
            if (options.getVariableMapping() != null) {
                options.getVariableMapping().forEach((targetVar, sourceVar) -> {
                    Object value = context.get(sourceVar);
                    if (value != null) {
                        mappedVariables.put(targetVar, value);
                    }
                });
            }

            result.put("linkedTypebotId", options.getTypebotId());
            result.put("startGroupId", options.getGroupId());
            result.put("mappedVariables", mappedVariables);
            result.put("mergeResults", options.isMergeResults());

            if (options.getVariableName() != null) {
                result.put(options.getVariableName(), true);
            }

            result.put("success", true);
            result.put("blockType", "typebotLink");

        } catch (Exception e) {
            log.error("Typebot link processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
        }

        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            TypebotLinkBlockOptions options = objectMapper.convertValue(block.getOptions(),
                    TypebotLinkBlockOptions.class);
            return options.getTypebotId() != null && !options.getTypebotId().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
