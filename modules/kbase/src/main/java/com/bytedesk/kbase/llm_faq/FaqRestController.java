/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:46:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

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
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "常见问题管理", description = "常见问题管理相关接口")
@RestController
@RequestMapping("/api/v1/faq")
public class FaqRestController extends BaseRestController<FaqRequest, FaqRestService> {

    private final FaqRestService faqRestService;

    private final FaqElasticService faqElasticService;

    @Autowired(required = false)
    private FaqVectorService faqVectorService;

    public FaqRestController(FaqRestService faqRestService, FaqElasticService faqElasticService) {
        this.faqRestService = faqRestService;
        this.faqElasticService = faqElasticService;
    }

    @Operation(summary = "查询组织下的常见问题", description = "根据组织ID查询常见问题列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = "常见问题", action = "组织查询", description = "query faq by org")
    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的常见问题", description = "根据用户ID查询常见问题列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = "常见问题", action = "用户查询", description = "query faq by user")
    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        
        Page<FaqResponse> page = faqRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定常见问题", description = "根据UID查询常见问题详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = "常见问题", action = "查询详情", description = "query faq by uid")
    @Override
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(faq));
    }

    @Operation(summary = "创建常见问题", description = "创建新的常见问题")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_CREATE)
    @ActionAnnotation(title = "常见问题", action = "新建", description = "create faq")
    @Override
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "更新常见问题", description = "更新常见问题信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "更新", description = "update faq")
    @Override
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "删除常见问题", description = "删除指定的常见问题")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_DELETE)
    @ActionAnnotation(title = "常见问题", action = "删除", description = "delete faq")
    @Override
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        // 删除FAQ的同时，同步删除全文索引与向量索引（向量服务可选）
        faqElasticService.deleteIndexAndSyncStatus(request);
        if (faqVectorService != null) {
            faqVectorService.deleteVectorIndexAndSyncStatus(request);
        }

        faqRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "删除所有常见问题", description = "删除所有常见问题")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_DELETE)
    @ActionAnnotation(title = "常见问题", action = "删除所有", description = "delete faq all")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody FaqRequest request) {

        // 删除全部FAQ前，同步删除全文索引与向量索引，并同步更新状态
        faqElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        if (faqVectorService != null) {
            faqVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
        }

        faqRestService.delateAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "按知识库删除所有FAQ向量索引", description = "按kbUid一键删除当前知识库下FAQ的向量索引，并同步更新FAQ实体vectorStatus")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "删除向量索引(知识库)", description = "delete faq vector index by kbUid")
    @PostMapping("/deleteAllVectorIndexByKbUid")
    public ResponseEntity<?> deleteAllVectorIndexByKbUid(@RequestBody FaqRequest request) {
        if (faqVectorService == null) {
            return ResponseEntity.ok(JsonResult.error("vector store not enabled"));
        }
        var result = faqVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "启用常见问题", description = "启用或禁用常见问题")
    @ApiResponse(responseCode = "200", description = "操作成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "启用", description = "enable faq")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody FaqRequest request) {

        FaqResponse faqResponse = faqRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(faqResponse));
    }

    @Operation(summary = "导出常见问题", description = "导出常见问题数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_EXPORT)
    @ActionAnnotation(title = "常见问题", action = "导出", description = "export faq")
    @GetMapping("/export")
    public Object export(FaqRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            faqRestService,
            FaqExcel.class,
            "常见问题",
            "faq"
        );
    }

    @Operation(summary = "更新常见问题索引", description = "更新常见问题的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "更新索引", description = "update faq index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    @Operation(summary = "删除常见问题索引", description = "删除常见问题在Elasticsearch中的索引，并同步更新FAQ实体索引状态")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "删除索引", description = "delete faq elastic index")
    @PostMapping("/deleteIndex")
    public ResponseEntity<?> deleteIndex(@RequestBody FaqRequest request) {
        Boolean deleted = faqElasticService.deleteIndexAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(deleted));
    }

    @Operation(summary = "同步常见问题索引状态", description = "检查Elasticsearch中是否存在FAQ索引并同步更新FAQ实体elasticStatus")
    @ApiResponse(responseCode = "200", description = "同步成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "同步索引状态", description = "sync faq elastic status")
    @PostMapping("/syncIndexStatus")
    public ResponseEntity<?> syncIndexStatus(@RequestBody FaqRequest request) {
        var faq = faqElasticService.syncElasticStatus(request);
        return ResponseEntity.ok(JsonResult.success(faq.getElasticStatus()));
    }

    @Operation(summary = "批量同步常见问题索引状态", description = "根据知识库kbUid批量检查Elasticsearch中是否存在FAQ索引并同步更新FAQ实体elasticStatus")
    @ApiResponse(responseCode = "200", description = "同步成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "批量同步索引状态", description = "sync faq elastic status by kb")
    @PostMapping("/syncIndexStatusByKbUid")
    public ResponseEntity<?> syncIndexStatusByKbUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.syncElasticStatusByKbUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "知识库一键删除常见问题索引", description = "根据知识库kbUid一键删除Elasticsearch中的FAQ索引，并同步更新FAQ实体elasticStatus")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "知识库删除索引", description = "delete faq elastic index by kb")
    @PostMapping("/deleteAllIndexByKbUid")
    public ResponseEntity<?> deleteAllIndexByKbUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "更新常见问题向量索引", description = "更新常见问题的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "更新向量索引", description = "update faq vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateVectorIndex(request);
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    @Operation(summary = "删除常见问题向量索引", description = "删除常见问题在向量存储中的索引，并同步更新FAQ实体向量索引状态")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "删除向量索引", description = "delete faq vector index")
    @PostMapping("/deleteVectorIndex")
    public ResponseEntity<?> deleteVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            Boolean deleted = faqVectorService.deleteVectorIndexAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(deleted));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "同步常见问题向量状态", description = "检查向量存储中是否存在FAQ向量索引并同步更新FAQ实体vectorStatus")
    @ApiResponse(responseCode = "200", description = "同步成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "同步向量状态", description = "sync faq vector status")
    @PostMapping("/syncVectorStatus")
    public ResponseEntity<?> syncVectorStatus(@RequestBody FaqRequest request) {
        
        if (faqVectorService != null) {
            var faq = faqVectorService.syncVectorStatus(request);
            return ResponseEntity.ok(JsonResult.success(faq.getVectorStatus()));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "查询常见问题全文索引", description = "根据FAQ UID查询Elasticsearch中的索引文档")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = "常见问题", action = "查询全文索引", description = "query faq elastic by uid")
    @PostMapping("/queryElasticByUid")
    public ResponseEntity<?> queryElasticByUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.queryElasticByUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "查询常见问题向量索引", description = "根据FAQ UID查询向量存储中的索引文档")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = "常见问题", action = "查询向量索引", description = "query faq vector by uid")
    @PostMapping("/queryVectorByUid")
    public ResponseEntity<?> queryVectorByUid(@RequestBody FaqRequest request) {
        if (faqVectorService != null) {
            var result = faqVectorService.queryVectorByUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    @Operation(summary = "批量同步常见问题向量状态", description = "根据知识库kbUid批量检查向量存储中是否存在FAQ向量索引并同步更新FAQ实体vectorStatus")
    @ApiResponse(responseCode = "200", description = "同步成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "批量同步向量状态", description = "sync faq vector status by kb")
    @PostMapping("/syncVectorStatusByKbUid")
    public ResponseEntity<?> syncVectorStatusByKbUid(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            var result = faqVectorService.syncVectorStatusByKbUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "更新所有常见问题索引", description = "更新所有常见问题的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "更新所有索引", description = "update all faq index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    @Operation(summary = "更新所有常见问题向量索引", description = "更新所有常见问题的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = "常见问题", action = "更新所有向量索引", description = "update all faq vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateAllVectorIndex(request);
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
    }

}
