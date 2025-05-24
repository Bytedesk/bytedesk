/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 10:31:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "访客管理", description = "访客管理相关接口")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/visitor")
public class VisitorRestController extends BaseRestController<VisitorRequest> {

    private final VisitorRestService visitorService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "查询组织下的访客", description = "根据组织ID查询访客列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @Override
    public ResponseEntity<?> queryByOrg(VisitorRequest request) {

        Page<VisitorResponse> page = visitorService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的访客", description = "根据用户ID查询访客列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @Override
    public ResponseEntity<?> queryByUser(VisitorRequest visitorRequest) {
        //
        Page<VisitorResponse> visitorResponse = visitorService.queryByUser(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Operation(summary = "查询指定访客", description = "根据UID查询访客详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @Override
    public ResponseEntity<?> queryByUid(VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorService.queryByUid(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Operation(summary = "创建访客", description = "创建新的访客")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @Override
    public ResponseEntity<?> create(VisitorRequest request) {
        
        VisitorResponse visitorResponse = visitorService.create(request);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Operation(summary = "更新访客", description = "更新访客信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @Override
    public ResponseEntity<?> update(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorService.update(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    // update tagList
    @Operation(summary = "更新访客标签", description = "更新访客的标签列表")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = VisitorResponse.class)))
    @PostMapping("/update/tagList")
    public ResponseEntity<?> updateTagList(@RequestBody VisitorRequest visitorRequest) {

        VisitorResponse visitorResponse = visitorService.updateTagList(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    @Operation(summary = "删除访客", description = "删除指定的访客")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @Override
    public ResponseEntity<?> delete(@RequestBody VisitorRequest visitorRequest) {

        visitorService.delete(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

    @Operation(summary = "导出访客", description = "导出访客数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @Override
    public Object export(VisitorRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            visitorService,
            VisitorExcel.class,
            "访客",
            "visitor"
        );
    }

}
