/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 11:14:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/robot/message")
@AllArgsConstructor
@Description("Robot Message Controller - AI robot message and conversation management APIs")
public class RobotMessageRestController extends BaseRestController<RobotMessageRequest, RobotMessageRestService> {

    private final RobotMessageRestService robotMessageRestService;

    @Operation(summary = "Query Robot Messages by Organization", description = "Retrieve robot messages for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query robot message")
    @Override
    public ResponseEntity<?> queryByOrg(RobotMessageRequest request) {
        
        Page<RobotMessageResponse> page = robotMessageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Robot Messages by User", description = "Retrieve robot messages for the current user")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query robot message")
    @Override
    public ResponseEntity<?> queryByUser(RobotMessageRequest request) {
        
        Page<RobotMessageResponse> page = robotMessageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Robot Messages by UID", description = "Retrieve a specific robot message entry by its unique identifier")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query robot message")
    @Override
    public ResponseEntity<?> queryByUid(RobotMessageRequest request) {
        
        RobotMessageResponse response = robotMessageRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Create Robot Messages", description = "Create a new robot message entry")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_CREATE, description = "create robot message")
    @Override
    public ResponseEntity<?> create(RobotMessageRequest request) {
        
        RobotMessageResponse response = robotMessageRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Robot Messages", description = "Update an existing robot message entry")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_UPDATE, description = "update robot message")
    @Override
    public ResponseEntity<?> update(RobotMessageRequest request) {
        
        RobotMessageResponse response = robotMessageRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Robot Messages", description = "Delete a robot message entry")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_DELETE, description = "delete robot message")
    @Override
    public ResponseEntity<?> delete(RobotMessageRequest request) {
        
        robotMessageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Export Robot Messages", description = "Export robot messages to Excel format")
    @ActionAnnotation(title = I18Consts.I18N_ROBOT_MESSAGE, action = I18Consts.I18N_ACTION_EXPORT, description = "export robot message")
    @Override
    public Object export(RobotMessageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            robotMessageRestService,
            RobotMessageExcel.class,
            "机器人对话记录表",
            "RobotMessage"
        );
    }
    
}
