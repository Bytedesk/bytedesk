/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-26 17:22:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * 黑名单管理接口
 * 
 * @author Jackning
 * @since 2024-06-27
 */
@RestController
@RequestMapping("/api/v1/black")
@AllArgsConstructor
@Tag(name = "黑名单管理", description = "黑名单增删改查操作")
public class BlackRestController extends BaseRestController<BlackRequest> {

    private final BlackRestService blackRestService;

    /**
     * 根据组织查询黑名单
     * 
     * @param request 查询请求
     * @return 分页黑名单列表
     */
    @Operation(summary = "根据组织查询黑名单", description = "返回当前组织的黑名单列表")
    @Override
    public ResponseEntity<?> queryByOrg(BlackRequest request) {
        
        Page<BlackResponse> page = blackRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 根据用户查询黑名单
     * 
     * @param request 查询请求
     * @return 分页黑名单列表
     */
    @Operation(summary = "根据用户查询黑名单", description = "返回当前用户的黑名单列表")
    @Override
    public ResponseEntity<?> queryByUser(BlackRequest request) {
        
        Page<BlackResponse> page = blackRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    /**
     * 根据UID查询黑名单
     * 
     * @param request 查询请求
     * @return 黑名单详情
     */
    @Operation(summary = "根据UID查询黑名单", description = "根据唯一标识符查询黑名单")
    @Override
    public ResponseEntity<?> queryByUid(BlackRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    /**
     * 查询当前blackUid是否存在于黑名单中
     * 
     * @param request 查询请求
     * @return 黑名单详情
     */
    @Operation(summary = "查询当前blackUid是否存在于黑名单中", description = "查询当前blackUid是否存在于黑名单中")
    @GetMapping("/exists/blackUid")
    public ResponseEntity<?> existsByBlackUid(BlackRequest request) {
        // 
        Boolean existsByBlackUid = blackRestService.existsByBlackUid(request);
        if (existsByBlackUid) {
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.success(false));
    }

    // unblock/BlackUid
    @Operation(summary = "解除黑名单", description = "解除指定的黑名单")
    @PostMapping("/unblock/blackUid")
    public ResponseEntity<?> unblockByBlackUid(@RequestBody BlackRequest request) {
        //
        blackRestService.unblockByBlackUid(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 创建黑名单
     * 
     * @param request 创建请求
     * @return 创建的黑名单
     */
    @Operation(summary = "创建黑名单", description = "创建新的黑名单记录")
    @Override
    public ResponseEntity<?> create(BlackRequest request) {

        BlackResponse response = blackRestService.initVisitor(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 更新黑名单
     * 
     * @param request 更新请求
     * @return 更新后的黑名单
     */
    @Operation(summary = "更新黑名单", description = "更新已存在的黑名单记录")
    @Override
    public ResponseEntity<?> update(BlackRequest request) {

        BlackResponse response = blackRestService.update(request);
        
        return ResponseEntity.ok(JsonResult.success(response));
    }

    /**
     * 删除黑名单
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    @Operation(summary = "删除黑名单", description = "删除指定的黑名单记录")
    @Override
    public ResponseEntity<?> delete(BlackRequest request) {

        blackRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * 导出黑名单列表
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @Operation(summary = "导出黑名单", description = "导出黑名单数据到Excel")
    @Override
    public Object export(BlackRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            blackRestService,
            BlackExcel.class,
            "黑名单",
            "black"
        );
    }
    

}
