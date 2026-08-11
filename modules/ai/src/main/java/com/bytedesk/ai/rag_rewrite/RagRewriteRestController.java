/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强记录查询控制器。
 *   提供 queryByOrg / queryByUid / delete 接口（记录为只读，无 create/update）。
 */
package com.bytedesk.ai.rag_rewrite;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/rag/rewrite")
@AllArgsConstructor
@Tag(name = "RagRewrite Management", description = "RAG Query Rewrite record management APIs")
@Description("RagRewrite Management Controller - Query and analyze RAG rewrite records")
public class RagRewriteRestController extends BaseRestController<RagRewriteRequest, RagRewriteRestService> {

    private final RagRewriteRestService ragRewriteRestService;

    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query rag rewrite records by org")
    @Operation(summary = "Query RagRewrite Records by Organization")
    @PreAuthorize(RagRewritePermissions.HAS_RAG_REWRITE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(RagRewriteRequest request) {
        return ResponseEntity.ok(JsonResult.success(ragRewriteRestService.queryByOrg(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query rag rewrite record by uid")
    @Operation(summary = "Query RagRewrite Record by UID")
    @PreAuthorize(RagRewritePermissions.HAS_RAG_REWRITE_READ)
    @Override
    public ResponseEntity<?> queryByUid(RagRewriteRequest request) {
        return ResponseEntity.ok(JsonResult.success(ragRewriteRestService.queryByUid(request)));
    }

    @ActionAnnotation(title = I18Consts.I18N_ROBOT, action = I18Consts.I18N_ACTION_DELETE, description = "delete rag rewrite record")
    @Operation(summary = "Delete RagRewrite Record")
    @PreAuthorize(RagRewritePermissions.HAS_RAG_REWRITE_DELETE)
    @Override
    public ResponseEntity<?> delete(RagRewriteRequest request) {
        ragRewriteRestService.delete(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}
