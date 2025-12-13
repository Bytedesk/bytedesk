/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-12
 * @Description: REST controller for quick buttons
 */
package com.bytedesk.kbase.quick_button;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/quickbutton")
@AllArgsConstructor
public class QuickButtonRestController extends BaseRestController<QuickButtonRequest, QuickButtonRestService> {

    private final QuickButtonRestService quickButtonRestService;

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_READ_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "组织查询", description = "query quick button by org")
    @Override
    public ResponseEntity<?> queryByOrg(QuickButtonRequest request) {
        Page<QuickButtonResponse> page = quickButtonRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_READ_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "用户查询", description = "query quick button by user")
    @Override
    public ResponseEntity<?> queryByUser(QuickButtonRequest request) {
        Page<QuickButtonResponse> page = quickButtonRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_CREATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "新建", description = "create quick button")
    @Override
    public ResponseEntity<?> create(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.create(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "更新", description = "update quick button")
    @Override
    public ResponseEntity<?> update(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_DELETE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "删除", description = "delete quick button")
    @Override
    public ResponseEntity<?> delete(@RequestBody QuickButtonRequest request) {
        quickButtonRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @PreAuthorize(QuickButtonPermissions.HAS_QUICKBUTTON_UPDATE_ANY_LEVEL)
    @ActionAnnotation(title = "快捷按钮", action = "启用", description = "enable quick button")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.enable(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }
}