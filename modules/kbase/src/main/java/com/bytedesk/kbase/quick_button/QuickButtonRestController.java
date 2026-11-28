/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-12
 * @Description: REST controller for quick buttons
 */
package com.bytedesk.kbase.quick_button;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/quickbutton")
@AllArgsConstructor
public class QuickButtonRestController extends BaseRestController<QuickButtonRequest, QuickButtonRestService> {

    private final QuickButtonRestService quickButtonRestService;

    @Operation(summary = "Query Quick Buttons by Organization", description = "Retrieve quick buttons for the current organization")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query quick button by org")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(QuickButtonRequest request) {
        Page<QuickButtonResponse> page = quickButtonRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query Quick Buttons by User", description = "Retrieve quick buttons for the current user")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_READ)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query quick button by user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(QuickButtonRequest request) {
        Page<QuickButtonResponse> page = quickButtonRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Create Quick Buttons", description = "Create a new quick button entry")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_CREATE, description = "create quick button")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.create(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Update Quick Buttons", description = "Update an existing quick button entry")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_UPDATE, description = "update quick button")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.update(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Operation(summary = "Delete Quick Buttons", description = "Delete a quick button entry")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_DELETE, description = "delete quick button")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody QuickButtonRequest request) {
        quickButtonRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "Enable / Disable", description = "Enable or disable quick button entries")
    @PreAuthorize(QuickButtonPermissions.HAS_QUICK_BUTTON_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_QUICK_BUTTON, action = I18Consts.I18N_ACTION_ENABLE, description = "enable quick button")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody QuickButtonRequest request) {
        QuickButtonResponse response = quickButtonRestService.enable(request);
        return ResponseEntity.ok(JsonResult.success(response));
    }
}