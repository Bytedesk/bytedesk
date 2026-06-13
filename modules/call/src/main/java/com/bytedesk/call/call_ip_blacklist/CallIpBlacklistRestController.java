package com.bytedesk.call.call_ip_blacklist;

import org.springframework.context.annotation.Description;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/call/ip/blacklist")
@AllArgsConstructor
@Tag(name = "Call IP Blacklist Management", description = "APIs for call source IP blacklist management")
@Description("Call IP Blacklist Management Controller - Call source IP blacklist APIs")
public class CallIpBlacklistRestController extends BaseRestController<CallIpBlacklistRequest, CallIpBlacklistRestService> {

    private final CallIpBlacklistRestService callIpBlacklistRestService;

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query call ip blacklist by org")
    @Operation(summary = "Query call ip blacklist by organization")
    @PreAuthorize(CallIpBlacklistPermissions.HAS_CALL_IP_BLACKLIST_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(CallIpBlacklistRequest request) {
        Page<CallIpBlacklistResponse> page = callIpBlacklistRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query call ip blacklist by user")
    @Operation(summary = "Query call ip blacklist by user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(CallIpBlacklistRequest request) {
        Page<CallIpBlacklistResponse> page = callIpBlacklistRestService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query call ip blacklist by uid")
    @Operation(summary = "Query call ip blacklist by uid")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(CallIpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(callIpBlacklistRestService.queryByUid(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_CREATE, description = "create call ip blacklist")
    @Operation(summary = "Create call ip blacklist")
    @PreAuthorize(CallIpBlacklistPermissions.HAS_CALL_IP_BLACKLIST_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody CallIpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(callIpBlacklistRestService.create(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_UPDATE, description = "update call ip blacklist")
    @Operation(summary = "Update call ip blacklist")
    @PreAuthorize(CallIpBlacklistPermissions.HAS_CALL_IP_BLACKLIST_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody CallIpBlacklistRequest request) {
        return ResponseEntity.ok(JsonResult.success(callIpBlacklistRestService.update(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_DELETE, description = "delete call ip blacklist")
    @Operation(summary = "Delete call ip blacklist")
    @PreAuthorize(CallIpBlacklistPermissions.HAS_CALL_IP_BLACKLIST_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody CallIpBlacklistRequest request) {
        callIpBlacklistRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_CALL_IP_BLACKLIST, action = I18Consts.I18N_ACTION_EXPORT, description = "export call ip blacklist")
    @Operation(summary = "Export call ip blacklist")
    @PreAuthorize(CallIpBlacklistPermissions.HAS_CALL_IP_BLACKLIST_EXPORT)
    @GetMapping("/export")
    @Override
    public Object export(CallIpBlacklistRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            callIpBlacklistRestService,
            CallIpBlacklistExcel.class,
            "IP黑名单",
            "call-ip-blacklist"
        );
    }
}