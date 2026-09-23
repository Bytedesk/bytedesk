/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21
 * @Description: T0-3 存量联系方式镜像盘点/修复运维端点（仅超级管理员）
 *   POST /api/v1/member/contact/audit  只读盘点不一致清单（dry-run）
 *   POST /api/v1/member/contact/fix    按确认的 memberUid 清单精准修复（before/after 审计）
 */
package com.bytedesk.core.member;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.rbac.role.RolePermissions;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/member/contact")
public class MemberContactAuditRestController {

    private final MemberContactAuditService memberContactAuditService;

    /**
     * 只读盘点 Member 与关联 User 的联系方式不一致清单（dry-run，不修改数据）。
     * 建议先调用本接口确认清单，再按 memberUid 调用 /fix。
     */
    @PostMapping("/audit")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> audit() {
        List<MemberContactAuditItem> items = memberContactAuditService.audit();
        return ResponseEntity.ok(JsonResult.success(items));
    }

    /**
     * 按确认清单修复：仅覆盖 User 侧非空值，User 侧为空的字段跳过不清空；
     * 每条输出 before/after 审计日志。
     */
    @PostMapping("/fix")
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    public ResponseEntity<?> fix(@RequestBody MemberContactFixRequest request) {
        if (request == null || request.getMemberUids() == null || request.getMemberUids().isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("memberUids is required", -1, false));
        }
        List<MemberContactFixResult> results = memberContactAuditService.fix(request.getMemberUids());
        long fixedCount = results.stream().filter(r -> Boolean.TRUE.equals(r.getFixed())).count();
        log.info("member contact fix requested: {} members, fixed: {}",
                request.getMemberUids().size(), fixedCount);
        return ResponseEntity.ok(JsonResult.success(results));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberContactFixRequest {
        /** 待修复的成员 uid 清单（来自 /audit 输出，经管理员确认） */
        private List<String> memberUids;
    }
}
