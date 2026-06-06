package com.bytedesk.call.call_settings;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/call/settings")
@AllArgsConstructor
@Tag(name = "Call Settings Management", description = "APIs for agent call settings managed in call admin")
@Description("Call Settings Management Controller - Agent call settings APIs")
public class CallSettingsRestController extends BaseRestController<CallSettingsRequest, CallSettingsRestService> {

    private final CallSettingsRestService callSettingsRestService;

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query call settings by org")
    @Operation(summary = "Query call settings by organization")
    @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(CallSettingsRequest request) {
        Page<CallSettingsResponse> page = callSettingsRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query call settings by user")
    @Operation(summary = "Query call settings by user")
    // @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(CallSettingsRequest request) {
        Page<CallSettingsResponse> page = callSettingsRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query call settings by uid")
    @Operation(summary = "Query call settings by uid")
    // @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_READ)
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(CallSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(callSettingsRestService.queryByUid(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query call settings by agent uid")
    @Operation(summary = "Query call settings by agent uid")
    // @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_READ)
    @GetMapping("/query/agent")
    public ResponseEntity<?> queryByAgentUid(CallSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(callSettingsRestService.queryByAgentUid(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_CREATE, description = "create call settings")
    @Operation(summary = "Create call settings")
    @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody CallSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(callSettingsRestService.create(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update call settings")
    @Operation(summary = "Update call settings")
    @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody CallSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(callSettingsRestService.update(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_UPDATE, description = "update call settings signed in state")
    @Operation(summary = "Update call settings signed in state")
    @PostMapping("/update/signed-in")
    public ResponseEntity<?> updateSignedIn(@RequestBody CallSettingsRequest request) {
        return ResponseEntity.ok(JsonResult.success(callSettingsRestService.updateSignedIn(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_DELETE, description = "delete call settings")
    @Operation(summary = "Delete call settings")
    @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody CallSettingsRequest request) {
        callSettingsRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_SETTINGS, action = I18Consts.I18N_ACTION_EXPORT, description = "export call settings")
    @Operation(summary = "Export call settings")
    @PreAuthorize(CallSettingsPermissions.HAS_CALL_SETTINGS_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(CallSettingsRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            callSettingsRestService,
            CallSettingsExcel.class,
            "呼叫配置",
            "call-settings"
        );
    }
}
