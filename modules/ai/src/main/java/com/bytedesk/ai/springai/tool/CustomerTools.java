/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-21 10:29:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 10:33:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.spring.io/spring-ai/reference/api/tools.html#_requiredoptional
 */
@Slf4j
@Component
public class CustomerTools {

    @Tool(description = "Update customer information. This tool returns structured data for AI tool invocation.")
    public String updateCustomerInfo(
            @ToolParam(description = "Customer id") Long id,
            @ToolParam(description = "Customer name") String name,
            @ToolParam(description = "Customer email", required = false) String email) {
        log.info("Updated info for customer with id: {}", id);
        return "Customer info updated: id=" + id + ", name=" + name + ", email=" + email;
    }

    // @Tool(description = "Retrieve customer information", resultConverter = CustomToolCallResultConverter.class)
    // Customer getCustomerInfo(Long id) {
    //     return customerRepository.findById(id);
    // }

    // @Tool(description = "Retrieve customer information. This tool returns structured data for AI tool invocation.")
    // Customer getCustomerInfo(Long id, ToolContext toolContext) {
    //     return customerRepository.findById(id, toolContext.get("tenantId"));
    // }

    // @Tool(description = "Retrieve customer information", returnDirect = true)
    // Customer getCustomerInfo(Long id) {
    //     return customerRepository.findById(id);
    // }


}
