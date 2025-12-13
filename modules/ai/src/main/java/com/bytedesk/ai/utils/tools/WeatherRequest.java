/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-21 10:10:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 10:19:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils.tools;

import org.springframework.ai.tool.annotation.ToolParam;

public record WeatherRequest(@ToolParam(description = "The name of a city or a country") String location, Unit unit) {}
