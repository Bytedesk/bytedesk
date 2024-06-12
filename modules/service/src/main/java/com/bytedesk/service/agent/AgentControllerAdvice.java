/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 19:16:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-04 10:20:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.JsonResultCodeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Order(value = 1) // 设置执行顺序，值越小，优先级越高
public class AgentControllerAdvice {

    @ExceptionHandler(AgentExceptionOffline.class)
    public ResponseEntity<?> handleAgentOfflineException(AgentExceptionOffline e) {
        // 在这里处理AgentExceptionOffline
        log.info("agent offline: " + e.getMessage());
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage(), JsonResultCodeEnum.AGENT_OFFLINE.getValue()));
    }

    @ExceptionHandler(AgentExceptionUnavailable.class)
    public ResponseEntity<?> handleAgentNotAvailableException(AgentExceptionUnavailable e) {
        // 在这里处理AAgentExceptionUnavailable
        log.info("agent unavailable: " + e.getMessage());
        return ResponseEntity.ok()
                .body(JsonResult.error(e.getMessage(), JsonResultCodeEnum.AGENT_UNAVAILABLE.getValue()));
    }

    @ExceptionHandler(AgentExceptionRobot.class)
    public ResponseEntity<?> handleAgentRobotException(AgentExceptionRobot e) {
        // 在这里处理AgentExceptionRobot
        log.info("agent route to robot: " + e.getMessage());
        return ResponseEntity.ok().body(JsonResult.error(e.getMessage(), JsonResultCodeEnum.ROUTE_TO_ROBOT.getValue()));
    }

}
