/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-21 10:09:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 10:11:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool.test;

import java.util.function.Function;

/**
 * https://docs.spring.io/spring-ai/reference/api/tools.html#_functions_as_tools
 */
public class ToolWeatherService implements Function<ToolWeatherRequest, ToolWeatherResponse> {

    public ToolWeatherResponse apply(ToolWeatherRequest request) {
        // 
        return new ToolWeatherResponse(30.0, Unit.C);
    }
}

