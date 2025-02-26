package com.bytedesk.core.rbac.role;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
public class RoleRestController extends BaseRestController<RoleRequest> {

    private final RoleRestService roleService;

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @GetMapping("/query/super")
    public ResponseEntity<?> queryBySuper(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryBySuper(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @Override
    public ResponseEntity<?> queryByUser(RoleRequest request) {
        Page<RoleResponse> roles = roleService.queryByUser(request);
        return ResponseEntity.ok(JsonResult.success(roles));
    }

    @Override
    public ResponseEntity<?> create(RoleRequest request) {
        RoleResponse role = roleService.create(request);
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @Override
    public ResponseEntity<?> update(RoleRequest request) {
        RoleResponse role = roleService.update(request);
        return ResponseEntity.ok(JsonResult.success(role));
    }

    @Override
    public ResponseEntity<?> delete(RoleRequest request) {
        roleService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}